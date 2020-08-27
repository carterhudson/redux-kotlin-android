package com.carterhudson.redux_kotlin_android_tools.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carterhudson.redux_kotlin_android_tools.util.ActionDispatcher
import com.carterhudson.redux_kotlin_android_tools.util.CoroutineScopeHolder
import com.carterhudson.redux_kotlin_android_tools.util.State
import com.carterhudson.redux_kotlin_android_tools.util.StateSubject

/**
 * A lifecycle-sensitive proxy / manager for a [StoreSubscriptionManager]. The [subscriptionManager] is valid
 * only within the lifetime of a [ViewModel]. When [ViewModel.onCleared] is called, the
 * [subscriptionManager] will be disposed, and cannot be reused.
 *
 * @param StateT the [State] descendant that is emitted by the [subscriptionManager]
 * @property subscriptionManager the [StoreSubscriptionManager] that is being managed / scoped.
 */
open class StoreViewModel<StateT : State>(
  private val subscriptionManager: StoreSubscriptionManager<StateT>
) : ViewModel(),
    StateSubject<StateT> by subscriptionManager,
    ActionDispatcher by subscriptionManager {

  init {
    CoroutineScopeHolder.setAsyncScope(viewModelScope)
  }

  /**
   * A [ViewModel] lifecycle method signifying the end of the [subscriptionManager]'s lifetime.
   */
  override fun onCleared() {
    super.onCleared()
    subscriptionManager.dispose()
  }
}