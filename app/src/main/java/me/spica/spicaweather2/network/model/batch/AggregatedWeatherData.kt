package me.spica.spicaweather2.network.model.batch

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BatchWeatherList(
  val results: List<BatchWeatherResult> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class BatchWeatherResult(
  val locationId: String = "",
  val success: Boolean = false,
  val data: AggregatedWeatherData? = null,
  val error: String? = null,
)

/**
 * 聚合的天气数据
 * 整合了实时天气、未来预报、分钟级降水、空气质量和预警信息
 */
@JsonClass(generateAdapter = true)
data class AggregatedWeatherData(
  /** 数据生成时间，形如 2026-01-07T20:57:13.365641330（不带时区） */
  val generatedAt: String = "",
  val location: LocationInfo? = null,
  val current: CurrentWeather? = null,
  val forecast: ForecastSummary? = null,
  val minutelyPrecip: MinutelyPrecipSummary? = null,
  val airQuality: AirQualitySummary? = null,
  val weatherAlerts: List<WeatherAlertSummary>? = null,
)

@JsonClass(generateAdapter = true)
data class LocationInfo(
  val name: String = "",
  val latitude: String = "",
  val longitude: String = "",
)

/**
 * 实时天气
 */
@JsonClass(generateAdapter = true)
data class CurrentWeather(
  /** 观测时间，形如 2026-01-07T20:48+08:00 */
  val obsTime: String = "",
  /** 温度（摄氏度） */
  val temperature: Int = 0,
  /** 体感温度（摄氏度） */
  val feelsLike: Int = 0,
  /** 天气状况文字 */
  val condition: String = "",
  /** 天气图标代码（和风编码，字符串形式） */
  val icon: String = "",
  /** 相对湿度（百分比） */
  val humidity: Int = 0,
  /** 降水量（毫米） */
  val precipitation: Double = 0.0,
  /** 大气压强（百帕） */
  val pressure: Int = 0,
  /** 能见度（公里） */
  val visibility: Int = 0,
  /** 风向（角度） */
  val windDirection: Int = 0,
  /** 风向文字 */
  val windDirectionText: String = "",
  /** 风力等级 */
  val windScale: String = "",
  /** 风速（公里/小时） */
  val windSpeed: Int = 0,
  /** 云量（百分比） */
  val cloudCover: Int = 0,
)

@JsonClass(generateAdapter = true)
data class ForecastSummary(
  val today: DailyForecast? = null,
  val tomorrow: DailyForecast? = null,
  /** 未来7天预报（包含今天和明天） */
  val next7Days: List<DailyForecast>? = null,
  /** 未来24小时预报 */
  val next24Hours: List<HourlyForecast>? = null,
)

@JsonClass(generateAdapter = true)
data class DailyForecast(
  /** 预报日期，形如 2026-01-08 */
  val date: String = "",
  val tempMax: Int = 0,
  val tempMin: Int = 0,
  val dayCondition: String = "",
  val dayIcon: String = "",
  val nightCondition: String = "",
  val nightIcon: String = "",
  /**
   * 上游 KDoc 写的是「降水概率（百分比）」，但字段名、Double 类型以及另外两处同名的
   * precipitation 都指向毫米。和风的日预报本身也没有概率字段，倾向于是注释复制粘贴出的错。
   * 需要抓一次真实响应确认：晴天出现两位数就说明是百分比。
   */
  val precipitation: Double = 0.0,
  val humidity: Int = 0,
  val uvIndex: Int = 0,
  /** 日出时间，格式未确认（可能是 HH:mm，也可能是完整 ISO-8601） */
  val sunrise: String = "",
  val sunset: String = "",
  /** 能见度，单位未确认（按公里处理） */
  val vis: String = "",
  /** 云量（百分比） */
  val cloud: String = "",
  val wind360Day: Double = 0.0,
  val wind360Night: Double = 0.0,
  val windDirDay: String = "",
  val windDirNight: String = "",
  val windSpeedDay: String = "",
  val windSpeedNight: String = "",
  val windScaleDay: String = "",
  val windScaleNight: String = "",
)

@JsonClass(generateAdapter = true)
data class HourlyForecast(
  /** 预报时间，形如 2026-01-08T08:00+08:00 */
  val time: String = "",
  val temperature: Int = 0,
  val condition: String = "",
  val icon: String = "",
  /** 降水概率（百分比） */
  val precipProbability: Int = 0,
  /** 降水量（毫米） */
  val precipitation: Double = 0.0,
  val windDirection: String = "",
  val windScale: String = "",
  val humidity: Int = 0,
  val wind360: Double = 0.0,
  /** 降水概率，与 precipProbability 同义但精度不同，服务端实际填哪个未确认 */
  val pop: Double = 0.0,
  val windSpeed: Double = 0.0,
)

@JsonClass(generateAdapter = true)
data class MinutelyPrecipSummary(
  /** 降水摘要文字，例如「未来两小时不会下雨」 */
  val summary: String = "",
  val isPrecipitating: Boolean = false,
  /** rain / snow */
  val precipType: String? = null,
  /** 当前降水强度（毫米/小时） */
  val currentIntensity: Double = 0.0,
  val next2Hours: List<MinutelyPrecip>? = null,
)

@JsonClass(generateAdapter = true)
data class MinutelyPrecip(
  /** 形如 2026-01-08T08:00+08:00 */
  val time: String = "",
  /** 降水强度（毫米/小时） */
  val precipitation: Double = 0.0,
  val type: String = "",
)

@JsonClass(generateAdapter = true)
data class AirQualitySummary(
  /** AQI 值（美国标准） */
  val aqi: Int = 0,
  /** 1-优 2-良 3-轻度污染 4-中度污染 5-重度污染 6-严重污染 */
  val level: Int = 0,
  val category: String = "",
  val primaryPollutant: String = "",
  val primaryPollutantName: String = "",
  val healthEffect: String = "",
  val healthAdvice: String = "",
  /** PM2.5 浓度（μg/m³） */
  val pm25: Double? = null,
  /** PM10 浓度（μg/m³） */
  val pm10: Double? = null,
)

@JsonClass(generateAdapter = true)
data class WeatherAlertSummary(
  val id: String = "",
  val headline: String = "",
  val eventType: String = "",
  val eventCode: String = "",
  val severity: String = "",
  val colorCode: String = "",
  val description: String = "",
  val instruction: String = "",
  val issuedTime: String = "",
  val effectiveTime: String = "",
  val expireTime: String = "",
)
