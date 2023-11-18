package me.spica.persistence.entity


import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "mz_city")
@Keep
data class MzCity(
    @PrimaryKey(autoGenerate = true)
    val areaid: Int? = null,
    val countyname: String? = null
)