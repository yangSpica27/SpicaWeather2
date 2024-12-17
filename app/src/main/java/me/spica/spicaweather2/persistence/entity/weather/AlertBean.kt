package me.spica.spicaweather2.persistence.entity.weather

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AlertBean(
  val title: String,
  val text: String,
  val sender: String,
  val startTime: String,
) : Parcelable{
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AlertBean

    if (title != other.title) return false
    if (text != other.text) return false
    if (sender != other.sender) return false
    if (startTime != other.startTime) return false

    return true
  }

  override fun hashCode(): Int {
    var result = title.hashCode()
    result = 31 * result + text.hashCode()
    result = 31 * result + sender.hashCode()
    result = 31 * result + startTime.hashCode()
    return result
  }
}
