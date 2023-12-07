package me.spica.spicaweather2.view.weather_drawable

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat

private const val mXMoveDistance = 40.0
private const val mYMoveDistance = 40.0

class TranslationDrawable(private val context: Context) : WeatherDrawable(), SensorEventListener {

    private var mCenterX = 0
    private var mCenterY = 0

    fun ready(width: Int, height: Int) {
        mCenterX = width / 2
        mCenterY = height / 2

        if (sensorManager == null) {
            sensorManager = ContextCompat.getSystemService(context, SensorManager::class.java)
        }

        sensorManager?.registerListener(
            this, sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME
        )

        sensorManager?.registerListener(
            this, sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME
        )
    }

    private var mAcceleValues: FloatArray? = null

    private var mMageneticValues: FloatArray? = null

    private var lastX = 0f

    private var lastY = 0f

    private var scrollX = 0f

    private var scrollY = 0f

    private var currentX = 0f

    private var currentY = 0f

    private val animation = ObjectAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
            currentX = (it.animatedValue as Float) * (scrollX - lastX) + lastX
            currentY = (it.animatedValue as Float) * (scrollY - lastY) + lastY
        }
        duration = 150
    }

    private val mDegreeYMin = -20.0
    private val mDegreeYMax = 20.0

    private val mDegreeXMin = -20.0
    private val mDegreeXMax = 20.0

    private val MAX_ROTATE_DEGREE = .4f
    private val mMatrix = Matrix()
    private val mCamera = Camera()

//    private fun rotateCanvas(canvas: Canvas, mCanvasRotateX: Float, mCanvasRotateY: Float) {
//        mMatrix.reset()
//        mCamera.save()
//        mMatrix.preTranslate(-mCenterX.toFloat(), -mCenterY.toFloat())
//        mMatrix.postTranslate(mCenterX.toFloat(), mCenterY.toFloat())
//        mMatrix.postTranslate
//        canvas.concat(mMatrix)
//    }

    // 方向传感器
    private var sensorManager: SensorManager? = null

    fun cancel() {
        sensorManager?.unregisterListener(this)
        sensorManager = null
    }

    override fun doOnDraw(canvas: Canvas, width: Int, height: Int) {
//        rotateCanvas(canvas, currentX / 100, currentX / 100)
        mMatrix.reset()
        mCamera.save()
        mMatrix.setTranslate(-currentX, -currentY)
        canvas.concat(mMatrix)
    }

    override fun onSensorChanged(sv: SensorEvent) {

        if (sv.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mAcceleValues = sv.values
        }
        if (sv.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mMageneticValues = sv.values
        }
        val values = FloatArray(3)
        val R = FloatArray(9)
        if (mMageneticValues != null && mAcceleValues != null) {
            SensorManager.getRotationMatrix(R, null, mAcceleValues, mMageneticValues)
        }

        if (animation.isRunning) return

        SensorManager.getOrientation(R, values)
        // x轴的偏转角度
        values[1] = Math.toDegrees(values[1].toDouble()).toFloat()
        // y轴的偏转角度
        values[2] = Math.toDegrees(values[2].toDouble()).toFloat()
        val degreeX = values[1].toDouble()
        val degreeY = values[2].toDouble()

        lastX = scrollX

        lastY = scrollY

        if (degreeY <= 0 && degreeY > mDegreeYMin) {
            scrollX = (degreeY / Math.abs(mDegreeYMin) * mXMoveDistance * 1f).toFloat()
        } else if (degreeY > 0 && degreeY < mDegreeYMax) {
            scrollX = (degreeY / Math.abs(mDegreeYMax) * mXMoveDistance * 1f).toFloat()
        }
        if (degreeX <= 0 && degreeX > mDegreeXMin) {
            scrollY = (degreeX / Math.abs(mDegreeXMin) * mYMoveDistance * 1f).toFloat()
        } else if (degreeX > 0 && degreeX < mDegreeXMax) {
            scrollY = (degreeX / Math.abs(mDegreeXMax) * mYMoveDistance * 1f).toFloat()
        }
        animation.start()
    }

    override fun onAccuracyChanged(a: Sensor, b: Int) = Unit
}
