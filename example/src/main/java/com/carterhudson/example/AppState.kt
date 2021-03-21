package com.carterhudson.example

import com.carterhudson.example.feature.counter.CounterState
import com.carterhudson.example.feature.todo.ToDoState
import com.carterhudson.redux_kotlin_android.util.ReduxState

data class AppState(
  val counterState: CounterState = CounterState(),
  val toDoState: ToDoState = ToDoState()
) : ReduxState