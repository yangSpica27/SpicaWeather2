package me.spica.spicaweather2.weather_anim_counter

import com.google.fpl.liquidfun.Body
import com.google.fpl.liquidfun.Vec2
import me.spica.spicaweather2.tools.dp
import java.util.*

/**
 * 粒子基类
 */
abstract class BaseParticle(
  var id: String = UUID.randomUUID().toString(),
  var x: Float = 0f,
  var y: Float = 0f,
  var body: Body? = null,
  var width: Float = 1.dp / 4,
  var height: Float = 1.dp / 4,
)
