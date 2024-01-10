package me.spica.spicaweather2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;

/**
 * @ClassName SunView
 * @Author Spica2 7
 * @Date 2023/12/7 10:25
 */
public class SunView extends View {
    private Paint mSunPaint;
    private Paint mRayPaint;

    private float mSunRadius;
    private float mSunX;
    private float mSunY;

    private float mRayStartX;
    private float mRayStartY;
    private float mRayEndX;
    private float mRayEndY;


    public SunView(Context context) {
        super(context);
        init();
    }


    public SunView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mSunPaint = new Paint();
        mSunPaint.setColor(Color.YELLOW);
        mSunPaint.setStyle(Paint.Style.FILL);

        mRayPaint = new Paint();
        mRayPaint.setColor(Color.WHITE);
        mRayPaint.setStyle(Paint.Style.STROKE);
        mRayPaint.setStrokeWidth(10);
        mRayPaint.setAntiAlias(true);

        mSunRadius = 200;
    }


    private final Path linePath = new Path();

    private final float[] hsv = new float[3];


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // 绘制太阳
        canvas.drawCircle(mSunX, mSunY, mSunRadius, mSunPaint);

        // 计算光线的颜色和透明度
        float distance = calculateDistance(mRayStartX, mRayStartY, mRayEndX, mRayEndY); // 计算距离

        float angle = calculateAngle(mSunX, mSunY, mRayEndX, mRayEndY); // 计算角度

        Color.colorToHSV(Color.WHITE, hsv);
        hsv[1] *= distance / mSunRadius; // 根据距离调整颜色的饱和度
        hsv[2] *= Math.max(0, Math.cos(Math.toRadians(angle))); // 根据角度调整颜色的明度
        int color = Color.HSVToColor(hsv);
        int alpha = (int) (255 * Math.max(0, Math.cos(Math.toRadians(angle)))); // 根据角度调整透明度
        mRayPaint.setColor(color);
        mRayPaint.setAlpha(alpha);

        // 绘制光线
        Path path = linePath;
        linePath.reset();
        path.moveTo(mRayStartX, mRayStartY);
        path.quadTo((mRayStartX + mRayEndX) / 2, (mRayStartY + mRayEndY) / 2, mRayEndX, mRayEndY);
        canvas.drawPath(path, mRayPaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 计算太阳的位置和半径
        mSunX = w / 2f;
        mSunY = h / 3f;
        mSunRadius = Math.min(w, h) / 6f;

        // 计算光线的位置和方向
        mRayStartX = mSunX;
        mRayStartY = mSunY - mSunRadius;
        mRayEndX = mSunX + mSunRadius;
        mRayEndY = mSunY - mSunRadius * 3;
    }


    // 计算两点之间的距离
    private float calculateDistance(float startX, float startY, float endX, float endY) {
        return (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
    }


    // 计算两点之间的角度
    private float calculateAngle(float centerX, float centerY, float pointX, float pointY) {
        float angle = (float) Math.toDegrees(Math.atan2(pointY - centerY, pointX - centerX));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }
}