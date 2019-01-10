package com.kirakishou.mvrxtest.ui


import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.mvrx.*
import com.kirakishou.mvrxtest.R
import com.kirakishou.mvrxtest.app.BaseFragmentWithRecycler
import com.kirakishou.mvrxtest.mvrx.state.MainFragmentState
import com.kirakishou.mvrxtest.mvrx.viewmodel.MainFragmentViewModel
import com.kirakishou.mvrxtest.ui.epoxy.colorRow
import com.kirakishou.mvrxtest.ui.epoxy.footerTextRow
import com.kirakishou.mvrxtest.ui.epoxy.loadingRow

class MainFragment : BaseFragmentWithRecycler(SPAN_COUNT) {
  private val viewModel: MainFragmentViewModel by fragmentViewModel()

  override fun getFragmentLayoutId(): Int = R.layout.fragment_main

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.selectSubscribe(MainFragmentState::colors, MainFragmentState::request) { _, _ ->
      recyclerView.requestModelBuild()
    }
  }

  override fun onStart() {
    super.onStart()

    withState(viewModel) { state ->
      if (state.lastSeenColorPosition > 0) {
        recyclerView.post {
          recyclerView.scrollToPosition(state.lastSeenColorPosition)
        }
      }
    }
  }

  override fun buildEpoxyController(): AsyncEpoxyController = simpleController {
    return@simpleController withState(viewModel) { state ->
      if (state.colors.isNotEmpty()) {
        state.colors.forEachIndexed { index, data ->
          colorRow {
            id(data.id)
            text(data.id.toString())
            color(data.color)

            if (index > 0 && index % (10 * SPAN_COUNT) == 0) {
              onBind { _, _, position ->
                viewModel.updateLastSeenColorPosition(position)
              }
            }
          }
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

        Toast.makeText(
          activity, state.request.error.message
          ?: "Unknown error", Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  companion object {
    const val SPAN_COUNT = 4
  }
}
