package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.addAll
import com.carterhudson.redux_kotlin_android.util.cancel
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.pause
import com.carterhudson.redux_kotlin_android.util.resume

abstract class ReduxActivity<StateT : State, ComponentStateT : State> : AppCompatActivity() {

  private lateinit var viewModel: ReduxViewModel<StateT>
  private lateinit var viewComponent: ViewComponent<ComponentStateT>
  private val managedSubs = mutableListOf<ManagedSubscription>()

  val dispatch by lazy { viewModel.dispatch }

  fun getViewComponent() = viewComponent

  fun getViewModel() = viewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewModel = onCreateViewModel()
    onViewModelCreated(viewModel)

    viewComponent = onCreateViewComponent()
    with(viewModel) {
      managedSubs.addAll(
        subscribe(viewComponent::render, distinct(), ::onSelectState),
        subscribe(::performSideEffect)
      )
    }
    onViewComponentCreated(viewComponent)
    setContentView(viewComponent)
  }

  /**
   * Overrides [AppCompatActivity.setContentView] to use [viewComponent]
   *
   * @param viewComponent - the component whose [ViewComponent.root] will be used as the content view.
   */
  protected open fun setContentView(viewComponent: ViewComponent<ComponentStateT>) {
    setContentView(viewComponent.root())
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
   * @param createViewModel function providing a redux view model
   */
  protected open fun onViewModelCreated(viewModel: ReduxViewModel<StateT>) {
    //optional
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
  protected open fun onViewComponentCreated(viewComponent: ViewComponent<ComponentStateT>) {
    //optional
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