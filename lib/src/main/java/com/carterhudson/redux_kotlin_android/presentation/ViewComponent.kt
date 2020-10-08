package com.carterhudson.redux_kotlin_android.presentation

import android.view.View
import com.carterhudson.redux_kotlin_android.util.Renderer
import com.carterhudson.redux_kotlin_android.util.State

/**
 * A class for managing UI.
 * Receives [StateT] updates via [render]
 *
 * Note: Many View Components can be composed into a single root View Component per controller.
 *
 * @param StateT the state the [ViewComponent] will [render]
 */
abstract class ViewComponent<StateT : State> : Renderer<StateT> {

  abstract fun root() : View

  /**
   * Function for receiving state & updating the UI
   *
   * @param state the current state the UI cares about
   */
  override fun render(state: StateT) {
    //optional
  }
}