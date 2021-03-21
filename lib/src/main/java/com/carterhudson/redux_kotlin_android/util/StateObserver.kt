package com.carterhudson.redux_kotlin_android.util

/**
 * An Observer who selectively notifies its [handler] of [SlicedStateT] changes.
 *
 * @param SlicedStateT the state type the [renderer] cares about
 * @property renderer the [Renderer] that will be notified if [SlicedStateT] has changed
 * @property distinct a flag for determining whether the [renderer] should be notified only when
 * the [SlicedStateT] has changed.
 * @property selector a function for selectively transforming [StateT] to [SlicedStateT]
 */
data class StateObserver<StateT : ReduxState, SlicedStateT : ReduxState>(
  private val handler: (SlicedStateT) -> Unit,
  private val distinct: Boolean,
  private val selector: (StateT) -> SlicedStateT,
  private val sub: ManagedSubscription
) {

  private var prevState: SlicedStateT? = null

  /**
   * Invokes the [selector] to transform [StateT] to [SlicedStateT]. If the [distinct] flag
   * is true, and [state] does NOT differ from [prevState], no action will be taken. Otherwise
   * [render] will be called with the transformed state.
   *
   * @param state
   */
  fun notify(state: StateT) {
    if (sub.isPaused() || sub.isCanceled()) {
      return
    }

    selector(state).let { slice: SlicedStateT ->
      when {
        distinct && prevState == slice -> {
          /*no-op*/
        }

        else -> render(slice)
      }
    }
  }

  /**
   * Passes the sliced state to the state [handler]
   * Sets the [prevState] to the sliced [nextState]
   *
   * @param nextState the next state to be rendered and saved
   */
  private fun render(nextState: SlicedStateT) {
    handler(nextState)
    prevState = nextState
  }
}