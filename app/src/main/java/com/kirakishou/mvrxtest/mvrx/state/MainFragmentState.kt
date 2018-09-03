package com.kirakishou.mvrxtest.mvrx.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.kirakishou.mvrxtest.data.response.ColorResponse

data class MainFragmentState(
  val colors: List<ColorResponse> = listOf(),
  val request: Async<List<ColorResponse>> = Uninitialized
) : MvRxState