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
    val startTime: String
) : Parcelable
