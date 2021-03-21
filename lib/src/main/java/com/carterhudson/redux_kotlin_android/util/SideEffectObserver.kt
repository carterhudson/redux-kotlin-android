package com.carterhudson.redux_kotlin_android.util

data class SideEffectObserver<StateT : ReduxState>(
  private val handler: SideEffectHandler<StateT>,
  private val sub: ManagedSubscription
) {
  fun notify(state: StateT, action: ReduxAction) {
    if (sub.isPaused() || sub.isCanceled()) {
      return
    }

    handler.onSideEffect(state, action)
  }
}