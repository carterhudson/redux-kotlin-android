package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.safeCast

abstract class ReduxCompatActivity<StateT : ReduxState, RenderStateT : ReduxState> : ReduxActivity<StateT, RenderStateT>() {

  protected val renderer: Renderer<RenderStateT>? by lazy { onCreateRenderer() }

  val storeViewModel: StoreViewModel<StateT> by lazy { onCreateViewModel() }

  val dispatcher: TypesafeDispatcher by lazy { setViewModelInternal().dispatcher }

  /**
   * Delegate method.
   * Invoked in order to obtain a [StoreViewModel] instance.
   *
   * @return the provided [StoreViewModel] instance.
   */
  protected abstract fun onCreateViewModel(): StoreViewModel<StateT>

  /**
   * Delegate method (Required).
   * Invoked in order to obtain a [ViewRenderer] instance.
   *
   * @return the created [ViewRenderer] instance.// KTX
  implementation "androidx.activity:activity-ktx:1.2.2"
   */
  open fun onCreateRenderer(): Renderer<RenderStateT>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    renderer?.safeCast<ViewRenderer<RenderStateT>>()?.let(::setContentView) ?: run {
      Log.d(
        this::class.java.simpleName,
        "No renderer provided; no content view being set"
      )
    }
  }

  /**
   * Overrides [AppCompatActivity.setContentView] to use [viewRenderer]
   *
   * @param viewRenderer - the component whose [ViewRenderer.root] will be used as the content view.
   */
  protected open fun setContentView(viewRenderer: ViewRenderer<RenderStateT>) {
    setContentView(viewRenderer.root())
  }

  protected abstract fun setViewModelInternal(): StoreViewModel<StateT>

  override fun onStart() {
    super.onStart()
    dispatcher.dispatch(LifecycleAction.Starting(this))
  }

  override fun onResume() {
    super.onResume()
    dispatcher.dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    dispatcher.dispatch(LifecycleAction.Pausing(this))
    super.onPause()
  }

  override fun onStop() {
    dispatcher.dispatch(LifecycleAction.Stopping(this))
    super.onStop()
  }

  override fun onDestroy() {
    dispatcher.dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}