package com.kirakishou.mvrxtest.app

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.kirakishou.mvrxtest.BuildConfig
import com.kirakishou.mvrxtest.R

abstract class BaseFragmentWithRecycler(
    private val spanCount: Int
) : BaseMvRxFragment() {
  protected lateinit var recyclerView: EpoxyRecyclerView
  protected lateinit var toolbar: Toolbar

  protected val epoxyController: EpoxyController by lazy {
    buildEpoxyController().apply { isDebugLoggingEnabled = BuildConfig.DEBUG }
  }

  @CallSuper
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(getFragmentLayoutId(), container, false).apply {
      val toolBarInstance = findViewById<Toolbar>(R.id.toolbar)
      if (toolBarInstance == null) {
        throw IllegalStateException("BaseFragmentWithRecycler requires fragment to contain " +
                "Toolbar with id = R.id.toolbar!")
      }

      val recyclerViewInstance = findViewById<EpoxyRecyclerView>(R.id.recycler_view)
      if (recyclerViewInstance == null) {
        throw IllegalStateException("BaseFragmentWithRecycler requires fragment to contain " +
          "RecyclerView with id = R.id.recycler_view!")
      }

      toolbar = toolBarInstance.apply {
        val navController = NavHostFragment.findNavController(this@BaseFragmentWithRecycler)
        NavigationUI.setupWithNavController(this, navController)
      }

      recyclerView = recyclerViewInstance.apply {
        epoxyController.spanCount = spanCount

        layoutManager = GridLayoutManager(activity, spanCount).apply {
          spanSizeLookup = epoxyController.spanSizeLookup
        }

        setController(epoxyController)
      }
    }
  }

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    epoxyController.onRestoreInstanceState(savedInstanceState)
  }

  @CallSuper
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    epoxyController.onSaveInstanceState(outState)
  }

  @CallSuper
  override fun onDestroyView() {
    epoxyController.cancelPendingModelBuild()
    super.onDestroyView()
  }

  @CallSuper
  override fun invalidate() {
    // We don't use invalidate because we manually subscribe to only those parts of the state that we
    // need in order to rebuild the epoxy models. Otherwise the rebuilding process would start every time
    // even if we don't want it (e.g. when we update lastSeenColorPosition in the state we don't want
    // to start the rebuilding process). By subscribing manually and then manually rebuilding epoxy
    // we can store more things in the state while not being afraid of triggering recyclerview redrawing
    // with every state change.
  }

  protected fun simpleController(
    build: EpoxyController.() -> Unit
  ): AsyncEpoxyController {
    return object : AsyncEpoxyController() {
      override fun buildModels() {
        if (view == null || isRemoving) {
          return
        }

        build()
      }
    }
  }

  abstract fun getFragmentLayoutId(): Int
  abstract fun buildEpoxyController(): AsyncEpoxyController
}