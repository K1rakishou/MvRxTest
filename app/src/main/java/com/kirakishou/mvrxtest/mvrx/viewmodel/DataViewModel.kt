package com.kirakishou.mvrxtest.mvrx.viewmodel

import android.support.v4.app.FragmentActivity
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.kirakishou.mvrxtest.data.ApiService
import com.kirakishou.mvrxtest.mvrx.state.DataState
import org.koin.android.ext.android.inject

class DataViewModel(
  initialState: DataState,
  private val apiService: ApiService
) : BaseMvRxViewModel<DataState>(initialState) {

  init {
    fetchNextPage()
  }

  fun fetchNextPage() {
    withState { state ->
      apiService.fetchNextPage(state.lastId, PHOTOS_PER_PAGE)
        .execute {
          if (it is Loading) {
            return@execute copy()
          }

          val newDataList = (it() ?: emptyList())

          copy(
            request = it,
            lastId = newDataList.lastOrNull()?.id ?: Long.MAX_VALUE,
            colors = colors + newDataList,
            endReached = newDataList.size < PHOTOS_PER_PAGE
          )
        }
    }
  }

  fun reset() {
    setState {
      copy(
        request = Uninitialized,
        lastId = 0,
        lastVisibleItemPosition = -1,
        colors = listOf(),
        endReached = false
      )
    }

    fetchNextPage()
  }

  fun setLastVisibleItemPosition(position: Int) {
    setState {
      copy(
        lastVisibleItemPosition = position
      )
    }
  }

  companion object : MvRxViewModelFactory<DataState> {
    const val PHOTOS_PER_PAGE = 150

    @JvmStatic
    override fun create(activity: FragmentActivity, state: DataState): BaseMvRxViewModel<DataState> {
      val service: ApiService by activity.inject()
      return DataViewModel(state, service)
    }
  }
}