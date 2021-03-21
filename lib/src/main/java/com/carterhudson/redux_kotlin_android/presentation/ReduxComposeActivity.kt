package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.cancel
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.pause
import com.carterhudson.redux_kotlin_android.util.resume

abstract class ReduxComposeActivity<StateT : ReduxState> : ComponentActivity() {

  protected lateinit var storeViewModel: StoreComposeViewModel<StateT>
    private set

  private val managedSubs: MutableList<ManagedSubscription> = mutableListOf()

  val dispatcher: TypeSafeDispatcher by lazy {
    storeViewModel.dispatcher
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    storeViewModel = onCreateViewModel()
    onViewModelCreated(storeViewModel)

    dispatcher.dispatch(LifecycleAction.Creating(this))
  }

  /**
   * Required delegate method enforcing the creation and assignment of [storeViewModel] in the proper sequence.
   *
   * @return the provided [StoreViewModel] instance.
   */
  protected abstract fun onCreateViewModel(): StoreComposeViewModel<StateT>

  /**
   * Optional delegate hook for performing explicit actions directly after the [viewModel] is created.
   *
   * @param viewModel - the viewModel
   */
  protected open fun onViewModelCreated(viewModel: StoreComposeViewModel<StateT>) = Unit

  override fun onStart() {
    super.onStart()
    dispatcher.dispatch(LifecycleAction.Starting(this))
  }

  override fun onResume() {
    super.onResume()
    managedSubs.resume()
    dispatcher.dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    dispatcher.dispatch(LifecycleAction.Pausing(this))
    managedSubs.pause()
    super.onPause()
  }

  override fun onStop() {
    dispatcher.dispatch(LifecycleAction.Stopping(this))
    managedSubs.cancel()
    super.onStop()
  }

  override fun onDestroy() {
    dispatcher.dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}