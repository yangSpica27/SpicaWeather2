package me.spica.spicaweather2.weather_anim_counter

import com.google.fpl.liquidfun.Body
import me.spica.spicaweather2.tools.dp
import java.util.UUID

/**
 * 粒子基类
 */
abstract class BaseParticle(
    var id: String = UUID.randomUUID().toString(),
    var x: Float = 0f,
    var y: Float = 0f,
    var body: Body? = null,
    var width: Float = 2.dp,
    var height: Float = 2.dp,
)
