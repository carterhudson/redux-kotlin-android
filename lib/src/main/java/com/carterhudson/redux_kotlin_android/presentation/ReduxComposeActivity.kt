package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction

abstract class ReduxComposeActivity<StateT : ReduxState, ViewModelT : StoreViewModel<StateT>> :
  ComponentActivity() {
  abstract val storeViewModel: ViewModelT

  protected val dispatcher: TypesafeDispatcher by lazy { storeViewModel.dispatcher }

  private val lifecycleStateEmitter = object : LifecycleObserver {
    @OnLifecycleEvent(ON_CREATE)
    fun created() {
      dispatcher.dispatch(LifecycleAction.Created(this@ReduxComposeActivity))
    }

    @OnLifecycleEvent(ON_START)
    fun started() {
      dispatcher.dispatch(LifecycleAction.Started(this@ReduxComposeActivity))
    }

    @OnLifecycleEvent(ON_RESUME)
    fun resumed() {
      dispatcher.dispatch(LifecycleAction.Resumed(this@ReduxComposeActivity))
    }

    @OnLifecycleEvent(ON_PAUSE)
    fun paused() {
      dispatcher.dispatch(LifecycleAction.Paused(this@ReduxComposeActivity))
    }

    @OnLifecycleEvent(ON_STOP)
    fun stopped() {
      dispatcher.dispatch(LifecycleAction.Stopped(this@ReduxComposeActivity))
    }

    @OnLifecycleEvent(ON_DESTROY)
    fun destroyed() {
      dispatcher.dispatch(LifecycleAction.Destroyed(this@ReduxComposeActivity))
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycle.addObserver(lifecycleStateEmitter)
  }
}