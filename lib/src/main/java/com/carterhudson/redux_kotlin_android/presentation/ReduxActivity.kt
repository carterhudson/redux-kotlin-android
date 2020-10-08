package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.addAll
import com.carterhudson.redux_kotlin_android.util.cancel
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.pause
import com.carterhudson.redux_kotlin_android.util.provideViewModel
import com.carterhudson.redux_kotlin_android.util.resume

abstract class ReduxActivity<StateT : State, ComponentStateT : State> : AppCompatActivity() {

  protected lateinit var reduxViewModel: ReduxViewModel<StateT>

  private lateinit var viewComponent: ViewComponent<ComponentStateT>

  private val managedSubs = mutableListOf<ManagedSubscription>()

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
   * Sets [reduxViewModel] reference.
   *
   * @param createViewModel function providing a redux view model
   */
  protected open fun onViewModelCreated(createViewModel: () -> ReduxViewModel<StateT>) {
    createViewModel().also { reduxViewModel = it }
  }

  /**
   * Delegate method (Required).
   * Invoked in order to obtain a [ViewComponent] instance.
   *
   * @return the created [ViewComponent] instance.
   */
  protected abstract fun onCreateViewComponent(): ViewComponent<ComponentStateT>

  /**
   * Delegate method invoked after [onCreateViewComponent]
   * Invoked when a [ViewComponent] is created
   * Retains the view component and subscribes to state & side effects
   */
  protected open fun onViewComponentCreated(
    createViewComponent: () -> ViewComponent<ComponentStateT>
  ): ViewComponent<ComponentStateT> = createViewComponent()
      .also { viewComponent = it }
      .also { component ->
        with(reduxViewModel) {
          managedSubs.addAll(
              subscribe(component::render, distinct(), ::onSelectState),
              subscribe(::performSideEffect)
          )
        }
      }

  /**
   * Delegate method (Required).
   * Override in order to provide mapping one state to another.
   *
   * @param state The state to be mapped from
   * @return The state that is mapped to
   */
  protected abstract fun onSelectState(state: StateT): ComponentStateT

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
    managedSubs.resume()
    reduxViewModel.dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    reduxViewModel.dispatch(LifecycleAction.Pausing(this))
    managedSubs.pause()
    super.onPause()
  }

  override fun onStop() {
    reduxViewModel.dispatch(LifecycleAction.Stopping(this))
    managedSubs.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    reduxViewModel.dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}