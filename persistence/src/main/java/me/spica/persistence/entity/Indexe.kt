package me.spica.persistence.entity


import androidx.annotation.Keep



@Keep
data class Indexe(
    val abbreviation: String? = null,
    val alias: String? = null,
    val content: String? = null,
    val level: String? = null,
    val name: String? = null
)