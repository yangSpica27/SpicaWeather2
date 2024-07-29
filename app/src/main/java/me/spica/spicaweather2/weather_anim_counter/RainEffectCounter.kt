package me.spica.spicaweather2.weather_anim_counter

import com.google.fpl.liquidfun.Body
import com.google.fpl.liquidfun.BodyDef
import com.google.fpl.liquidfun.BodyType
import com.google.fpl.liquidfun.CircleShape
import com.google.fpl.liquidfun.FixtureDef
import com.google.fpl.liquidfun.PolygonShape
import com.google.fpl.liquidfun.Vec2
import com.google.fpl.liquidfun.World
import me.spica.spicaweather2.tools.dp

class RainEffectCounter {
    private lateinit var world: World

    private val random = kotlin.random.Random.Default

    /**
     * 迭代频率
     * 迭代速率
     * 迭代次数
     * */
    private val dt = 1f / 30f
    private val velocityIterations = 8
    private val positionIterations = 3
    private val particleIterations = 3

    private val mProportion = 60f // 模拟世界和view坐标的转化比例
    private var mDensity = .5f
    private val mFrictionRatio = 0.0f // 摩擦系数
    private val mRestitutionRatio = 0.3f // 回复系数

    private var mWorldWidth = 0
    private var mWorldHeight = 0

    private var boxWidth: Float = 0f
    private var boxHeight: Float = 0f

    private var isInitOK = false

    // 初始化
    fun init(
        width: Int,
        height: Int,
    ) {
        this.mWorldWidth = width
        this.mWorldHeight = height
        boxWidth = mappingView2Body(mWorldWidth * 1f - 28.dp) / 2f
        boxHeight = mappingView2Body(mProportion * 1f)
        world = World(0f, 9.8f)
        updateHorizontalBounds()
        isInitOK = true
    }

    private var backgroundBody: Body? = null

    private fun updateHorizontalBounds() {
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
        bodyDef.position[boxWidth + mappingView2Body(16.dp)] =
            mappingView2Body(mWorldHeight * 1f) + boxHeight
        val bottomBody: Body = world.createBody(bodyDef) // 创建一个真实的下边 body
        val fixture = bottomBody.createFixture(fixtureDef)
        val body = fixture.body
        backgroundBody = body
        fixtureDef.delete()
        box.delete()
        bodyDef.delete()
    }

    // 创建粒子
    fun createParticle(view: BaseParticle) {
        synchronized(world) {
            val bodyDef = BodyDef()
            view.x = (0..mWorldWidth).random(random).toFloat()
            view.y = (-3 * mWorldHeight..0).random(random).toFloat()
            bodyDef.type = BodyType.dynamicBody
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
            body.linearVelocity = Vec2(random.nextFloat(), random.nextFloat())
            body.createFixture(def)
            body.linearDamping = 0.6f
            def.delete()
            shape.delete()
            bodyDef.delete()
        }
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
                    boxWidth + mappingView2Body(16.dp),
                    mappingView2Body(y * 1f + 10.dp),
                ),
                0f,
            )
        }
    }

    // 获取粒子的x,y坐标
    fun getXy(view: BaseParticle) {
        view.x = getViewX(view)
        val newY = getViewY(view)
        val sameY = newY == view.y
        view.y = newY
        if (view.y > mWorldHeight || view.x < 0 || view.x > mWorldWidth || sameY) {
            view.x = (0..mWorldWidth).random(random).toFloat()

            view.y = (-mWorldHeight..0).random(random).toFloat()

            view.body?.setTransform(
                Vec2(
                    mappingView2Body(view.x),
                    mappingView2Body(view.y),
                ),
                0f,
            )
            view.body?.linearVelocity = Vec2(random.nextFloat(), random.nextFloat())
            view.body?.isSleepingAllowed = false
        }
    }

    // 获取粒子在view坐标系的x坐标
    private fun getViewX(view: BaseParticle): Float {
        val body = view.body
        return if (body != null) {
            mappingBody2View(body.position.x) - view.width / 2f
        } else {
            0f
        }
    }

    // 获取粒子在view坐标系的y坐标
    private fun getViewY(view: BaseParticle): Float {
        val body = view.body
        return if (body != null) {
            mappingBody2View(body.position.y) - view.height / 2f
        } else {
            0f
        }
    }

    // view坐标系转化为模拟世界坐标系
    private fun mappingView2Body(view: Float): Float = view / mProportion

    // 模拟世界坐标系转化为view坐标系
    private fun mappingBody2View(body: Float): Float = body * mProportion

    fun destroy() {
        world.delete()
    }
}
