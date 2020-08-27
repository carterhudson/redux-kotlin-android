package com.carterhudson.example

import com.carterhudson.redux_kotlin_android_tools.util.Action

sealed class CounterAction : Action {
  data class Increment(val by: Int = 1) : CounterAction()
  data class Decrement(val by: Int = 1) : CounterAction()
}