package com.carterhudson.example

import com.carterhudson.redux_kotlin_android_tools.util.Action

sealed class ToDoAction : Action {
  data class Add(val toDoItem: String) : ToDoAction()
  data class Remove(val toDoItem: String) : ToDoAction()
}