package me.spica.spicaweather2.view.weather_bg;

import java.util.Random;

@SuppressWarnings("unused")
public class SnowRandomGenerator {
  private static final Random RANDOM = new Random();

  // 區間隨機
  public float getRandom(float lower, float upper) {
    float min = Math.min(lower, upper);
    float max = Math.max(lower, upper);
    return getRandom(max - min) + min;
  }

  // 上界隨機
  public float getRandom(float upper) {
    return RANDOM.nextFloat() * upper;
  }

  // 上界隨機
  public int getRandom(int upper) {
    return RANDOM.nextInt(upper);
  }
}
