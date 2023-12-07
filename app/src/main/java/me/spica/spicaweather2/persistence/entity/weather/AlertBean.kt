package me.spica.spicaweather2.persistence.entity.weather

import android.graphics.Color
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AlertBean(
    val title: String,
    val description: String,
    val status: String,
    val code: String,
    val source: String
) : Parcelable {

    fun getAlertColor(): Int {
        return if (code.endsWith("00")) {
            Color.BLACK
        } else if (code.endsWith("01")) {
            Color.parseColor("#FF6200EE")
        } else if (code.endsWith("02")) {
            Color.parseColor("#FFD54F")
        } else if (code.endsWith("03")) {
            Color.parseColor("#fa8c16")
        } else if (code.endsWith("04")) {
            Color.RED
        } else {
            Color.BLACK
        }
    }
}
