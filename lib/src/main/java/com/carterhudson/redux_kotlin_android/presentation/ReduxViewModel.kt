package com.carterhudson.redux_kotlin_android.presentation

import androidx.lifecycle.ViewModel
import com.carterhudson.redux_kotlin_android.util.PostDispatchObservable
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.StateObservable

/**
 * Holds a reference to a [subscriptionManager] to manage subscriptions
 * across lifecycle events.
 *
 * @param StateT
 * @property subscriptionManager
 */
open class ReduxViewModel<StateT : State>(private val subscriptionManager: ReduxSubscriptionManager<StateT>) :
  ViewModel(),
  StateObservable<StateT> by subscriptionManager,
  PostDispatchObservable<StateT> by subscriptionManager {

  val dispatch = subscriptionManager.dispatch

  /**
   * A [ViewModel] lifecycle method signifying the end of the [subscriptionManager]'s lifetime.
   */
  override fun onCleared() {
    super.onCleared()
    subscriptionManager.dispose()
  }
}