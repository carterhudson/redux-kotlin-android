package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android.util.CompositeSubscription
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.provideViewModel

/**
 * An abstract class that handles the boilerplate and wiring for obtaining and managing
 * a [ReduxViewModel] and [ViewComponent].
 *
 * @param StateT the state for the [reduxViewModel]
 * @param ComponentStateT the state for the [viewComponent]
 */
abstract class ReduxActivity<StateT : State, ComponentStateT : State> : AppCompatActivity() {

  protected lateinit var reduxViewModel: ReduxViewModel<StateT>

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

  protected abstract fun onCreateViewModel(): ReduxViewModel<StateT>

  protected open fun onViewModelCreated(createViewModel: () -> ReduxViewModel<StateT>): ReduxViewModel<StateT> =
    createViewModel().also { reduxViewModel = it }

  protected open fun onViewComponentCreated(
    createViewComponent: () -> ViewComponent<ComponentStateT>
  ): ViewComponent<ComponentStateT> =
    createViewComponent()
        .also { viewComponent = it }
        .also { component ->
          with(reduxViewModel) {
            composite.apply {
              add {
                subscribe(component::render, distinct(), ::onSelectState)
              }

              add {
                subscribe { state, action ->
                  performSideEffect(state, action)
                }
              }
            }
          }
        }

  protected abstract fun onCreateViewComponent(): ViewComponent<ComponentStateT>

  protected abstract fun onSelectState(state: StateT): ComponentStateT

  protected open fun distinct(): Boolean = true

  abstract fun performSideEffect(
    state: StateT,
    action: Any
  )

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