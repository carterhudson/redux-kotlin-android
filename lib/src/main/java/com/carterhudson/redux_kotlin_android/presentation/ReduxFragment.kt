package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.addAll
import com.carterhudson.redux_kotlin_android.util.cancel
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.pause
import com.carterhudson.redux_kotlin_android.util.resume
import com.carterhudson.redux_kotlin_android.util.safeCast

abstract class ReduxFragment<StateT : ReduxState, RenderStateT : ReduxState> : Fragment() {

  private lateinit var viewModel: StoreViewModel<StateT>
  private var renderer: Renderer<RenderStateT>? = null
  private var subscriptions = mutableListOf<ManagedSubscription>()

  val dispatch by lazy { viewModel.dispatch }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateViewModel().also { viewModel = it }
    onViewModelCreated(viewModel)
  }

  abstract fun onCreateViewModel(): StoreViewModel<StateT>

  open fun onViewModelCreated(viewModel: StoreViewModel<StateT>) = Unit

  /**
   * Overridden from [Fragment.onCreateView].
   * Assigns [renderer] reference.
   * Dispatches [LifecycleAction.CreatingView].
   *
   * @return the root view of the created [renderer]
   */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? = onCreateRenderer(inflater, container, savedInstanceState)
      .also { renderer = it }
      .also { viewModel.dispatch(LifecycleAction.Creating(this)) }
      .also { onRendererCreated(renderer) }
      ?.safeCast<ViewRenderer<RenderStateT>>()
      ?.root()

  /**
   * Invoked in order to obtain a [ViewRenderer] instance.
   * Called from [Fragment.onCreateView]
   *
   * @param inflater layout inflater provided by [Fragment.onCreateView]
   * @param container container provided by [Fragment.onCreateView]
   * @param savedInstanceState bundle provided by [Fragment.onCreateView]
   * @return the created [ViewRenderer] instance.
   */
  open fun onCreateRenderer(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): Renderer<RenderStateT>? = null

  open fun onRendererCreated(renderer: Renderer<RenderStateT>?) = Unit

  /**
   * Overridden from [Fragment.onViewCreated]
   * Handles subscribing to state & side effects
   * Dispatches [LifecycleAction.ViewCreated]
   */
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)

    with(viewModel) {
      subscriptions.addAll(
        onStateChanged(::onSelectState, distinct()) {
          renderer?.render(it)
        },
        addSideEffectHandler(::performSideEffect)
      )
    }
  }

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
  protected open fun performSideEffect(state: StateT, action: Any) = Unit

  abstract fun onSelectState(inState: StateT): RenderStateT

  override fun onStart() {
    super.onStart()
    dispatch(LifecycleAction.Starting(this))
  }

  override fun onResume() {
    super.onResume()
    subscriptions.resume()
    dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    dispatch(LifecycleAction.Pausing(this))
    subscriptions.pause()
    super.onPause()
  }

  override fun onStop() {
    dispatch(LifecycleAction.Stopping(this))
    subscriptions.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}