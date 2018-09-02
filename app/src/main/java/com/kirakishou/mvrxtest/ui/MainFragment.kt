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
import com.kirakishou.mvrxtest.mvrx.viewmodel.DataViewModel
import com.kirakishou.mvrxtest.ui.epoxy.colorRow
import com.kirakishou.mvrxtest.ui.epoxy.footerTextRow
import com.kirakishou.mvrxtest.ui.epoxy.loadingRow
import java.util.concurrent.atomic.AtomicBoolean

class MainFragment : BaseFragmentWithRecycler() {
  private val viewModel: DataViewModel by fragmentViewModel()
  private val runOnce = AtomicBoolean(false)
  private var isFragmentFreshlyCreate = false

  override fun getFragmentLayoutId(): Int = R.layout.fragment_main

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    isFragmentFreshlyCreate = savedInstanceState == null
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    epoxyController.onRestoreInstanceState(savedInstanceState)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    epoxyController.onSaveInstanceState(outState)
  }

  override fun onDestroyView() {
    epoxyController.cancelPendingModelBuild()
    super.onDestroyView()
  }

  override fun buildEpoxyController(): AsyncEpoxyController = simpleController {
    return@simpleController withState(viewModel) { state ->
      if (state.colors.isNotEmpty()) {
        state.colors.forEachIndexed { index, data ->
          colorRow {
            id(data.id)
            text(data.id.toString())
            color(data.color)

            if (index % 10 == 0) {
              onBind { _, _, position -> viewModel.setLastVisibleItemPosition(position) }
            }
          }
        }
      }

      if (state.lastVisibleItemPosition != -1 && !isFragmentFreshlyCreate && runOnce.compareAndSet(false, true)) {
        recyclerView.post {
          recyclerView.scrollToPosition(state.lastVisibleItemPosition)
        }
      }

      if (state.request is Success) {
        if (state.endReached) {
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

        Toast.makeText(activity, state.request.error.message ?: "Unknown error", Toast.LENGTH_LONG).show()
      }
    }
  }
}
