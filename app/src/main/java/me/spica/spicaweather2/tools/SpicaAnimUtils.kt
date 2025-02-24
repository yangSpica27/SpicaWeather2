package me.spica.spicaweather2.tools

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator


object SpicaAnimUtils {


  fun getShakeAnim(
    view: View,
    scaleSmall: Float = 1f,
    scaleLarge: Float = 1f,
    shakeDegrees: Float = .1f,
    duration: Long = 70,
    repeatCount: Int = ValueAnimator.INFINITE
  ): ObjectAnimator {
    val scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(
      View.SCALE_X,
      Keyframe.ofFloat(0f, 1.0f),
      Keyframe.ofFloat(0.25f, scaleSmall),
      Keyframe.ofFloat(0.5f, scaleLarge),
      Keyframe.ofFloat(0.75f, scaleLarge),
      Keyframe.ofFloat(1.0f, 1.0f)
    )
    val scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(
      View.SCALE_Y,
      Keyframe.ofFloat(0f, 1.0f),
      Keyframe.ofFloat(0.25f, scaleSmall),
      Keyframe.ofFloat(0.5f, scaleLarge),
      Keyframe.ofFloat(0.75f, scaleLarge),
      Keyframe.ofFloat(1.0f, 1.0f)
    )


    //先往左再往右
    val rotateValuesHolder = PropertyValuesHolder.ofKeyframe(
      View.ROTATION,
      Keyframe.ofFloat(0f, 0f),
      Keyframe.ofFloat(0.1f, -shakeDegrees),
      Keyframe.ofFloat(0.2f, shakeDegrees),
      Keyframe.ofFloat(0.3f, -shakeDegrees),
      Keyframe.ofFloat(0.4f, shakeDegrees),
      Keyframe.ofFloat(0.5f, -shakeDegrees),
      Keyframe.ofFloat(0.6f, shakeDegrees),
      Keyframe.ofFloat(0.7f, -shakeDegrees),
      Keyframe.ofFloat(0.8f, shakeDegrees),
      Keyframe.ofFloat(0.9f, -shakeDegrees),
      Keyframe.ofFloat(1.0f, 0f)
    )

    val objectAnimator: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
      view,
      scaleXValuesHolder,
      scaleYValuesHolder,
      rotateValuesHolder
    )
    objectAnimator.interpolator = LinearInterpolator()
    objectAnimator.setDuration(duration)
    objectAnimator.repeatCount = repeatCount
    return objectAnimator
  }

}