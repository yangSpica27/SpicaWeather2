package me.spica.spicaweather2.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.RenderEffect;
import android.graphics.RuntimeShader;
import android.graphics.Shader;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Scanner;
import me.spica.spicaweather2.R;
import me.spica.spicaweather2.base.App;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class SnowShaderUtils {

  private static final BitmapShader snowBitmapShader;

  private static final float[] bitmapDimensions = new float[2];

  private final RuntimeShader snowRuntimeShader;

  static {
    Resources resources = App.getInstance().getApplicationContext().getResources();
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;
    Bitmap bitmapDecodeResource2 = BitmapFactory.decodeResource(resources,
        R.drawable.vieweffect_snow, options);
    Shader.TileMode tileMode2 = Shader.TileMode.REPEAT;
    BitmapShader bitmapShader2 = new BitmapShader(bitmapDecodeResource2, tileMode2, tileMode2);
    snowBitmapShader = bitmapShader2;
    bitmapShader2.setFilterMode(BitmapShader.FILTER_MODE_LINEAR);
    bitmapDimensions[0] = bitmapDecodeResource2.getWidth();
    bitmapDimensions[1] = bitmapDecodeResource2.getHeight();
  }

  public SnowShaderUtils() {
    Resources resources = App.getInstance().getApplicationContext().getResources();
    this.snowRuntimeShader = new RuntimeShader(readRawToString(resources, R.raw.snow));
    this.snowRuntimeShader.setInputShader("uSnowTex", snowBitmapShader);
    this.snowRuntimeShader.setFloatUniform("uSnowTexWH", bitmapDimensions);
  }

  public RenderEffect getRenderEffect(){
    return RenderEffect.createRuntimeShaderEffect(this.snowRuntimeShader, "uTex");
  }

  private String readRawToString(Resources resources, int raw_res) {
    Scanner scanner = new Scanner(resources.openRawResource(raw_res));
    StringBuilder sb = new StringBuilder();
    while (scanner.hasNextLine()) {
      sb.append(scanner.nextLine());
      sb.append("\n");
    }
    return sb.toString();
  }

  /**
   * 更新shader
   * @param uResolution 背景分辨率
   * @param uProgress
   */
  public void update(float[] uResolution, float uProgress) {
    this.snowRuntimeShader.setFloatUniform("uResolution", uResolution);
    this.snowRuntimeShader.setFloatUniform("uProgress", uProgress);
  }

}
