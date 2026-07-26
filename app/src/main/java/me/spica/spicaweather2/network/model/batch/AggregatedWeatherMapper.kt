package me.spica.spicaweather2.network.model.batch

import me.spica.spicaweather2.persistence.entity.weather.AirBean
import me.spica.spicaweather2.persistence.entity.weather.AlertBean
import me.spica.spicaweather2.persistence.entity.weather.DailyWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.HourlyWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.LifeIndexBean
import me.spica.spicaweather2.persistence.entity.weather.Minutely
import me.spica.spicaweather2.persistence.entity.weather.NowWeatherBean
import me.spica.spicaweather2.persistence.entity.weather.Weather
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

private val SHANGHAI: ZoneId = ZoneId.of("Asia/Shanghai")

private val OBS_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
private val DAILY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA)
private val HOURLY_PREFIX_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.CHINA)
private val CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA)

/** HourlyWeatherBean.fxTime 模板里写死的时区后缀 */
private const val FIXED_OFFSET = "+08:00"

/** HourlyLineView 的索引下限，见文件头注释 */
private const val MIN_HOURLY = 3

/** MinuteRainView 按 24 格排版（lineWidth = w / 24f - 3.dp） */
private const val MINUTELY_SLOTS = 24

/**
 * 把毫米/小时折算成 0..1 比例时的封顶值（10mm/h 已是大雨）。
 */
private const val PRECIP_CEILING_MM = 10.0

private const val ICON_UNKNOWN = 100
private const val ICON_UNKNOWN_HOURLY = 101

/**
 * @param cityName 必须来自本地 [me.spica.spicaweather2.persistence.entity.city.CityBean]，
 *   不能用服务端返回的 location.name——它是 Weather 表主键，也是 CityWithWeather 关联的唯一依据。
 * @return null 表示这条结果不可用（缺少实时天气），调用方应保留上一次的缓存。
 */
fun AggregatedWeatherData.toWeather(cityName: String): Weather? {
  val current = current ?: return null

  val daily = buildDaily(forecast, current)
  val hourly = buildHourly(current, forecast?.next24Hours)

  return Weather(
    todayWeather = current.toNowWeatherBean(),
    dailyWeather = daily,
    hourlyWeather = hourly,
    lifeIndexes = buildLifeIndexes(airQuality),
    air = airQuality.toAirBean(),
    cityName = cityName,
    descriptionForToday = buildTodayDescription(minutelyPrecip, daily.firstOrNull(), current),
    descriptionForToWeek = null,
    alerts = weatherAlerts.orEmpty().map { it.toAlertBean() },
    minutelies = buildMinutely(minutelyPrecip),
  )
}

// region 实时天气

private fun CurrentWeather.toNowWeatherBean(): NowWeatherBean =
  NowWeatherBean(
    obsTime = formatObsTime(obsTime),
    temp = temperature,
    feelTemp = feelsLike,
    iconId = icon.toIconId(ICON_UNKNOWN),
    // 接口明确标注公里/小时，不做换算
    windSpeed = windSpeed,
    water = humidity,
    // 字段名叫 windPa，实际是气压（百帕）
    windPa = pressure,
    weatherName = condition,
    fxLink = null,
  )

// endregion

// region 每日预报

private fun buildDaily(
  forecast: ForecastSummary?,
  current: CurrentWeather,
): List<DailyWeatherBean> {
  val source =
    forecast?.next7Days?.takeIf { it.isNotEmpty() }
      ?: listOfNotNull(forecast?.today, forecast?.tomorrow).takeIf { it.isNotEmpty() }
      ?: return listOf(current.toFallbackDailyBean())

  val sorted = source.sortedBy { it.date }
  val today = LocalDate.now(SHANGHAI).format(DAILY_DATE_FORMAT)
  // [0] 必须是今天：服务端若只回了明天开始的数据，就把 today 补到头部
  val normalized =
    if (sorted.first().date == today) {
      sorted
    } else {
      listOfNotNull(forecast?.today?.takeIf { it.date == today }) + sorted
    }

  return normalized.map { it.toDailyWeatherBean() }.ifEmpty {
    listOf(current.toFallbackDailyBean())
  }
}

