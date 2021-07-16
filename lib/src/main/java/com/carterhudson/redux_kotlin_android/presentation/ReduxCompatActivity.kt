package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.safeCast

abstract class ReduxCompatActivity<StateT : ReduxState, RenderStateT : ReduxState> :
  AppCompatActivity() {

  protected val renderer: Renderer<RenderStateT>? by lazy { onCreateRenderer() }

  val storeViewModel: StoreViewModel<StateT> by lazy { onCreateViewModel() }

  val dispatcher: TypesafeDispatcher by lazy { storeViewModel.dispatcher }

  private val lifecycleStateEmitter = object : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun created() {
      dispatcher.dispatch(LifecycleAction.Created(this@ReduxCompatActivity))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun started() {
      dispatcher.dispatch(LifecycleAction.Started(this@ReduxCompatActivity))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resumed() {
      dispatcher.dispatch(LifecycleAction.Resumed(this@ReduxCompatActivity))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun paused() {
      dispatcher.dispatch(LifecycleAction.Paused(this@ReduxCompatActivity))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopped() {
      dispatcher.dispatch(LifecycleAction.Stopped(this@ReduxCompatActivity))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroyed() {
      dispatcher.dispatch(LifecycleAction.Destroyed(this@ReduxCompatActivity))
    }
  }

  /**
   * Delegate method.
   * Invoked in order to obtain a [StoreViewModel] instance.
   *
   * @return the provided [StoreViewModel] instance.
   */
  protected abstract fun onCreateViewModel(): StoreViewModel<StateT>

  /**
   * Delegate method (Required).
   * Invoked in order to obtain a [ViewRenderer] instance.
   *
   * @return the created [ViewRenderer] instance.// KTX
  implementation "androidx.activity:activity-ktx:1.2.2"
   */
  open fun onCreateRenderer(): Renderer<RenderStateT>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycle.addObserver(lifecycleStateEmitter)
    renderer?.safeCast<ViewRenderer<RenderStateT>>()?.let(::setContentView) ?: run {
      Log.d(
        this::class.java.simpleName,
        "No renderer provided; no content view being set"
      )
    }
  }

  /**
   * Overrides [AppCompatActivity.setContentView] to use [viewRenderer]
   *
   * @param viewRenderer - the component whose [ViewRenderer.root] will be used as the content view.
   */
  protected open fun setContentView(viewRenderer: ViewRenderer<RenderStateT>) {
    setContentView(viewRenderer.root())
  }
}