package com.carterhudson.example.feature.todo

import com.carterhudson.example.feature.todo.ToDoAction.Add
import com.carterhudson.example.feature.todo.ToDoAction.Remove
import org.reduxkotlin.combineReducers
import org.reduxkotlin.reducerForActionType

fun <ElementT> Set<ElementT>.add(element: ElementT): Set<ElementT> =
  toMutableSet().apply { add(element) }

fun <ElementT> Set<ElementT>.remove(element: ElementT): Set<ElementT> =
  toMutableSet().apply { remove(element) }

val nothingToDoReducer = reducerForActionType<ToDoState, ToDoAction> { state, action -> state }

val removeToDoItem = reducerForActionType<ToDoState, Remove> { state, action ->
  state.copy(toDoItems = state.toDoItems.remove(action.toDoItem))
}

val addToDoItem = reducerForActionType<ToDoState, Add> { state, action ->
  state.copy(toDoItems = state.toDoItems.add(action.toDoItem))
}

val toDoStateReducer =
  combineReducers(
      removeToDoItem,
      addToDoItem,
      nothingToDoReducer,
  )