private fun DailyForecast.toDailyWeatherBean(): DailyWeatherBean =
  DailyWeatherBean(
    fxTime = formatDailyDate(date),
    maxTemp = tempMax,
    minTemp = tempMin,
    iconId = dayIcon.toIconId(ICON_UNKNOWN),
    water = humidity,
    // 日预报没有气压字段，且全项目无人读取
    windPa = 0,
    weatherNameDay = dayCondition,
    precip = precipitation.toFloat(),
    sunriseDate = formatClock(sunrise),
    sunsetDate = formatClock(sunset),
    // 新接口没有月相数据
    moonParse = "",
    dayWindDir = windDirDay,
    dayWindSpeed = windSpeedDay.toIntString(),
    nightWindSpeed = windSpeedNight.toIntString(),
    nightWindDir = windDirNight,
    weatherNameNight = nightCondition,
    pressure = null,
    // DailyWeatherBean 内部用 uv.toIntOrNull() 匹配区间，必须是纯数字串
    uv = uvIndex.toString(),
    vis = vis.toIntOrNull0(),
    cloud = cloud.toIntOrNull0().coerceIn(0, 100),
  )

/** 连日预报都没有时的最后兜底，保证 dailyWeather 非空 */
private fun CurrentWeather.toFallbackDailyBean(): DailyWeatherBean =
  DailyWeatherBean(
    fxTime = LocalDate.now(SHANGHAI).format(DAILY_DATE_FORMAT),
    maxTemp = temperature,
    minTemp = temperature,
    iconId = icon.toIconId(ICON_UNKNOWN),
    water = humidity,
    windPa = pressure,
    weatherNameDay = condition,
    precip = precipitation.toFloat(),
    sunriseDate = "",
    sunsetDate = "",
    moonParse = "",
    dayWindDir = windDirectionText,
    dayWindSpeed = windSpeed.toString(),
    nightWindSpeed = windSpeed.toString(),
    nightWindDir = windDirectionText,
    weatherNameNight = condition,
    pressure = null,
    uv = "0",
    vis = visibility,
    cloud = cloudCover.coerceIn(0, 100),
  )

// endregion

// region 逐小时预报

private fun buildHourly(
  current: CurrentWeather,
  source: List<HourlyForecast>?,
): List<HourlyWeatherBean> {
  val nowHour = LocalDateTime.now(SHANGHAI).withMinute(0).withSecond(0).withNano(0)
  val headFxTime = nowHour.format(HOURLY_PREFIX_FORMAT) + FIXED_OFFSET

  // CurrentWeatherLayout 把 [0] 当「此刻」渲染成 170dp 主温度，所以头部必须是实时数据
  val head =
    HourlyWeatherBean(
      fxTime = headFxTime,
      temp = current.temperature,
      iconId = current.icon.toIconId(ICON_UNKNOWN_HOURLY),
      windSpeed = current.windSpeed,
      water = current.humidity,
      windPa = current.pressure,
      weatherName = current.condition,
      // CurrentWeather 没有降水概率，借用最近一个小时的
      pop = source?.firstOrNull()?.popPercent() ?: 0,
    )

  val rest =
    source
      .orEmpty()
      .map { it.toHourlyWeatherBean() }
      // 避免出现两根标签相同的柱子
      .filter { it.fxTime != headFxTime }

  val merged = listOf(head) + rest
  if (merged.size >= MIN_HOURLY) return merged

  // 补齐到 3 条，时间按小时递推，其余沿用最后一条
  val padded = merged.toMutableList()
  var cursor = nowHour
  while (padded.size < MIN_HOURLY) {
    cursor = cursor.plusHours(1)
    val last = padded.last()
    padded.add(last.copy(fxTime = cursor.format(HOURLY_PREFIX_FORMAT) + FIXED_OFFSET))
  }
  return padded
}

