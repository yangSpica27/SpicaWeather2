package me.spica.spicaweather2.effect.rain

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World


class RainEffectListener : ApplicationListener {

    private var world: World? = null

    private var box2DDebugRenderer: Box2DDebugRenderer? = null
    private var body: Body? = null
    private var camera: OrthographicCamera? = null

    private val SCENE_WIDTH = 12.8f // 13 metres wide

    private val SCENE_HEIGHT = 7.2f // 7 metres high


    override fun create() {


        world = World(Vector2(0.0f, -9.8f), true)
        box2DDebugRenderer = Box2DDebugRenderer()

    }

    override fun resize(width: Int, height: Int) {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(SCENE_WIDTH / 2, SCENE_HEIGHT / 2)
        val shape = PolygonShape()
        shape.setAsBox(0.5f, 0.5f)
        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape;
        fixtureDef.density = 2f
        fixtureDef.restitution = 1.0f

        body?.createFixture(fixtureDef)
        shape.dispose()

        camera = OrthographicCamera(SCENE_WIDTH, SCENE_HEIGHT)
        camera?.let { camera ->
            camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)
            camera.update()
        }
        createGround()
    }


    private fun createGround() {
        val halfGroundWidth = SCENE_WIDTH
        val halfGroundHeight = 0.5f // 0.5 * 2 = 1meter
        val groundBodyDef = BodyDef()
        groundBodyDef.position.set(halfGroundWidth * 0.5f, halfGroundHeight);
        val groundBody = world!!.createBody(groundBodyDef)
        val groundBox = PolygonShape()
        groundBox.setAsBox(halfGroundWidth * 0.5f, halfGroundHeight)
        groundBody.createFixture(groundBox, 1.0f);
        groundBox.dispose()
    }


    override fun render() {
        world?.step(1/45f, 6, 2);

        Gdx.gl.glClearColor(0.39f, 0.58f, 0.92f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        box2DDebugRenderer?.render(world, camera?.combined)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun dispose() {
        box2DDebugRenderer?.dispose();
    }


}