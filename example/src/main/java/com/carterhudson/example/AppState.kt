package com.carterhudson.example

import com.carterhudson.redux_kotlin_android_tools.util.State

data class AppState(
  val counterState: CounterState = CounterState(),
  val toDoState: ToDoState = ToDoState()
) : State