private fun HourlyForecast.toHourlyWeatherBean(): HourlyWeatherBean =
  HourlyWeatherBean(
    fxTime = formatHourlyFxTime(time),
    temp = temperature,
    iconId = icon.toIconId(ICON_UNKNOWN_HOURLY),
    windSpeed = windSpeed.roundToInt(),
    water = humidity,
    // 小时预报没有气压字段
    windPa = 0,
    weatherName = condition,
    pop = popPercent(),
  )

/**
 * 接口同时给了 precipProbability(Int) 和 pop(Double)，语义相同、精度不同，
 * 服务端实际填哪个未确认，先取整数字段、为 0 时回退到 pop。
 * HourlyLineView 直接做 pop / 100f 且不夹取，所以这里必须先夹到 0..100。
 */
private fun HourlyForecast.popPercent(): Int =
  (if (precipProbability != 0) precipProbability else pop.roundToInt()).coerceIn(0, 100)

// endregion

// region 空气质量 / 生活指数

private fun AirQualitySummary?.toAirBean(): AirBean =
  AirBean(
    aqi = this?.aqi ?: 0,
    category = this?.category.orEmpty(),
    primary = this?.primaryPollutant.orEmpty(),
    pm10 = this?.pm10?.toFloat() ?: 0f,
    pm2p5 = this?.pm25?.toFloat() ?: 0f,
    // 新接口不再提供气体浓度，AirCardLayout 里对应的三格会显示 0.0
    no2 = 0f,
    so2 = 0f,
    co = 0f,
    o3 = 0f,
    fxLink = null,
  )

/**
 * 新接口没有生活指数。先临时用空气质量凑出空气指数一格，
 * 其余三格由 TipsLayout 自己的 `?: "暂无数据"` 兜底。
 */
private fun buildLifeIndexes(airQuality: AirQualitySummary?): List<LifeIndexBean> {
  if (airQuality == null || airQuality.category.isBlank()) return emptyList()
  return listOf(
    LifeIndexBean(
      type = LifeIndexBean.AIR,
      name = "空气指数",
      category = airQuality.category,
      text = airQuality.healthAdvice,
    ),
  )
}

// endregion

// region 预警 / 分钟级降水

private fun WeatherAlertSummary.toAlertBean(): AlertBean =
  AlertBean(
    title = headline,
    text = listOf(description, instruction).filter { it.isNotBlank() }.joinToString("\n"),
    // 接口没有发布单位字段
    sender = "",
    startTime = issuedTime.ifBlank { effectiveTime },
  )

private fun buildMinutely(summary: MinutelyPrecipSummary?): List<Minutely> {
  val points = summary?.next2Hours.orEmpty()
  if (points.isEmpty()) return emptyList()
  return points.downsampleTo(MINUTELY_SLOTS).map { it.toMinutely() }
}

private fun MinutelyPrecip.toMinutely(): Minutely =
  Minutely(
    fxTime = time,
    precip = String.format(Locale.US, "%.3f", precipitation.toBarFraction()),
    type = type,
  )

/**
 * 降水强度 -> 柱状图高度比例。
 *
 * 用平方根而不是线性：降水量的可感知差异是非线性的，线性刻度会让常见的小雨
 * （实测 0.07~0.43mm）全部塌到 5% 以下看不见。
 * 折算后小雨约 8%~21%，中雨（2.5mm）50%，大雨（10mm）满格。
 */
private fun Double.toBarFraction(): Double {
  if (this <= 0.0) return 0.0
  return sqrt((this / PRECIP_CEILING_MM).coerceIn(0.0, 1.0))
}

