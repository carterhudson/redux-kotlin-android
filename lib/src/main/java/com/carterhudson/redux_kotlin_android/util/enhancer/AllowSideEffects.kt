package com.carterhudson.redux_kotlin_android.util.enhancer

import com.carterhudson.redux_kotlin_android.util.PostDispatchHandler
import com.carterhudson.redux_kotlin_android.util.PostDispatchSubject
import com.carterhudson.redux_kotlin_android.util.State
import org.reduxkotlin.Dispatcher
import org.reduxkotlin.Store
import org.reduxkotlin.StoreEnhancer
import org.reduxkotlin.StoreSubscription

fun <StateT : State> allowSideEffects(): StoreEnhancer<StateT> = { storeCreator ->
  { reducer, initialState, enhancer ->
    storeCreator(reducer, initialState, enhancer).let { store ->
      object : PostDispatchSubject<StateT>, Store<StateT> by store {

        private var currentPostDispatchHandlers: MutableSet<PostDispatchHandler<StateT>> =
          mutableSetOf()

        private var safePostDispatchHandlers: MutableSet<PostDispatchHandler<StateT>> =
          currentPostDispatchHandlers.toMutableSet()

        fun ensureSafeSideEffectHandlers() {
          if (currentPostDispatchHandlers == safePostDispatchHandlers) {
            safePostDispatchHandlers = currentPostDispatchHandlers.toMutableSet()
          }
        }

        var isIssuing = false

        override var dispatch: Dispatcher = { action ->
          store.dispatch(action).also {
            isIssuing = true
            currentPostDispatchHandlers = safePostDispatchHandlers
            try {
              currentPostDispatchHandlers.forEach { handler ->
                handler(store.state, action)
              }
            } finally {
              isIssuing = false
            }
          }
        }

        override fun subscribe(postDispatchHandler: PostDispatchHandler<StateT>): StoreSubscription {
          check(!isIssuing) {
            """Currently issuing actions to side-effect handlers. You may not add handlers
              |until the store is at rest.""".trimMargin()
          }

          var isSubscribed = true
          ensureSafeSideEffectHandlers()
          safePostDispatchHandlers.add(postDispatchHandler)

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
            safePostDispatchHandlers.remove(postDispatchHandler)
          }
        }
      }
    }
  }
}