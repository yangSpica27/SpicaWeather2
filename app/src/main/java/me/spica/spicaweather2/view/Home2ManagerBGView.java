package me.spica.spicaweather2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * @ClassName Home2ManagerBGView
 * @Author Spica2 7
 * @Date 2023/11/22 16:43
 */

public class Home2ManagerBGView extends View {
    private final Paint a;
    private LinearGradient b;
    private int f;
    private int g;
    private int[] h;
    private float[] i;

    public Home2ManagerBGView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    private void a() {
        this.a.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void b() {
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, this.g, this.h, this.i, Shader.TileMode.CLAMP);
        this.b = linearGradient;
        this.a.setShader(linearGradient);
    }

    public void c(int[] iArr, float[] fArr) {
        int[] iArr2 = this.h;
        iArr2[0] = iArr[1];
        iArr2[1] = iArr[2];
        iArr2[2] = iArr[3];
        float[] fArr2 = this.i;
        fArr2[0] = 0.0f;
        fArr2[1] = fArr[2];
        fArr2[2] = 1.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0.0f, 0.0f, this.f, this.g, this.a);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.f = View.MeasureSpec.getSize(i);
        this.g = View.MeasureSpec.getSize(i2);
    }

    public Home2ManagerBGView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.a = new Paint(1);
        this.h = new int[3];
        this.i = new float[3];
        a();
    }
}