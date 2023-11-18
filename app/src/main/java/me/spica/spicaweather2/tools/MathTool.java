package me.spica.spicaweather2.tools;

import static android.view.KeyCharacterMap.ALPHA;

public class MathTool {

  public static float[] lowPass(float[] input, float[] output) {
    if (output == null) return input;
    try {
      for (int i = 0; i < input.length; i++) {
        output[i] = output[i] + ALPHA * (input[i] - output[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return input;
    }

    return output;
  }
}
