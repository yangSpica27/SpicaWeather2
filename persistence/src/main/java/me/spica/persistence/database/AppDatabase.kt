package me.spica.persistence.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.spica.persistence.dao.WeatherDao
import me.spica.persistence.entity.Weather

@Database(entities = [Weather::class], version = 1, exportSchema = false)
abstract class AppDatabase :RoomDatabase(){

    abstract fun weatherDao(): WeatherDao

}