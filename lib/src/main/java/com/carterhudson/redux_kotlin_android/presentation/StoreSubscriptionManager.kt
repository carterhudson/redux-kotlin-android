package com.carterhudson.redux_kotlin_android.presentation

import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.SideEffectObserver
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.SideEffectHandler
import com.carterhudson.redux_kotlin_android.util.SideEffectObservable
import com.carterhudson.redux_kotlin_android.util.SideEffectSubject
import com.carterhudson.redux_kotlin_android.util.StateObservable
import com.carterhudson.redux_kotlin_android.util.StateObserver
import com.carterhudson.redux_kotlin_android.util.notifyAll
import com.carterhudson.redux_kotlin_android.util.safeCast
import org.reduxkotlin.Store
import org.reduxkotlin.StoreSubscription

/**
 * Manages [Store] subscriptions.
 *
 * @param StateT the type of state emitted by the [Store]
 * @property store the [Store] instance itself
 */
open class StoreSubscriptionManager<StateT : ReduxState>(private val store: Store<StateT>) :
  StateObservable<StateT>,
  SideEffectObservable<StateT> {

  private var stateObservers = mutableSetOf<StateObserver<StateT, *>>()

  private var sideEffectObservers = mutableSetOf<SideEffectObserver<StateT>>()

  private val storeStateSubscription: StoreSubscription = store.subscribe {
    store.getState().let(stateObservers::notifyAll)
  }

  private val storeSideEffectSubscription =
    store.safeCast<SideEffectSubject<StateT>>()?.onSideEffect { state, action ->
      sideEffectObservers.notifyAll(state, action)
    }

  val dispatch = store.dispatch

  /**
   * Creates and adds a [StateObserver] to [stateObservers].
   *
   * @param SlicedStateT the mapped state that the [handleState] function cares about.
   * @param handleState the render method to be invoked on state changes
   * @param distinct flag indicating whether enforce distinct states
   * @param select function for [StateT] to [SlicedStateT]
   * @return a [ManagedSubscription]
   */
  override fun <SlicedStateT : ReduxState> onStateChanged(
    select: (StateT) -> SlicedStateT,
    distinct: Boolean,
    handleState: (SlicedStateT) -> Unit,
  ): ManagedSubscription = object : ManagedSubscription() {}.also { sub ->
    StateObserver(handleState, distinct, select, sub).also { obs ->
      stateObservers.add(obs)
      // notify on subscribe with latest state
      obs.notify(store.getState())
    }
  }

  /**
   * Creates and adds a [SideEffectObserver] to [sideEffectObservers]
   *
   * @param sideEffectHandler function that handles state and action after dispatching has completed.
   * @return a [ManagedSubscription]
   */
  override fun addSideEffectHandler(sideEffectHandler: SideEffectHandler<StateT>): ManagedSubscription =
    object : ManagedSubscription() {}.also {
      sideEffectObservers.add(SideEffectObserver(sideEffectHandler, it))
    }

  /**
   * Invoked in order to clear all subscriptions.
   */
  fun dispose() {
    storeStateSubscription()
    storeSideEffectSubscription?.invoke()
    stateObservers.clear()
    sideEffectObservers.clear()
  }
}