package me.spica.spicaweather2.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.view.drawToBitmap
import me.spica.spicaweather2.tools.BlurBitmapUtil
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.random.Random

// 前景玻璃效果
class RainDropForegroundView : View {


  constructor(ctx: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : super(
    ctx,
    attrs,
    defStyle
  )

  private lateinit var parentView: View

  private val mThreadPool: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

  init {
    post {
      staticDrops.clear()
      val xCell = width / 30
      val yCell = height / 30
      for (x in 2 until 27) {
        val x = x * xCell - xCell / 2 + xCell * random.nextDouble()
        for (y in 20..28) {
          val y = y * yCell - yCell / 2 + yCell * random.nextDouble()
          staticDrops.add(StaticDrop(x.toInt(), y.toInt(), 20 * random.nextDouble().toFloat()))
        }
      }
      mThreadPool.scheduleWithFixedDelay(
        {
          if (bgBitmap?.isRecycled == false) {
            bgBitmap?.recycle()
          }
          bgBitmap = parentView.drawToBitmap()
          blurBitmap = getFlipBitmap(bgBitmap?.blur(20))
          if (blurBitmap != null) {
            staticDropPaint.shader =
              BitmapShader(blurBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
          }
          staticDrops.forEach { drop ->
            drop.next()
          }
          postInvalidateOnAnimation()
        }, 16, 16, TimeUnit.MILLISECONDS
      )
    }
  }

  fun bindParentView(parentView: View) {
    this.parentView = parentView
  }


  // 背后内容背景
  private var bgBitmap: Bitmap? = null


  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    staticDrops.forEach {
      canvas.drawCircle(it.x * 1f, it.currentY * 1f, it.currentRadius, staticDropPaint)
    }
  }


  // 模糊后的图片
  private var blurBitmap: Bitmap? = null


  private val staticDrops = mutableListOf<StaticDrop>()

  private val random = Random(System.currentTimeMillis())

  private val staticDropPaint = Paint()


  fun Bitmap.blur(radius: Int = 10): Bitmap {
    return BlurBitmapUtil.blurBitmap(context, this@blur, radius.toFloat())
//    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//      val imageReader = ImageReader.newInstance(
//        width, height,
//        PixelFormat.RGBA_8888, 1,
//        HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
//      )
//      val renderNode = RenderNode("blur")
//      val hardwareRenderer = HardwareRenderer()
//      hardwareRenderer.setSurface(imageReader.surface)
//      hardwareRenderer.setContentRoot(renderNode)
//      renderNode.setPosition(0, 0, width, height)
//      renderNode.setRenderEffect(
//        RenderEffect.createBlurEffect(
//          radius.toFloat(),
//          radius.toFloat(),
//          Shader.TileMode.CLAMP
//        )
//      )
//      val canvas = renderNode.beginRecording(width, height)
//      canvas.drawBitmap(this@blur, 0f, 0f, null)
//      renderNode.endRecording()
//      hardwareRenderer.createRenderRequest()
//        .setWaitForPresent(true)
//        .syncAndDraw()
//      val image = imageReader.acquireNextImage() ?: throw RuntimeException("No Image")
//      val hardwareBuffer = image.hardwareBuffer ?: throw RuntimeException("No HardwareBuffer")
//      val bitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
//        ?: throw RuntimeException("Create Bitmap Failed")
//      hardwareBuffer.close()
//      image.close()
//      bitmap
//    } else {
//      BlurBitmapUtil.blurBitmap(context, this@blur, radius.toFloat())
//    }
  }


  data class StaticDrop(
    // 摆动幅度
    val x: Int,
    val y: Int,
    val radius: Float,
  ) {

    var currentRadius: Float = 0f
    var currentY: Float = y * 1f

    fun next() {
      currentRadius = if (currentRadius != 0f) {
        currentY += (currentRadius - 5 / 120f) * 2
        (currentRadius - 5 / 120f).coerceIn(0f, radius * 1f)
      } else {
        currentY = y *1f
        radius * 1f
      }
    }
  }

  private fun getFlipBitmap(bitmap: Bitmap?): Bitmap? {
    if (bitmap == null) return null
    val matrix = Matrix()
    matrix.postScale(1.2f, 1.2f, bitmap.getWidth() / 2f, bitmap.getHeight() * .5f)
    // 创建并返回旋转后的位图对象
    return Bitmap.createBitmap(
      bitmap, 0, 0, bitmap.getWidth(),
      bitmap.getHeight(), matrix, false
    )
  }

}