/** 取每段的峰值而不是均值——降水图看的是有没有下，不是平均下多少 */
private fun List<MinutelyPrecip>.downsampleTo(slots: Int): List<MinutelyPrecip> {
  if (size <= slots) return this
  val step = size.toDouble() / slots
  return (0 until slots).map { i ->
    val from = (i * step).toInt()
    val to = min(size, ((i + 1) * step).toInt().coerceAtLeast(from + 1))
    subList(from, to).maxByOrNull { it.precipitation } ?: this[from]
  }
}

// endregion

// region 文案

/**
 * NowWeatherInfoCard 直接把它赋给 TextView.text 且不判空，所以必须给出非空文案。
 * minutelyPrecip 恰恰在「不下雨」时为 null，也就是绝大多数时候。
 */
private fun buildTodayDescription(
  minutelyPrecip: MinutelyPrecipSummary?,
  today: DailyWeatherBean?,
  current: CurrentWeather,
): String {
  minutelyPrecip?.summary?.takeIf { it.isNotBlank() }?.let { return it }
  if (today != null) {
    return "今天${today.weatherNameDay}，${today.minTemp}~${today.maxTemp}℃，体感${current.feelsLike}℃"
  }
  return "${current.condition}，${current.temperature}℃，体感${current.feelsLike}℃"
}

// endregion

// region 解析工具

private fun String.toIconId(fallback: Int): Int = trim().toIntOrNull() ?: fallback

/** vis / cloud 是字符串，可能带小数 */
private fun String.toIntOrNull0(): Int = trim().toDoubleOrNull()?.roundToInt() ?: 0

/** 风速字符串统一成整数串，DailyWeatherBean.windSpeed 用 toIntOrNull() 取值 */
private fun String.toIntString(): String = (trim().toDoubleOrNull()?.roundToInt() ?: 0).toString()

/** "2026-01-07T20:48+08:00" -> "2026-01-07 20:48:00" */
private fun formatObsTime(raw: String): String =
  parseToShanghai(raw)?.format(OBS_TIME_FORMAT).orEmpty()

/** 已经是 yyyy-MM-dd 就原样返回 */
private fun formatDailyDate(raw: String): String {
  if (raw.isBlank()) return ""
  runCatching { return LocalDate.parse(raw).format(DAILY_DATE_FORMAT) }
  return parseToShanghai(raw)?.format(DAILY_DATE_FORMAT) ?: raw
}

/**
 * 必须换算到东八区再拼字面量 "+08:00"，见文件头注释。
 */
private fun formatHourlyFxTime(raw: String): String {
  val local = parseToShanghai(raw) ?: return ""
  return local.format(HOURLY_PREFIX_FORMAT) + FIXED_OFFSET
}

/**
 * 日出日落的线上格式没有样例（不像 obsTime 带了注释），可能是完整 ISO-8601，
 * 也可能已经是 HH:mm，所以逐个格式试。DetailsCardsLayout 会原样显示，需要补零。
 */
private fun formatClock(raw: String): String {
  if (raw.isBlank()) return ""
  runCatching { return OffsetDateTime.parse(raw).toLocalTime().format(CLOCK_FORMAT) }
  runCatching { return LocalDateTime.parse(raw).toLocalTime().format(CLOCK_FORMAT) }
  runCatching { return LocalTime.parse(raw).format(CLOCK_FORMAT) }
  Regex("""(\d{1,2}):(\d{2})""").find(raw)?.let { match ->
    val hour = match.groupValues[1].toIntOrNull() ?: return ""
    val minute = match.groupValues[2].toIntOrNull() ?: return ""
    return String.format(Locale.US, "%02d:%02d", hour, minute)
  }
  return ""
}

private fun parseToShanghai(raw: String): LocalDateTime? {
  if (raw.isBlank()) return null
  runCatching { return OffsetDateTime.parse(raw).atZoneSameInstant(SHANGHAI).toLocalDateTime() }
  runCatching { return LocalDateTime.parse(raw) }
  return null
}

// endregion
