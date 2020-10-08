package com.carterhudson.redux_kotlin_android.presentation

import com.carterhudson.redux_kotlin_android.util.ManagedSubscription
import com.carterhudson.redux_kotlin_android.util.PostDispatchHandler
import com.carterhudson.redux_kotlin_android.util.PostDispatchObservable
import com.carterhudson.redux_kotlin_android.util.PostDispatchObserver
import com.carterhudson.redux_kotlin_android.util.PostDispatchSubject
import com.carterhudson.redux_kotlin_android.util.State
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
open class ReduxSubscriptionManager<StateT : State>(private val store: Store<StateT>) :
  StateObservable<StateT>,
  PostDispatchObservable<StateT> {

  private var stateObservers = mutableSetOf<StateObserver<StateT, *>>()

  private var postDispatchObservers = mutableSetOf<PostDispatchObserver<StateT>>()

  private val storeSubscription: StoreSubscription = store.subscribe {
    store.getState().let(stateObservers::notifyAll)
  }

  private val storePostDispatchSubscription =
    store.safeCast<PostDispatchSubject<StateT>>()?.subscribe { state, action ->
      postDispatchObservers.notifyAll(state, action)
    }

  val dispatch = store.dispatch

  /**
   * Creates and adds a [StateObserver] to [stateObservers].
   *
   * @param SlicedStateT the mapped state that the [render] function cares about.
   * @param render the render method to be invoked on state changes
   * @param distinct flag indicating whether enforce distinct states
   * @param select function for [StateT] to [SlicedStateT]
   * @return a [ManagedSubscription]
   */
  override fun <SlicedStateT : State> subscribe(
    render: (SlicedStateT) -> Unit,
    distinct: Boolean,
    select: (StateT) -> SlicedStateT
  ): ManagedSubscription = object : ManagedSubscription() {}.also { sub ->
    StateObserver(render, distinct, select, sub).also { obs ->
      stateObservers.add(obs)
      obs.notify(store.getState())
    }
  }

  /**
   * Creates and adds a [PostDispatchObserver] to [postDispatchObservers]
   *
   * @param postDispatchHandler function that handles state and action after dispatching has completed.
   * @return a [ManagedSubscription]
   */
  override fun subscribe(postDispatchHandler: PostDispatchHandler<StateT>): ManagedSubscription =
    object : ManagedSubscription() {}.also {
      postDispatchObservers.add(PostDispatchObserver(postDispatchHandler, it))
    }

  /**
   * Invoked in order to clear all subscriptions.
   */
  fun dispose() {
    storeSubscription()
    storePostDispatchSubscription?.invoke()
    stateObservers.clear()
    postDispatchObservers.clear()
  }
}