package com.carterhudson.redux_kotlin_android.presentation

import androidx.activity.ComponentActivity
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction

abstract class ReduxComposeActivity<StateT : ReduxState, ViewModelT : StoreViewModel<StateT>> :
  ComponentActivity() {
  abstract val storeViewModel: ViewModelT

  protected val dispatcher: TypesafeDispatcher by lazy { storeViewModel.dispatcher }

  override fun onStart() {
    super.onStart()
    dispatcher.dispatch(LifecycleAction.Starting(this))
  }

  override fun onResume() {
    super.onResume()
    dispatcher.dispatch(LifecycleAction.Resuming(this))
  }

  override fun onPause() {
    dispatcher.dispatch(LifecycleAction.Pausing(this))
    super.onPause()
  }

  override fun onStop() {
    dispatcher.dispatch(LifecycleAction.Stopping(this))
    super.onStop()
  }

  override fun onDestroy() {
    dispatcher.dispatch(LifecycleAction.Destroying(this))
    super.onDestroy()
  }
}