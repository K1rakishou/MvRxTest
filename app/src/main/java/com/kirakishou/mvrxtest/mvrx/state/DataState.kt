package com.kirakishou.mvrxtest.mvrx.state

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.kirakishou.mvrxtest.data.response.ColorResponse

data class DataState(
  val colors: List<ColorResponse> = listOf(),
  val lastId: Long = 0,
  val lastVisibleItemPosition: Int = -1,
  val endReached: Boolean = false,
  val request: Async<List<ColorResponse>> = Uninitialized
) : MvRxState