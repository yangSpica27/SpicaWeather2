package me.spica.spicaweather2.persistence.entity.city

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "t_city")
data class CityBean(
  @PrimaryKey(autoGenerate = false)
  var cityName: String, // 名称
  var sortName: String, // 拼音
  var lon: String, // 经度
  var lat: String, // 纬度
  var isSelected: Boolean = false,
) : Parcelable {

  @Ignore
  @IgnoredOnParcel
  var iconId: Int = 100

  @IgnoredOnParcel
  @Ignore
  val sortId: String = if (sortName.isNotEmpty()) {
    sortName[0].toString()
  } else {
    "#"
  }
}
