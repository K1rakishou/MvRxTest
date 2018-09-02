package com.kirakishou.mvrxtest.ui.epoxy

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.kirakishou.mvrxtest.R
import com.kirakishou.mvrxtest.data.response.MyColor

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ColorRow @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  private val colorView: TextView

  init {
    inflate(context, R.layout.color_row, this)
    colorView = findViewById(R.id.color_view)
    orientation = VERTICAL
  }

  @ModelProp
  fun setColor(color: MyColor) {
    colorView.background = ColorDrawable(color.toIntColor())
  }

  @ModelProp
  fun setText(text: String) {
    colorView.text = text
  }
}