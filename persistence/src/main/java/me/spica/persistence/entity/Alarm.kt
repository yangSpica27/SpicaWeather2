package me.spica.persistence.entity


import androidx.annotation.Keep


@Keep
data class Alarm(
    val alarmContent: String? = null,
    val alarmDesc: String? = null,
    val alarmId: String? = null,
    val alarmLevelNo: String? = null,
    val alarmLevelNoDesc: String? = null,
    val alarmType: String? = null,
    val alarmTypeDesc: String? = null,
    val precaution: String? = null,
    val publishTime: String? = null
)