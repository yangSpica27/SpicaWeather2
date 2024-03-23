package me.spica.spicaweather2.weather_anim_counter

import com.google.fpl.liquidfun.Body
import com.google.fpl.liquidfun.BodyDef
import com.google.fpl.liquidfun.BodyType
import com.google.fpl.liquidfun.FixtureDef
import com.google.fpl.liquidfun.ParticleGroup
import com.google.fpl.liquidfun.ParticleGroupDef
import com.google.fpl.liquidfun.ParticleSystemDef
import com.google.fpl.liquidfun.PolygonShape
import com.google.fpl.liquidfun.Vec2
import com.google.fpl.liquidfun.World
import me.spica.spicaweather2.tools.dp


/**
 * 雨滴粒子管理器
 */
class RainParticleManager {

    private val mProportion = 60f // 模拟世界和view坐标的转化比例
    private val dt = 1f / 30f
    private val velocityIterations = 8
    private val positionIterations = 3
    private val particleIterations = 3

    companion object {
        const val ParticleMaxCount = 2000
    }

    private var mWorldWidth = 0
    private var mWorldHeight = 0
    private var mDensity = .5f

    private var boxWidth: Float = 0f
    private var boxHeight: Float = 0f

    private var isInitOK = false
    private lateinit var world: World

    private val random = kotlin.random.Random.Default


    fun init(width: Int, height: Int) {
        this.mWorldWidth = width
        this.mWorldHeight = height
        boxWidth = mappingView2Body(mWorldWidth * 1f - 32.dp) / 2f
        boxHeight = mappingView2Body(mProportion * 1f)
        world = World(0f, 9.8f)
        createCardBox()
        createRainItemShapeAndDef()
        isInitOK = true
    }


    // 更新粒子的位置
    fun run() {
        if (!isInitOK) return
        synchronized(world) {
            world.step(dt, velocityIterations, positionIterations, particleIterations)
        }
    }


    // 设置背景刚体的y坐标
    fun setBackgroundY(y: Int) {
        if (!isInitOK) return
        synchronized(world) {
            backgroundBody?.setTransform(
                Vec2(
                    boxWidth + mappingView2Body(16.dp), mappingView2Body(y * 1f + 16.dp)
                ), 0f
            )
        }
    }

    private var backgroundBody: Body? = null

    private fun createCardBox() {
        val bodyDef = BodyDef()
        // 创建静止刚体
        bodyDef.type = BodyType.staticBody
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
        bodyDef.position[boxWidth + mappingView2Body(16.dp)] = mappingView2Body(mWorldHeight * 1f) + boxHeight
        val bottomBody: Body = world.createBody(bodyDef) // 创建一个真实的下边 body
        val fixture = bottomBody.createFixture(fixtureDef)
        val body = fixture.body
        backgroundBody = body
    }

    val system: com.google.fpl.liquidfun.ParticleSystem by lazy {
        val psd = ParticleSystemDef()
        psd.dampingStrength = 2.2f
        psd.density = 1f
        psd.maxCount = ParticleMaxCount
        psd.radius = mappingView2Body(1.dp)
        psd.dampingStrength = 1.3f
        psd.pressureStrength = 0.03f
        psd.gravityScale = 1.4f
        psd.repulsiveStrength = 0f
        psd.surfaceTensionPressureStrength = 10.55f
        psd.viscousStrength = 20f
        val ps = world.createParticleSystem(psd)
        psd.delete()
        return@lazy ps
    }


    private val rainItemShape = PolygonShape()

    private val rainItemDef = ParticleGroupDef()

    private fun createRainItemShapeAndDef() {
        rainItemDef.flags = 0
        rainItemDef.groupFlags = 0
        rainItemDef.shape = rainItemShape
        rainItemDef.linearVelocity = Vec2(0f, 9f)
    }


    // 创建雨点
    fun createRainItem(): ParticleGroup {
        val x = (0..mWorldWidth).random(random).toFloat()
        val y = (-3 * mWorldHeight..0).random(random).toFloat()
        val width = (1..2).random(random).dp
        rainItemShape.set(
            arrayOf(
                Vec2(mappingView2Body(x), mappingView2Body(y)),
                Vec2(mappingView2Body(x + width), mappingView2Body(y)),
                Vec2(mappingView2Body(x + width), mappingView2Body(y + 18.dp)),
                Vec2(mappingView2Body(x), mappingView2Body(y + 16.dp)),
            ), 4
        )
        return system.createParticleGroup(rainItemDef)
    }

    // view坐标系转化为模拟世界坐标系
    private fun mappingView2Body(view: Float): Float {
        return view / mProportion
    }

    // 模拟世界坐标系转化为view坐标系
    private fun mappingBody2View(body: Float): Float {
        return body * mProportion
    }


    fun destroy() {
        try {
            rainItemShape.delete()
            rainItemDef.delete()
            system.delete()
            world.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}