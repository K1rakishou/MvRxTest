package com.kirakishou.mvrxtest.ui

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity
import com.kirakishou.mvrxtest.R

class MainActivity : BaseMvRxActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

}
