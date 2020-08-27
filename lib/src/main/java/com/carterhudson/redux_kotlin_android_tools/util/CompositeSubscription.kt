package com.carterhudson.redux_kotlin_android_tools.util

/**
 * Serves as a single point for subscription management.
 *
 */
class CompositeSubscription : Subscription {

  /**
   * A set of all current subscriptions
   */
  val subscriptions: Set<Subscription> = mutableSetOf()

  private var canceled: Boolean = false
  private var paused: Boolean = false

  /**
   * Adds a subscription to [subscription] to [subscriptions]
   *
   * @param subscription the [Subscription] to be added
   */
  fun add(subscription: Subscription) {
    subscriptions.asMutable().add(subscription)
  }

  /**
   * Invokes a function to obtain a subscription, which will then be added to [subscriptions]
   *
   * @param subscribe the function that yields a [Subscription]
   */
  fun add(subscribe: () -> Subscription) {
    if (isCanceled()) {
      return
    }

    subscriptions.asMutable().add(subscribe())
  }

  /**
   * Invokes [Subscription.cancel] for all [subscriptions] and [MutableSet.clear]s
   * [subscriptions]
   *
   */
  override fun cancel() {
    if (isCanceled()) {
      return
    }

    with(subscriptions) {
      cancel()
      asMutable().clear()
    }
  }

  override fun isCanceled(): Boolean = canceled

  override fun pause() {
    if (isCanceled() || isPaused()) {
      return
    }

    paused = true
    subscriptions.pause()
  }

  override fun resume() {
    if (isCanceled() || !isPaused()) {
      return
    }

    subscriptions.resume()
  }

  override fun isPaused(): Boolean = paused
}