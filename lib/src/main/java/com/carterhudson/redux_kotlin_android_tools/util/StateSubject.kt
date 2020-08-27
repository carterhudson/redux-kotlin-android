package com.carterhudson.redux_kotlin_android_tools.util

/**
 * An interface that allows a [Renderer] to subscribe for selective state changes
 *
 * @param InStateT the original, non-transformed state
 */
interface StateSubject<InStateT : State> {

  /**
   * Supports subscribing to slices of [InStateT]
   *
   * @param OutStateT the state the [handle] cares about
   * @param handle receives [OutStateT] yielded by [select] and does something with it
   * @param distinct a flag to only call [handle] with distinct [OutStateT]s
   * @param select a function that transforms [InStateT] to [OutStateT]
   * @return a [Subscription]
   */
  fun <OutStateT : State> subscribe(
    handle: (OutStateT) -> Unit,
    distinct: Boolean = true,
    select: (InStateT) -> OutStateT
  ): Subscription
}