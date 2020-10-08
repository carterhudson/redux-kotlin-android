package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.StateSelector
import com.carterhudson.redux_kotlin_android.util.addAll
import com.carterhudson.redux_kotlin_android.util.cancel
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.pause
import com.carterhudson.redux_kotlin_android.util.provideViewModel
import com.carterhudson.redux_kotlin_android.util.resume

abstract class ReduxFragment<StateT : State, ComponentStateT : State> : Fragment() {

  private lateinit var reduxViewModel: ReduxViewModel<StateT>
  private lateinit var stateSelector: StateSelector<StateT, ComponentStateT>
  private var subscriptions = mutableListOf<ManagedSubscription>()

  private lateinit var viewComponent: ViewComponent<ComponentStateT>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    provideViewModel {
      onProvideViewModel().also { reduxViewModel = it }
    }
  }

  abstract fun onProvideViewModel(): ReduxViewModel<StateT>

  fun getViewModel() = reduxViewModel

  /**
   * Overridden from [Fragment.onCreateView].
   * Assigns [viewComponent] reference.
   * Dispatches [LifecycleAction.CreatingView].
   *
   * @return the root view of the created [viewComponent]
   */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = onCreateViewComponent(inflater, container, savedInstanceState)
    .also { viewComponent = it }
    .also { reduxViewModel.dispatch(LifecycleAction.CreatingView(this)) }
    .root()

  /**
   * Delegate method (Required).
   * Invoked in order to obtain a [ViewComponent] instance.
   * Called from [Fragment.onCreateView]
   *
   * @param inflater layout inflater provided by [Fragment.onCreateView]
   * @param container container provided by [Fragment.onCreateView]
   * @param savedInstanceState bundle provided by [Fragment.onCreateView]
   * @return the created [ViewComponent] instance.
   */
  abstract fun onCreateViewComponent(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): ViewComponent<ComponentStateT>

  /**
   * Overridden from [Fragment.onViewCreated]
   * Handles subscribing to state & side effects
   * Dispatches [LifecycleAction.ViewCreated]
   */
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    with(reduxViewModel) {
      subscriptions.addAll(
        subscribe(viewComponent::render, distinct(), stateSelector),
        subscribe(::performSideEffect)
      )
    }

    reduxViewModel.dispatch(LifecycleAction.ViewCreated(this))
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
  protected open fun performSideEffect(state: StateT, action: Any) {
    //optional
  }

  override fun onStart() {
    super.onStart()
    reduxViewModel.dispatch(LifecycleAction.Starting(this))
  }

  override fun onResume() {
    super.onResume()
    subscriptions.resume()
    reduxViewModel.dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    reduxViewModel.dispatch(LifecycleAction.Pausing(this))
    subscriptions.pause()
    super.onPause()
  }

  override fun onStop() {
    reduxViewModel.dispatch(LifecycleAction.Stopping(this))
    subscriptions.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    reduxViewModel.dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}