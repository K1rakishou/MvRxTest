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
import com.kirakishou.mvrxtest.R

abstract class BaseFragmentWithRecycler : BaseMvRxFragment() {
  protected lateinit var recyclerView: EpoxyRecyclerView
  protected lateinit var toolbar: Toolbar

  protected val epoxyController: EpoxyController by lazy { buildEpoxyController() }

  @CallSuper
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(getFragmentLayoutId(), container, false).apply {
      toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
        val navController = NavHostFragment.findNavController(this@BaseFragmentWithRecycler)
        NavigationUI.setupWithNavController(this, navController)
      }

      val recyclerViewInstance = findViewById<EpoxyRecyclerView>(R.id.recycler_view)
      if (recyclerViewInstance == null) {
        throw IllegalStateException("BaseFragmentWithRecycler requires fragment to contain " +
          "RecyclerView with id R.id.recycler_view!")
      }

      recyclerView = recyclerViewInstance.apply {
        epoxyController.spanCount = 2

        layoutManager = GridLayoutManager(activity, 2).apply {
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
    recyclerView.requestModelBuild()
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