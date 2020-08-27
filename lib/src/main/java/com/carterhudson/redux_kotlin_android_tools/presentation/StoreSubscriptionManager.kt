package com.carterhudson.redux_kotlin_android_tools.presentation

import com.carterhudson.redux_kotlin_android_tools.util.*
import org.reduxkotlin.Store
import org.reduxkotlin.StoreSubscription

/**
 * An observable subject that is both a [StateSubject] and [CommandSubject]. Serves as a managed
 * proxy for handling [Transition]s emitted by the [store]
 *
 * @param StateT the [Store] state type
 * @property store the store instance that is subscribed to
 */
open class StoreSubscriptionManager<StateT : State>(private val store: Store<StateT>) :
  StateSubject<StateT>, ActionDispatcher {

  private val storeSubscription: StoreSubscription = store.subscribe {
    store.getState().let { state ->
      currentStateObservers = safeStateObservers
      currentStateObservers.notifyAll(state)
    }
  }

  private var currentStateObservers = mutableSetOf<StateObserver<StateT, *>>()
  private var safeStateObservers = currentStateObservers

  private fun ensureSafeStateObservers() {
    if (currentStateObservers === safeStateObservers) {
      safeStateObservers = currentStateObservers.toMutableSet()
    }
  }

  override fun dispatch(action: Action): Action = store.dispatch(action).cast()

  override fun <SlicedStateT : State> subscribe(
    handle: (SlicedStateT) -> Unit,
    distinct: Boolean,
    select: (StateT) -> SlicedStateT
  ): Subscription {

    var canceled = false
    var paused = false

    val observer = StateObserver(
      handle,
      distinct,
      select,
      { paused },
      { canceled }
    )

    ensureSafeStateObservers()
    safeStateObservers.add(observer)

    /* Emit state on subscribe */
    observer.notify(store.getState())

    return object : Subscription {
      override fun cancel() {
        if (isCanceled()) {
          return
        }

        canceled = true
        ensureSafeStateObservers()
        safeStateObservers.remove(observer)
      }

      override fun isCanceled(): Boolean = canceled

      override fun pause() {
        if (isCanceled() || isPaused()) {
          return
        }

        paused = true
      }

      override fun resume() {
        if (isCanceled() || !isPaused()) {
          return
        }

        paused = false
      }

      override fun isPaused(): Boolean = paused
    }
  }

  fun dispose() {
    storeSubscription()
    currentStateObservers.clear()
  }
}