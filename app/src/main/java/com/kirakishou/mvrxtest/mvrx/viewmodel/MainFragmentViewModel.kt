package com.kirakishou.mvrxtest.mvrx.viewmodel

import android.support.v4.app.FragmentActivity
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.kirakishou.mvrxtest.data.ApiService
import com.kirakishou.mvrxtest.mvrx.state.MainFragmentState
import org.koin.android.ext.android.inject

class MainFragmentViewModel(
  initialState: MainFragmentState,
  private val apiService: ApiService
) : BaseMvRxViewModel<MainFragmentState>(initialState) {

  init {
    fetchNextPage()
  }

  fun fetchNextPage() {
    withState { state ->
      apiService.fetchNextPage(state.colors.lastOrNull()?.id ?: 0, PHOTOS_PER_PAGE)
        .execute {
          //do not modify the state and do not make request if the request is being executed
          if (it is Loading) {
            return@execute copy()
          }

          val newDataList = (it() ?: emptyList())

          //TODO: figure out how to find out that the request is in the loading state without storing
          //the request itself in the state (it that even possible right now?)
          copy(
            request = it,
            colors = colors + newDataList
          )
        }
    }
  }

  fun reset() {
    setState {
      copy(
        request = Uninitialized,
        colors = listOf()
      )
    }

    fetchNextPage()
  }

  companion object : MvRxViewModelFactory<MainFragmentState> {
    const val PHOTOS_PER_PAGE = 100

    @JvmStatic
    override fun create(
      activity: FragmentActivity,
      state: MainFragmentState
    ): BaseMvRxViewModel<MainFragmentState> {
      val service: ApiService by activity.inject()
      return MainFragmentViewModel(state, service)
    }
  }
}