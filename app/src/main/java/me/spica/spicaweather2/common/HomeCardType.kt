package me.spica.spicaweather2.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import me.spica.spicaweather2.view.NowWeatherInfoCard
import me.spica.spicaweather2.view.view_group.AirCardLayout
import me.spica.spicaweather2.view.view_group.DailyWeatherLayout
import me.spica.spicaweather2.view.view_group.DetailsCardsLayout
import me.spica.spicaweather2.view.view_group.HourlyCardLayout
import me.spica.spicaweather2.view.view_group.MinuteWeatherCard
import me.spica.spicaweather2.view.view_group.TipsLayout


enum class HomeCardType(
  val code: Int,
) {
  //    TODAY_DESC(1), // 今天的描述
  MINUTE_WEATHER(2), // 分钟级天气
  NOW_WEATHER(3), // 现在的天气
  HOUR_WEATHER(4), // 小时天气
  DAY_WEATHER(5), // 日级天气
  DETAILS(6), // 详细信息

  //    SUNRISE(6), // 日出日落
  AIR(7),
  TIPS(8), // 生活指数
  FOOTER(9)
  ;

  fun getViewType(context: Context): View =
    when (this) {
//            TODAY_DESC -> TodayDescLayout(context)
      MINUTE_WEATHER -> MinuteWeatherCard(context)
      NOW_WEATHER -> NowWeatherInfoCard(context)
      HOUR_WEATHER -> HourlyCardLayout(context)
      DAY_WEATHER -> DailyWeatherLayout(context)
      DETAILS -> DetailsCardsLayout(context)
//            SUNRISE -> SunriseCardLayout(context)
      AIR -> AirCardLayout(context)
      TIPS -> TipsLayout(context)
      FOOTER -> AppCompatTextView(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        text = HtmlCompat.fromHtml("<font color='#666'>数据来源于</font>&nbsp<b>和风天气<b/>", HtmlCompat.FROM_HTML_MODE_LEGACY)
      }
    }
}
