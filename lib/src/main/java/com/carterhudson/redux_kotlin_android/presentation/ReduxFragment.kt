package com.carterhudson.redux_kotlin_android.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction
import com.carterhudson.redux_kotlin_android.util.safeCast

abstract class ReduxFragment<StateT : ReduxState, RenderStateT : ReduxState> : Fragment() {

  private lateinit var storeViewModel: StoreViewModel<StateT>
  private var renderer: Renderer<RenderStateT>? = null
  val dispatcher: TypesafeDispatcher by lazy { storeViewModel.dispatcher }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateViewModel().also { storeViewModel = it }
    onViewModelCreated(storeViewModel)
  }

  abstract fun onCreateViewModel(): StoreViewModel<StateT>

  open fun onViewModelCreated(viewModel: StoreViewModel<StateT>) = Unit

  /**
   * Overridden from [Fragment.onCreateView].
   * Assigns [renderer] reference.
   * Dispatches [LifecycleAction.FragmentAction.CreatingView].
   *
   * @return the root view of the created [renderer]
   */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? = onCreateRenderer(inflater, container, savedInstanceState)
      .also { renderer = it }
      .also { dispatcher.dispatch(LifecycleAction.FragmentAction.CreatingView(this)) }
      .also { onRendererCreated(renderer) }
      ?.safeCast<ViewRenderer<RenderStateT>>()
      ?.root()

  /**
   * Invoked in order to obtain a [ViewRenderer] instance.
   * Called from [Fragment.onCreateView]
   *
   * @param inflater layout inflater provided by [Fragment.onCreateView]
   * @param container container provided by [Fragment.onCreateView]
   * @param savedInstanceState bundle provided by [Fragment.onCreateView]
   * @return the created [ViewRenderer] instance.
   */
  open fun onCreateRenderer(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): Renderer<RenderStateT>? = null

  open fun onRendererCreated(renderer: Renderer<RenderStateT>?) = Unit

  /**
   * Overridden from [Fragment.onViewCreated]
   * Handles subscribing to state & side effects
   * Dispatches [LifecycleAction.FragmentAction.ViewCreated]
   */
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    storeViewModel.dispatcher.dispatch(LifecycleAction.FragmentAction.ViewCreated(this))
  }

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