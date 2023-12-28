package me.spica.spicaweather2.weather_anim_counter

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import me.spica.spicaweather2.tools.dp
import java.io.Closeable

class RainEffectCounter : Closeable {

    private lateinit var world: World

    private val random = kotlin.random.Random.Default

    /**
     * 迭代频率
     * 迭代速率
     * 迭代次数
     * */
    private val dt = 1f / 30f
    private val velocityIterations = 5
    private val positionIterations = 20

    private val mProportion = 60f // 模拟世界和view坐标的转化比例
    private var mDensity = .5f
    private val mFrictionRatio = 0.0f // 摩擦系数
    private val mRestitutionRatio = 0.3f // 回复系数

    private var mWorldWidth = 0
    private var mWorldHeight = 0


    private var boxWidth: Float = 0f
    private var boxHeight: Float = 0f

    private var isInitOK = false

    fun init(width: Int, height: Int) {
        this.mWorldWidth = width
        this.mWorldHeight = height
        boxWidth = mappingView2Body(mWorldWidth * 1f - 28.dp) / 2f
        boxHeight = mappingView2Body(mProportion * 1f)
        world = World(Vector2(0f, 9.8f), true)
        updateHorizontalBounds()
        isInitOK = true
    }

    private var backgroundBody: Body? = null

    private fun updateHorizontalBounds() {
        val bodyDef = BodyDef()
        // 创建静止刚体
        bodyDef.type = BodyDef.BodyType.StaticBody
        // 定义的形状
        val box = PolygonShape()
        box.setAsBox(boxWidth, boxHeight) // 确定为矩形
        val fixtureDef = FixtureDef()
        fixtureDef.shape = box
        fixtureDef.density = mDensity
        fixtureDef.friction = 0.1f // 摩擦系数
        fixtureDef.restitution = 0.3f // 补偿系数
        fixtureDef.filter.maskBits = 0b01
        fixtureDef.filter.groupIndex = 0b01
        bodyDef.position[boxWidth + mappingView2Body(16.dp)] =
            mappingView2Body(mWorldHeight * 1f)  + boxHeight
        val bottomBody: Body = world.createBody(bodyDef) // 创建一个真实的下边 body
        val fixture = bottomBody.createFixture(fixtureDef)
        val body = fixture.body
        backgroundBody = body
        backgroundBody?.userData = -1
    }

    fun createParticle(view: BaseParticle) {
        synchronized(world) {
            val bodyDef = BodyDef()
            view.x = (0..mWorldWidth).random(random).toFloat()
            view.y = (-3 * mWorldHeight..0).random(random).toFloat()
            bodyDef.type = BodyDef.BodyType.DynamicBody
            bodyDef.position[mappingView2Body(view.x + view.width / 2)] = mappingView2Body(view.y + view.height / 2)
            val shape = CircleShape()
            shape.radius = mappingView2Body(view.width / 2)
            val def = FixtureDef()
            def.shape = shape
            def.density = mDensity
            def.friction = mFrictionRatio
            def.restitution = mRestitutionRatio
            def.filter.maskBits = 0
            def.filter.groupIndex = 0b01
            val body = world.createBody(bodyDef)
            view.body = body
            body.linearVelocity = Vector2(random.nextFloat(), random.nextFloat())
            body.createFixture(def)
            body.linearDamping = 0.6f
            body.userData = 0
        }
    }

    fun run() {
        if (!isInitOK) return
        synchronized(world) {
            world.step(dt, velocityIterations, positionIterations)
        }
    }


    fun setBackgroundY(y: Int) {
        if (!isInitOK) return
        synchronized(world) {
            backgroundBody?.setTransform(
                Vector2(
                    boxWidth + mappingView2Body(16.dp),
                    mappingView2Body(y * 1f + 10.dp)
                ),
                0f
            )
        }
    }

    fun getXy(view: BaseParticle) {
        view.x = getViewX(view)
        val newY = getViewY(view)
        val sameY = newY == view.y
        view.y = newY
        if (view.y > mWorldHeight || view.x < 0 || view.x > mWorldWidth || sameY) {
            view.x = (0..mWorldWidth).random(random).toFloat()

            view.y = (-mWorldHeight..0).random(random).toFloat()

            view.body?.setTransform(
                Vector2(
                    mappingView2Body(view.x),
                    mappingView2Body(view.y)
                ),
                0f
            )
            view.body?.linearVelocity = Vector2(random.nextFloat(), random.nextFloat())
            view.body?.userData = 0
            view.body?.isSleepingAllowed = false
        }
    }

    private fun getViewX(view: BaseParticle): Float {
        val body = view.body
        return if (body != null) {
            mappingBody2View(body.position.x) - view.width / 2f
        } else 0f
    }

    private fun getViewY(view: BaseParticle): Float {
        val body = view.body
        return if (body != null) {
            mappingBody2View(body.position.y) - view.height / 2f
        } else 0f
    }

    private fun mappingView2Body(view: Float): Float {
        return view / mProportion
    }

    private fun mappingBody2View(body: Float): Float {
        return body * mProportion
    }



    override fun close() {
        world.dispose()
    }
}
