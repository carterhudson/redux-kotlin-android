package com.carterhudson.redux_kotlin_android.util.enhancer

import com.carterhudson.redux_kotlin_android.util.ReduxAction
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.SideEffectHandler
import com.carterhudson.redux_kotlin_android.util.SideEffectSubject
import org.reduxkotlin.Dispatcher
import org.reduxkotlin.Store
import org.reduxkotlin.StoreEnhancer
import org.reduxkotlin.StoreSubscription

fun <StateT : ReduxState> allowSideEffects(): StoreEnhancer<StateT> = { storeCreator ->
  { reducer, initialState, enhancer ->
    storeCreator(reducer, initialState, enhancer).let { store ->
      object : SideEffectSubject<StateT>, Store<StateT> by store {

        private var currentSideEffectHandlers: MutableSet<SideEffectHandler<StateT>> =
          mutableSetOf()

        private var safeSideEffectHandlers: MutableSet<SideEffectHandler<StateT>> =
          currentSideEffectHandlers.toMutableSet()

        fun ensureSafeSideEffectHandlers() {
          if (currentSideEffectHandlers == safeSideEffectHandlers) {
            safeSideEffectHandlers = currentSideEffectHandlers.toMutableSet()
          }
        }

        var isIssuing = false

        override var dispatch: Dispatcher = { action ->
          require(action is ReduxAction) {
            """In order to minimize errors like forgetting to wrap objects in actions,
              |kotlin-redux-android requires all dispatched actions to be of type ReduxAction.""".trimMargin()
          }

          store.dispatch(action).also {
            isIssuing = true
            currentSideEffectHandlers = safeSideEffectHandlers
            try {
              currentSideEffectHandlers.forEach { handler ->
                handler.handle(store.state, action)
              }
            } finally {
              isIssuing = false
            }
          }
        }

        override fun onSideEffect(sideEffectHandler: SideEffectHandler<StateT>): StoreSubscription {
          check(!isIssuing) {
            """Currently issuing actions to side-effect handlers. You may not add handlers
              |until the store is at rest.""".trimMargin()
          }

          var isSubscribed = true
          ensureSafeSideEffectHandlers()
          safeSideEffectHandlers.add(sideEffectHandler)

          return unsubscribeBlock@{
            if (!isSubscribed) {
              return@unsubscribeBlock
            }

            check(!isIssuing) {
              """Currently issuing actions to side-effect handlers. You may not remove handlers
                |until the store is at rest.""".trimMargin()
            }

            isSubscribed = false
            ensureSafeSideEffectHandlers()
            safeSideEffectHandlers.remove(sideEffectHandler)
          }
        }
      }
    }
  }
}