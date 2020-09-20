package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carterhudson.redux_kotlin_android.util.Action
import com.carterhudson.redux_kotlin_android.util.CompositeSubscription
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.StateSelector
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.provideViewModel

/**
 * A [Fragment] that manages a [reduxViewModel] and [viewComponent]
 *
 * @param StateT the state type for the [ReduxViewModel]
 * @param ComponentStateT the state type for the [ViewComponent]
 * @property reduxViewModel the [ReduxViewModel] instance
 * @property composite a [CompositeSubscription] for managing [State] & [Command] subscriptions
 */
abstract class ReduxFragment<StateT : State, ComponentStateT : State> : Fragment() {

  protected lateinit var reduxViewModel: ReduxViewModel<StateT>
  protected lateinit var composite: CompositeSubscription
  protected lateinit var stateSelector: StateSelector<StateT, ComponentStateT>

  private lateinit var viewComponent: ViewComponent<ComponentStateT>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    provideViewModel {
      reduxViewModel
    }
  }

  abstract fun onCreateViewComponent(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): ViewComponent<ComponentStateT>

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = onCreateViewComponent(inflater, container, savedInstanceState)
      .also { viewComponent = it }
      .also { reduxViewModel.dispatch(LifecycleAction.CreatingView(this)) }
      .root()

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    composite.add {
      reduxViewModel.subscribe(viewComponent::render, distinct(), stateSelector)
    }

    reduxViewModel.dispatch(LifecycleAction.ViewCreated(this))
  }

  abstract fun performSideEffect(action: Action)

  protected open fun distinct(): Boolean = true

  override fun onStart() {
    super.onStart()
    reduxViewModel.dispatch(LifecycleAction.Starting(this))
  }

  override fun onResume() {
    super.onResume()
    composite.resume()
    reduxViewModel.dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    reduxViewModel.dispatch(LifecycleAction.Pausing(this))
    composite.pause()
    super.onPause()
  }

  override fun onStop() {
    reduxViewModel.dispatch(LifecycleAction.Stopping(this))
    composite.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    reduxViewModel.dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}