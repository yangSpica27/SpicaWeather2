package me.spica.spicaweather2.render

import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef
import org.jbox2d.dynamics.World


class RainEffectRender {

    private lateinit var world: World

    private val random = kotlin.random.Random.Default

    /**
     * 迭代频率
     * 迭代速率
     * 迭代次数
     * */
    private val dt = 1f / 60f
    private val velocityIterations = 5
    private val positionIterations = 20


    private val mProportion = 50 // 模拟世界和view坐标的转化比例
    private var mDensity = .5f
    private val mFrictionRatio = 0.2f // 摩擦系数
    private val mRestitutionRatio = 0.1f // 回复系数

    private var mWorldWidth = 0
    private var mWorldHeight = 0

    fun init(width: Int, height: Int) {
        this.mWorldWidth = width
        this.mWorldHeight = height
        world = World(Vec2(0f, 9.8f))
        updateHorizontalBounds()
    }


    private fun updateHorizontalBounds() {
        val bodyDef = BodyDef()
        //创建静止刚体
        bodyDef.type = BodyType.STATIC
        //定义的形状
        val box = PolygonShape()
        val boxWidth: Float = mappingView2Body(mWorldWidth * 1f)
        val boxHeight: Float = mappingView2Body(mProportion * 1f)
        box.setAsBox(boxWidth, boxHeight) //确定为矩形


        val fixtureDef = FixtureDef()
        fixtureDef.shape = box
        fixtureDef.density = mDensity
        fixtureDef.friction = 0.8f //摩擦系数
        fixtureDef.restitution = 0.5f //补偿系数


        bodyDef.position[0f] = mappingView2Body(mWorldHeight * 1f) + boxHeight
        val bottomBody: Body = world.createBody(bodyDef) //创建一个真实的下边 body

        bottomBody.createFixture(fixtureDef)

    }


    fun createParticle(view: BaseParticle) {
        val bodyDef = BodyDef()
        view.x = (0..mWorldWidth).random(random).toFloat()
        view.y = (-3 * mWorldHeight..0).random(random).toFloat()
        bodyDef.type = BodyType.DYNAMIC
        bodyDef.position[mappingView2Body(view.x + view.width / 2)] = mappingView2Body(view.y + view.height / 2)
        val shape = CircleShape()
        shape.radius = mappingView2Body(view.width / 2)
        val def = FixtureDef()
        def.shape = shape
        def.density = mDensity
        def.friction = mFrictionRatio
        def.restitution = mRestitutionRatio
        val body = world.createBody(bodyDef)
        view.body = body
        body.linearVelocity = Vec2(random.nextFloat(), random.nextFloat())
        body.createFixture(def)
        body.linearDamping = 0.5f
    }

    fun run() {
        world.step(dt, velocityIterations, positionIterations)
    }


    fun getXy(view: BaseParticle) {
        view.x = getViewX(view)
        view.y = getViewY(view)
        if (view.y > mWorldHeight || view.x < 0 || view.x > mWorldWidth) {
            view.x = (0..mWorldWidth).random(random).toFloat()

            view.y = (-mWorldHeight..0).random(random).toFloat()

            view.body?.setTransform(
                Vec2(
                    mappingView2Body(view.x),
                    mappingView2Body(view.y)
                ),
                0f
            )
            view.body?.linearVelocity = Vec2(random.nextFloat(), random.nextFloat())
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
}