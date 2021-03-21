package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.safeCast

abstract class ReduxActivity<StateT : ReduxState, RenderStateT : ReduxState> : AppCompatActivity() {

  protected lateinit var storeViewModel: StoreViewModel<StateT>
    private set

  protected var renderer: Renderer<RenderStateT>? = null
    private set

  val dispatcher: TypesafeDispatcher by lazy { storeViewModel.dispatcher }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    storeViewModel = onCreateViewModel()
    onViewModelCreated(storeViewModel)

    renderer = onCreateRenderer()

    renderer?.safeCast<ViewRenderer<RenderStateT>>()?.let(::setContentView)
  }

  /**
   * Overrides [AppCompatActivity.setContentView] to use [viewRenderer]
   *
   * @param viewRenderer - the component whose [ViewRenderer.root] will be used as the content view.
   */
  protected open fun setContentView(viewRenderer: ViewRenderer<RenderStateT>) {
    setContentView(viewRenderer.root())
  }

  /**
   * Delegate method.
   * Invoked in order to obtain a [StoreViewModel] instance.
   *
   * @return the provided [StoreViewModel] instance.
   */
  protected abstract fun onCreateViewModel(): StoreViewModel<StateT>

  /**
   * Delegate method.
   * Invoked after [onCreateViewModel].
   * Sets [viewModel] reference.
   *
   * @param viewModel - the viewModel
   */
  protected open fun onViewModelCreated(viewModel: StoreViewModel<StateT>) = Unit

  /**
   * Delegate method (Required).
   * Invoked in order to obtain a [ViewRenderer] instance.
   *
   * @return the created [ViewRenderer] instance.
   */
  open fun onCreateRenderer(): Renderer<RenderStateT>? = null

  /**
   * Delegate method invoked after [onCreateRenderer]
   * Invoked after [onCreateRenderer] is created
   */
  protected open fun onRendererCreated(renderer: Renderer<RenderStateT>?) = Unit


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