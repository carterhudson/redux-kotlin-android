package com.carterhudson.example

import com.carterhudson.redux_kotlin_android_tools.util.State

data class ToDoState(val toDoItems: Set<String> = emptySet()) : State