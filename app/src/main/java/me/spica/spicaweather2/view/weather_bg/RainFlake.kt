package me.spica.spicaweather2.view.weather_bg;

import android.graphics.Canvas
import android.graphics.Paint


/**
 *  雨滴单元
 */
// 雨滴的移動速度
private const val INCREMENT_LOWER: Float = 10f
private const val INCREMENT_UPPER = 30f

// 雨滴的大小
private const val FLAKE_SIZE_LOWER = 2f
private const val FLAKE_SIZE_UPPER = 4f

class RainFlake(// 雨滴
    private var mRandom: RainRandomGenerator,// 雨滴的速度
     var mLine: Line,
    private var mIncrement: Float,// 雨滴的大小
    private var mFlakeSize: Float,// 画笔
    private var mPaint: Paint
) {


    companion object {
        //生成雨滴
        fun create(width: Int, height: Int, paint: Paint): RainFlake {
            val random = RainRandomGenerator()
            val nline: IntArray = random.getLine(width, height)
            val line = Line(nline[0], nline[1], nline[2], nline[3])
            val increment: Float = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER)
            val flakeSize: Float = random.getRandom(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER)
            return RainFlake(random, line, increment, flakeSize, paint)
        }

    }

    // 繪製雨滴
    fun draw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        drawLine(canvas, width, height)
    }

    // 计算下落
    fun calculation(width: Int, height: Int) {
        mPaint.strokeWidth = mFlakeSize
        //y是豎直方向，就是下落
        val y1: Double = mLine.y1 + mIncrement * Math.sin(1.5)
        val y2: Double = mLine.y2 + mIncrement * Math.sin(1.5)

        //這個是設置雨滴位置，如果在很短時間內刷新一次，就是連起來的動畫效果
        mLine.set(mLine.x1, y1.toInt(), mLine.x2, y2.toInt())
        if (!isInsideLine(height)) {
            resetLine(width, height)
        }
    }

    // 仅绘制
    fun onlyDraw(canvas: Canvas) {
        //設置線寬
        mPaint.strokeWidth = mFlakeSize
        canvas.drawLine(mLine.x1 * 1f, mLine.y1 * 1f, mLine.x2 * 1f, mLine.y2 * 1f, mPaint)
    }

    /**
     * 改成線條，類似於雨滴效果
     * @param canvas
     * @param width
     * @param height
     */
    private fun drawLine(canvas: Canvas, width: Int, height: Int) {
        //設置線寬
        mPaint.strokeWidth = mFlakeSize
        //y是豎直方向，就是下落
        val y1: Double = mLine.y1 + mIncrement * Math.sin(1.5)
        val y2: Double = mLine.y2 + mIncrement * Math.sin(1.5)

        //這個是設置雨滴位置，如果在很短時間內刷新一次，就是連起來的動畫效果
        mLine.set(mLine.x1, y1.toInt(), mLine.x2, y2.toInt())
        if (!isInsideLine(height)) {
            resetLine(width, height)
        }
        canvas.drawLine(mLine.x1 * 1f, mLine.y1 * 1f, mLine.x2 * 1f, mLine.y2 * 1f, mPaint)
    }

    // 判斷是否在其中
    private fun isInsideLine(height: Int): Boolean {
        return mLine.y1 < height && mLine.y2 < height
    }

    // 重置雨滴
    private fun resetLine(width: Int, height: Int) {
        val nline: IntArray = mRandom.getLine(width, height)
        mLine.x1 = nline[0]
        mLine.y1 = nline[1]
        mLine.x2 = nline[2]
        mLine.y2 = nline[3]
    }
}