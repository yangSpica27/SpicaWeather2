package me.spica.spicaweather2.render

import com.badlogic.gdx.physics.box2d.Body
import me.spica.spicaweather2.tools.dp
import java.util.UUID

abstract class BaseParticle(
    var id: String = UUID.randomUUID().toString(),
    var x: Float = 0f,
    var y: Float = 0f,
    var body: Body? = null,
    var width: Float = 4.dp,
    var height: Float = 4.dp
)
