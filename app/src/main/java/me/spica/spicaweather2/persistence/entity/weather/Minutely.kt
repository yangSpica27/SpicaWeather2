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
) : Parcelable
