package com.carterhudson.example.feature.todo

import com.carterhudson.redux_kotlin_android.util.ReduxAction

sealed class ToDoAction : ReduxAction {
  data class Add(val toDoItem: String) : ToDoAction()
  data class Remove(val toDoItem: String) : ToDoAction()
}