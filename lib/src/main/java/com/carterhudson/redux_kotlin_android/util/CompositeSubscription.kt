package com.carterhudson.redux_kotlin_android.util

/**
 * Serves as a single point for subscription management.
 *
 */
class CompositeSubscription : StoreManagerSubscription() {

  /**
   * A set of all current subscriptions
   */
  private val subscriptions: MutableSet<StoreManagerSubscription> = mutableSetOf()

  /**
   * Adds a subscription to [storeManagerSubscription] to [subscriptions]
   *
   * @param storeManagerSubscription the [StoreManagerSubscription] to be added
   */
  fun add(storeManagerSubscription: StoreManagerSubscription) {
    subscriptions.asMutable().add(storeManagerSubscription)
  }

  /**
   * Invokes a function to obtain a subscription, which will then be added to [subscriptions]
   *
   * @param subscribe the function that yields a [StoreManagerSubscription]
   */
  fun add(subscribe: () -> StoreManagerSubscription) {
    if (isCanceled()) {
      return
    }

    subscriptions.asMutable().add(subscribe())
  }

  /**
   * Invokes [StoreManagerSubscription.cancel] for all [subscriptions] and [MutableSet.clear]s
   * [subscriptions]
   *
   */
  override fun cancel() {
    super.cancel()

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
    super.pause()

    if (isCanceled() || isPaused()) {
      return
    }

    subscriptions.pause()
  }

  override fun resume() {
    super.resume()

    if (canceled || !paused) {
      return
    }

    subscriptions.resume()
  }
}