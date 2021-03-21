package com.carterhudson.example.feature.counter

import com.carterhudson.redux_kotlin_android.util.ReduxAction

sealed class CounterAction : ReduxAction {
  data class Increment(val by: Int = 1) : CounterAction()
  data class Decrement(val by: Int = 1) : CounterAction()
}