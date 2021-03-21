package com.carterhudson.redux_kotlin_android.presentation

import android.view.View
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.ReduxState

/**
 * A class for managing UI.
 * Receives [StateT] updates via [render]
 *
 * Note: Many View Components can be composed into a single root View Component per controller.
 *
 * @param StateT the state the [ViewRenderer] will [render]
 */
abstract class ViewRenderer<StateT : ReduxState> : Renderer<StateT> {

  abstract fun root() : View

  /**
   * Function for receiving state & updating the UI
   *
   * @param state the current state the UI cares about
   */
  override fun render(state: StateT) = Unit
}