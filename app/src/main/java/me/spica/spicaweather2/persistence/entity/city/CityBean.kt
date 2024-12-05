package me.spica.spicaweather2.persistence.entity.city

import android.content.Context
import android.os.Parcelable
import androidx.annotation.WorkerThread
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Calendar

@Parcelize
@Entity(tableName = "t_city")
data class CityBean(
  @PrimaryKey(autoGenerate = false)
  var cityName: String, // 名称
  var sortName: String, // 拼音
  var lon: String, // 经度
  var lat: String, // 纬度
  var sort: Long = Calendar.getInstance().timeInMillis,
) : Parcelable {
  @Ignore
  @IgnoredOnParcel
  var iconId: Int = 100

  @IgnoredOnParcel
  @Ignore
  val sortId: String =
    if (sortName.isNotEmpty()) {
      sortName[0].toString()
    } else {
      "#"
    }

  companion object {
    // 读取配置文件中的所有城市信息
    @JvmStatic
    @WorkerThread
    fun getAllCities(context: Context): List<CityBean> {
      val provinces = arrayListOf<Province>()
      val cityList = arrayListOf<CityBean>()
      val moshi = Moshi.Builder().build()
      val listOfCardsType =
        Types.newParameterizedType(
          List::class.java,
          Province::class.java,
        )
      val jsonAdapter = moshi.adapter<List<Province>>(listOfCardsType)

      provinces.addAll(
        jsonAdapter.fromJson(
          getJsonString(context),
        ) ?: listOf(),
      )

      cityList.addAll(
        provinces
          .map {
            CityBean(
              cityName = it.name,
              sortName =
              PinyinHelper.convertToPinyinString
                (it.name, "", PinyinFormat.WITHOUT_TONE),
              lon = it.log,
              lat = it.lat,
            )
          }.filter {
            it.cityName.isNotEmpty()
          },
      )

      provinces.forEach {
        cityList.addAll(
          it.children.map { city ->
            CityBean(
              cityName = city.name,
              sortName =
              PinyinHelper.convertToPinyinString
                (city.name, "", PinyinFormat.WITHOUT_TONE),
              lon = city.log,
              lat = city.lat,
            )
          },
        )
      }

      cityList.sortBy {
        it.sortName
      }
      return cityList
    }

    @Throws(IOException::class)
    private fun getJsonString(context: Context): String {
      var br: BufferedReader? = null
      val sb = StringBuilder()
      try {
        val manager = context.assets
        br = BufferedReader(InputStreamReader(manager.open("city.json")))
        var line: String?
        while (br.readLine().also { line = it } != null) {
          sb.append(line)
        }
      } catch (e: IOException) {
        e.printStackTrace()
        throw e
      } finally {
        try {
          br?.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }
      }
      return sb.toString()
    }
  }
}
