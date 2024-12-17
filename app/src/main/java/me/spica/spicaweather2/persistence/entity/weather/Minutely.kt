package me.spica.spicaweather2.persistence.entity.weather

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
data class Minutely(
  @Json(name = "fxTime")
  val fxTime: String,
  @Json(name = "precip")
  val precip: String,
  @Json(name = "type")
  val type: String,
) : Parcelable{
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Minutely

    if (fxTime != other.fxTime) return false
    if (precip != other.precip) return false
    if (type != other.type) return false

    return true
  }

  override fun hashCode(): Int {
    var result = fxTime.hashCode()
    result = 31 * result + precip.hashCode()
    result = 31 * result + type.hashCode()
    return result
  }
}
