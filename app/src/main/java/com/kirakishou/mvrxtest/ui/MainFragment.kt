package com.kirakishou.mvrxtest.ui


import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.kirakishou.mvrxtest.R
import com.kirakishou.mvrxtest.app.BaseFragmentWithRecycler
import com.kirakishou.mvrxtest.mvrx.viewmodel.MainFragmentViewModel
import com.kirakishou.mvrxtest.ui.epoxy.colorRow
import com.kirakishou.mvrxtest.ui.epoxy.footerTextRow
import com.kirakishou.mvrxtest.ui.epoxy.loadingRow
import java.util.concurrent.atomic.AtomicBoolean

class MainFragment : BaseFragmentWithRecycler() {
  private val viewModel: MainFragmentViewModel by fragmentViewModel()
  private val runOnce = AtomicBoolean(false)
  private var isFragmentFreshlyCreate = false
  private var lastVisibleItemPosition = -1

  override fun getFragmentLayoutId(): Int = R.layout.fragment_main

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    isFragmentFreshlyCreate = savedInstanceState == null

    savedInstanceState?.let {
      lastVisibleItemPosition = it.getInt(LAST_VISIBLE_ITEM_POSITION_KEY, -1)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)

    outState.putInt(LAST_VISIBLE_ITEM_POSITION_KEY, lastVisibleItemPosition)
  }

  override fun buildEpoxyController(): AsyncEpoxyController = simpleController {
    return@simpleController withState(viewModel) { state ->
      if (state.colors.isNotEmpty()) {
        state.colors.forEach { data ->
          colorRow {
            id(data.id)
            text(data.id.toString())
            color(data.color)

            onBind { _, _, position -> lastVisibleItemPosition = position }
          }
        }
      }

      //we want recyclerView to scroll to bottom after the phone has been rotated and we
      //want to do it only once
      if (lastVisibleItemPosition != -1
        && !isFragmentFreshlyCreate
        && runOnce.compareAndSet(false, true)) {
        recyclerView.post {
          recyclerView.scrollToPosition(lastVisibleItemPosition)
        }
      }

      if (state.request is Success) {
        if (state.colors.size % MainFragmentViewModel.PHOTOS_PER_PAGE != 0) {
          footerTextRow {
            id("list_end_footer")
            text("End of the list reached")
          }

          return@withState
        }

        loadingRow {
          id("loading${state.colors.size}")
          onBind { _, _, _ -> viewModel.fetchNextPage() }
        }
      } else if (state.request is Fail) {
        footerTextRow {
          id("error_footer")
          text("An error has occurred. Tap to retry")
          callback { _ -> viewModel.reset() }
        }

        Toast.makeText(activity, state.request.error.message
          ?: "Unknown error", Toast.LENGTH_LONG).show()
      }
    }
  }

  companion object {
      const val LAST_VISIBLE_ITEM_POSITION_KEY = "last_visible_item_position"
  }
}
