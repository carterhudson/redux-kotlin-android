package com.carterhudson.redux_kotlin_android.util

import com.carterhudson.redux_kotlin_android.util.enhancer.allowSideEffects
import org.reduxkotlin.Reducer
import org.reduxkotlin.StoreSubscription
import org.reduxkotlin.createStore

interface ReduxAction

interface ReduxState

fun <StateT : ReduxState> createStoreWithSideEffects(
  reducer: Reducer<StateT>,
  initialState: StateT
) = createStore(reducer, initialState, allowSideEffects())

/**
 * Used for pre-Jetpack Compose
 */
interface Renderer<StateT : ReduxState> {
  fun render(state: StateT)
}

/**
 * Handler for side-effects
 */
fun interface SideEffectHandler<StateT : ReduxState> {
  fun handle(state: StateT, action: ReduxAction)
}

/**
 * Since the Store keeps state, it is a subject
 */
fun interface SideEffectSubject<StateT : ReduxState> {
  fun onSideEffect(sideEffectHandler: SideEffectHandler<StateT>): StoreSubscription
}

inline fun <reified ClassT> Any.cast() = this as ClassT

inline fun <reified ClassT> Any.safeCast() = this as? ClassT