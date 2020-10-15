package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.addAll
import com.carterhudson.redux_kotlin_android.util.cancel
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.pause
import com.carterhudson.redux_kotlin_android.util.resume
import com.carterhudson.redux_kotlin_android.util.safeCast
import org.reduxkotlin.Dispatcher

abstract class ReduxActivity<StateT : State, RenderStateT : State> : AppCompatActivity() {

  private lateinit var viewModel: ReduxViewModel<StateT>
  private var renderer: Renderer<RenderStateT>? = null
  private val managedSubs: MutableList<ManagedSubscription> = mutableListOf()

  val dispatch: Dispatcher by lazy { viewModel.dispatch }

  fun getRenderer(): Renderer<RenderStateT>? = renderer

  fun getViewModel(): ReduxViewModel<StateT> = viewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewModel = onCreateViewModel()
    onViewModelCreated(viewModel)

    renderer = onCreateRenderer()
    with(viewModel) {
      managedSubs.addAll(
        subscribe({ renderer?.render(it) }, distinct(), ::onSelectState),
        subscribe(::performSideEffect)
      )
    }
    onRendererCreated(renderer)
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
   * Invoked in order to obtain a [ReduxViewModel] instance.
   *
   * @return the provided [ReduxViewModel] instance.
   */
  protected abstract fun onCreateViewModel(): ReduxViewModel<StateT>

  /**
   * Delegate method.
   * Invoked after [onCreateViewModel].
   * Sets [viewModel] reference.
   *
   * @param viewModel - the viewModel
   */
  protected open fun onViewModelCreated(viewModel: ReduxViewModel<StateT>) {
    //optional
  }

  /**
   * Delegate method (Required).
   * Invoked in order to obtain a [ViewRenderer] instance.
   *
   * @return the created [ViewRenderer] instance.
   */
  open fun onCreateRenderer(): Renderer<RenderStateT>? = null

  /**
   * Delegate method invoked after [onCreateRenderer]
   * Invoked when a [ViewRenderer] is created
   * Retains the view component and subscribes to state & side effects
   */
  protected open fun onRendererCreated(renderer: Renderer<RenderStateT>?) {
    //optional
  }

  /**
   * Delegate method (Required).
   * Override in order to provide mapping one state to another.
   *
   * @param state The state to be mapped from
   * @return The state that is mapped to
   */
  protected abstract fun onSelectState(state: StateT): RenderStateT

  /**
   * Delegate method (Optional).
   * Override in order to change distinct vs indistinct render calls.
   *
   * @return boolean indicating distinct state rendering.
   */
  protected open fun distinct(): Boolean = true

  /**
   * Delegate method (Optional)
   * Override in order to perform side effects.
   *
   * @param state
   * @param action
   */
  protected open fun performSideEffect(state: StateT, action: Any) {
    //optional
  }

  override fun onStart() {
    super.onStart()
    dispatch(LifecycleAction.Starting(this))
  }

  override fun onResume() {
    super.onResume()
    managedSubs.resume()
    dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    dispatch(LifecycleAction.Pausing(this))
    managedSubs.pause()
    super.onPause()
  }

  override fun onStop() {
    dispatch(LifecycleAction.Stopping(this))
    managedSubs.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}