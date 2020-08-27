package com.carterhudson.redux_kotlin_android_tools.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carterhudson.redux_kotlin_android_tools.util.*
import com.carterhudson.redux_kotlin_android_tools.util.lifecycle.LifecycleAction

/**
 * A [Fragment] that manages a [storeViewModel] and [viewComponent]
 *
 * @param StateT the state type for the [StoreViewModel]
 * @param ComponentStateT the state type for the [ViewComponent]
 * @property storeViewModel the [StoreViewModel] instance
 * @property composite a [CompositeSubscription] for managing [State] & [Command] subscriptions
 */
abstract class ReduxFragment<StateT : State, ComponentStateT : State>(
  private val storeViewModel: StoreViewModel<StateT>,
  private val composite: CompositeSubscription,
  private val stateSelector: StateSelector<StateT, ComponentStateT>
) : Fragment(), ActionDispatcher by storeViewModel {

  private lateinit var viewComponent: ViewComponent<ComponentStateT>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    provideViewModel {
      storeViewModel
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
    .also {
      viewComponent = it
    }
    .root()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(storeViewModel) {
      composite.add {
        subscribe(viewComponent::render, distinct(), stateSelector)
      }
      dispatch(LifecycleAction.Fragment.ViewCreated)
    }
  }

  override fun dispatch(action: Action) = storeViewModel.dispatch(action)

  protected open fun distinct(): Boolean = true

  override fun onStart() {
    super.onStart()
    dispatch(LifecycleAction.Fragment.Started)
  }

  override fun onResume() {
    super.onResume()
    composite.resume()
    dispatch(LifecycleAction.Fragment.Resumed)
  }

  override fun onPause() {
    dispatch(LifecycleAction.Fragment.Paused)
    composite.pause()
    super.onPause()
  }

  override fun onStop() {
    dispatch(LifecycleAction.Fragment.Stopped)
    composite.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    dispatch(LifecycleAction.Fragment.Destroyed)
    super.onDestroy()
  }
}