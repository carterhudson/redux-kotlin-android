package com.carterhudson.example.feature.counter

import com.carterhudson.example.feature.counter.CounterAction.Decrement
import com.carterhudson.example.feature.counter.CounterAction.Increment
import org.reduxkotlin.combineReducers
import org.reduxkotlin.reducerForActionType

val nothingCounterReducer =
  reducerForActionType<CounterState, CounterAction> { state, action -> state }

val incrementCount = reducerForActionType<CounterState, Increment> { state, action ->
  state.copy(count = state.count + action.by)
}

val decrementCount = reducerForActionType<CounterState, Decrement> { state, action ->
  state.copy(count = state.count - action.by)
}

val counterStateReducer =
  combineReducers(
      incrementCount,
      decrementCount,
      nothingCounterReducer,
  )