package com.carterhudson.redux_kotlin_android.presentation

import androidx.lifecycle.ViewModel
import com.carterhudson.redux_kotlin_android.util.SideEffectObservable
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.StateObservable

/**
 * Holds a reference to a [subscriptionManager] to manage subscriptions
 * across lifecycle events.
 *
 * @param StateT
 * @property subscriptionManager
 */
open class StoreViewModel<StateT : ReduxState>(private val subscriptionManager: StoreSubscriptionManager<StateT>) :
  ViewModel(),
  StateObservable<StateT> by subscriptionManager,
  SideEffectObservable<StateT> by subscriptionManager {

  val dispatch = subscriptionManager.dispatch

  /**
   * A [ViewModel] lifecycle method signifying the end of the [subscriptionManager]'s lifetime.
   */
  override fun onCleared() {
    super.onCleared()
    subscriptionManager.dispose()
  }
}