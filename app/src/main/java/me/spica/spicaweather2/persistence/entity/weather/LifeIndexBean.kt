package me.spica.spicaweather2.persistence.entity.weather

import android.graphics.Color
import androidx.annotation.DrawableRes
import com.squareup.moshi.JsonClass
import me.spica.spicaweather2.R
import me.spica.spicaweather2.network.model.hefeng.index.Daily


@JsonClass(generateAdapter = true)
data class LifeIndexBean(
  val type: Int, // 类型
  val name: String, // 指数名称
  val category: String, // 指数级别说明
  val text: String, // 说明
) {

  companion object {
    const val SPT = 0x01 // 运动指数
    const val CLOTHES = 0x02 // 穿衣指数
    const val AIR = 0x04 // 空气指数
    const val CAR = 0x08 // 洗车指数
    const val UNKNOWN = -1
  }

  fun color(): Int = Color.BLACK

  @DrawableRes
  fun iconRes(): Int {
    when (type) {
      SPT -> {
        return R.drawable.ic_spt
      }
      CLOTHES -> {
        return R.drawable.ic_clothes
      }
      AIR -> {
        return R.drawable.ic_air_index
      }
      CAR -> {
        return R.drawable.ic_clean_car
      }
      UNKNOWN -> {
        return R.drawable.ic_clean_car
      }
    }

    return R.drawable.ic_clean_car
  }

}

/**
 * 目前只展示几个常用参数
 * 穿衣，运动，空气，洗车
 */

fun Daily.toLifeIndexBean(): LifeIndexBean {
  when (type) {
    "1" -> {
      return LifeIndexBean(
        LifeIndexBean.SPT,
        "运动指数",
        category,
        text ?: "无更多描述",
      )
    }
    "3" -> {
      return LifeIndexBean(
        LifeIndexBean.CLOTHES,
        "穿衣指数",
        category,
        text ?: "无更多描述",
      )
    }
    "10" -> {
      return LifeIndexBean(
        LifeIndexBean.AIR,
        "空气污染指数",
        category,
        text ?: "无更多描述",
      )
    }
    "2" -> {
      return LifeIndexBean(
        LifeIndexBean.CAR,
        "洗车指数",
        category,
        text ?: "无更多描述",
      )
    }
  }
  return LifeIndexBean(
    LifeIndexBean.UNKNOWN,
    name,
    category,
    text ?: "无更多描述",
  )
}
