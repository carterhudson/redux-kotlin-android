package com.carterhudson.redux_kotlin_android_tools.util

interface Action

typealias Subscriber<StateT> = (StateT) -> Unit

interface Subscription {
  fun cancel()
  fun isCanceled(): Boolean
  fun pause()
  fun resume()
  fun isPaused(): Boolean
}

interface ActionDispatcher {
  fun dispatch(action: Action): Action
}
//typealias Reducer<StateT> = (StateT, Action) -> StateT
//
//typealias ReducerForEvent<StateT, EventT> = (StateT, EventT) -> StateT
//
//inline fun <StateT, reified EventT> createReducer(
//  crossinline reducer: ReducerForEvent<StateT, EventT>
//): Reducer<StateT> = { state, action ->
//  when (action) {
//    is EventT -> reducer(state, action)
//    else -> state
//  }
//}
//
//typealias Dispatch = (Action) -> Action

interface StateProvider<StateT> {
  val getState: () -> StateT
}

//Type alias for a set of curried functions for partial application.
//typealias Middleware<StateT> = (store: MiddlewareStore<StateT>) -> (nextDispatcher: Dispatch) -> Dispatch

//typealias StoreCreator<StateT> = (initialState: StateT, reducer: Reducer<StateT>, enhancer: Any?) -> Store<StateT>
//
//typealias StoreEnhancer<StateT> = (StoreCreator<StateT>) -> StoreCreator<StateT>

typealias StateSelector<InStateT, OutStateT> = (InStateT) -> OutStateT

/**
 * This function takes variable number of functions with the same signature and yields a single,
 * composed function of the same type, essentially chaining them all together. It's just a glorified
 * wrapper around kotlin's [fold] function.
 */
fun Collection<Subscription>.cancel() = forEach(Subscription::cancel)

fun <StateT : State> Collection<StateObserver<StateT, *>>.notifyAll(next: StateT) = forEach {
  it.notify(next)
}

fun Collection<Subscription>.pause() = forEach(Subscription::pause)

fun Collection<Subscription>.resume() = forEach(Subscription::resume)