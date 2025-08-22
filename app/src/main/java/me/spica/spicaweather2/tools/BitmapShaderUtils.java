package me.spica.spicaweather2.tools;

import android.content.res.Resources;
import android.graphics.RenderEffect;
import android.graphics.RuntimeShader;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.Scanner;
import me.spica.spicaweather2.R;
import me.spica.spicaweather2.base.App;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class BitmapShaderUtils {


  RuntimeShader shader_rain;


  public BitmapShaderUtils() {
    init();
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


  public RenderEffect getRenderEffect(int mode) {
    return RenderEffect.createRuntimeShaderEffect(this.shader_rain, "uTex");
  }


  public void init() {
    Resources resources =  App.getInstance().getApplicationContext().getResources();
    this.shader_rain = new RuntimeShader(readRawToString(resources, R.raw.rain_drop_new));

    this.shader_rain.setFloatUniform("uResolution", new float[]{
        AppToolsKt.getScreenWidth(App.getInstance().getApplicationContext()),
        AppToolsKt.getScreenHeight(App.getInstance().getApplicationContext())});
  }



  public void updateShader(float nanoTime) {
    this.shader_rain.setFloatUniform("uTime", nanoTime);
    this.shader_rain.setFloatUniform("uStaticDropSize", 0.2f);
    this.shader_rain.setFloatUniform("uStaticDropAmount", 0.4f);
    this.shader_rain.setFloatUniform("uStaticDropSpeed", 2.0f);
    this.shader_rain.setFloatUniform("uRunningDropSize", 0.25f);
    this.shader_rain.setFloatUniform("uRunningDropAmount", 0.55f);
    this.shader_rain.setFloatUniform("uRunningDropSpeed", 1.3f);
  }

  public void updateUGravity(float x, float y){
    this.shader_rain.setFloatUniform("uGravity", new float[]{x, y});
  }

}