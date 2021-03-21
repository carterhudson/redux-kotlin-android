package com.carterhudson.example.feature.todo

import com.carterhudson.redux_kotlin_android.util.ReduxState

data class ToDoState(val toDoItems: Set<String> = emptySet()) : ReduxState