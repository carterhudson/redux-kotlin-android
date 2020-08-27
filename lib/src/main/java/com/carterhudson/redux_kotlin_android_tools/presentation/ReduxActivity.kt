package com.carterhudson.redux_kotlin_android_tools.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android_tools.util.Action
import com.carterhudson.redux_kotlin_android_tools.util.CompositeSubscription
import com.carterhudson.redux_kotlin_android_tools.util.State
import com.carterhudson.redux_kotlin_android_tools.util.provideViewModel
import com.carterhudson.redux_kotlin_android_tools.util.lifecycle.LifecycleAction

/**
 * An abstract class that handles the boilerplate and wiring for obtaining and managing
 * a [StoreViewModel] and [ViewComponent].
 *
 * @param StateT the state for the [storeViewModel]
 * @param ComponentStateT the state for the [viewComponent]
 */
abstract class ReduxActivity<StateT : State, ComponentStateT : State> : AppCompatActivity() {

  private lateinit var storeViewModel: StoreViewModel<StateT>

  private lateinit var viewComponent: ViewComponent<ComponentStateT>

  private val composite = CompositeSubscription()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    onViewModelCreated {
      provideViewModel {
        onCreateViewModel()
      }
    }

    setContentView {
      onViewComponentCreated {
        onCreateViewComponent()
      }
    }
  }

  protected open fun setContentView(createViewComponent: () -> ViewComponent<ComponentStateT>?) {
    setContentView(createViewComponent()?.root())
  }

  protected abstract fun onCreateViewModel(): StoreViewModel<StateT>

  protected open fun onViewModelCreated(viewModel: StoreViewModel<StateT>) {
    storeViewModel = viewModel
  }

  protected open fun onViewModelCreated(createViewModel: () -> StoreViewModel<StateT>): StoreViewModel<StateT> =
    createViewModel().also(::onViewModelCreated)

  protected open fun onViewComponentCreated(viewComponent: ViewComponent<ComponentStateT>) {
    this.viewComponent = viewComponent
  }

  protected open fun onViewComponentCreated(
    createViewComponent: () -> ViewComponent<ComponentStateT>
  ): ViewComponent<ComponentStateT> =
    createViewComponent()
      .also(::onViewComponentCreated)
      .also { component ->
        with(storeViewModel) {
          composite.add {
            subscribe(component::render, distinct(), ::onSelectState)
          }
        }
      }

  protected abstract fun onCreateViewComponent(): ViewComponent<ComponentStateT>

  protected abstract fun onSelectState(state: StateT): ComponentStateT

  protected open fun distinct(): Boolean = true

  protected fun dispatch(action: Action) = storeViewModel.dispatch(action)

  override fun onStart() {
    super.onStart()
    dispatch(LifecycleAction.Activity.Started)
  }

  override fun onResume() {
    super.onResume()
    composite.resume()
    dispatch(LifecycleAction.Activity.Resumed)
  }

  override fun onPause() {
    dispatch(LifecycleAction.Activity.Paused)
    composite.pause()
    super.onPause()
  }

  override fun onStop() {
    dispatch(LifecycleAction.Activity.Stopped)
    composite.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    dispatch(LifecycleAction.Activity.Destroyed)
    super.onDestroy()
  }
}