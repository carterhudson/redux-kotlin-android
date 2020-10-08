package com.carterhudson.redux_kotlin_android.util

data class PostDispatchObserver<StateT: State>(
  private val handler: (StateT, Any) -> Unit,
  private val sub: ManagedSubscription
) {
  fun notify(state: StateT, action: Any) {
    if (sub.isPaused() || sub.isCanceled()) {
      return
    }

    handler(state, action)
  }
}