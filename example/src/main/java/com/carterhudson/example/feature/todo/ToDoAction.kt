package com.carterhudson.example.feature.todo

import com.carterhudson.redux_kotlin_android.util.Action

sealed class ToDoAction : Action {
  data class Add(val toDoItem: String) : ToDoAction()
  data class Remove(val toDoItem: String) : ToDoAction()
}