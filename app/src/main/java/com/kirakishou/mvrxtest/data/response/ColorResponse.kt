package com.kirakishou.mvrxtest.data.response

import java.util.*

data class MyColor(
  val a: Float,
  val r: Float,
  val g: Float,
  val b: Float
) {

  fun toIntColor(): Int {
    return (a * 255.0f + 0.5f).toInt() shl 24 or
      ((r * 255.0f + 0.5f).toInt() shl 16) or
      ((g * 255.0f + 0.5f).toInt() shl 8) or
      (b * 255.0f + 0.5f).toInt()
  }

  companion object {
    fun create(random: Random): MyColor {
      return MyColor(
        1.0f,
        random.nextFloat(),
        random.nextFloat(),
        random.nextFloat()
      )
    }
  }
}

data class ColorResponse(
  val id: Long,
  val color: MyColor
)