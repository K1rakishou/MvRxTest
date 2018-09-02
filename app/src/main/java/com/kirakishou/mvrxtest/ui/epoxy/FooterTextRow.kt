package com.kirakishou.mvrxtest.ui.epoxy

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.kirakishou.mvrxtest.R

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class FooterTextRow @JvmOverloads constructor(
  context: Context, 
  attrs: AttributeSet? = null, 
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  private val footerTextView: TextView

  init {
    inflate(context, R.layout.footer_text_row, this)
    footerTextView = findViewById(R.id.footer_text_view)
  }

  @ModelProp
  fun setText(text: String) {
    footerTextView.text = text
  }

  @CallbackProp
  fun setCallback(clickListener: OnClickListener?) {
    setOnClickListener(clickListener)
  }
}