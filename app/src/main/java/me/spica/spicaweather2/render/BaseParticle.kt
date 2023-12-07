package me.spica.spicaweather2.render

import me.spica.spicaweather2.tools.dp
import org.jbox2d.dynamics.Body
import java.util.UUID

abstract class BaseParticle(
    var id: String = UUID.randomUUID().toString(),
    var x: Float = 0f,
    var y: Float = 0f,
    var body: Body? = null,
    var width: Float = 10.dp,
    var height: Float = 10.dp
)
