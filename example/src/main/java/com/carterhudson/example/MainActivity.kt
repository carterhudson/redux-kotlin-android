package com.carterhudson.example

import android.os.Bundle
import android.view.LayoutInflater
import com.carterhudson.example.databinding.CounterLayoutBinding
import com.carterhudson.redux_kotlin_android_tools.presentation.ReduxActivity
import com.carterhudson.redux_kotlin_android_tools.presentation.StoreViewModel
import com.carterhudson.redux_kotlin_android_tools.presentation.ViewComponent
import org.reduxkotlin.combineReducers
import org.reduxkotlin.reducerForActionType
import javax.inject.Inject

val nothingCounterReducer =
  reducerForActionType<CounterState, CounterAction> { state, action -> state }

val incrementCount = reducerForActionType<CounterState, CounterAction.Increment> { state, action ->
  state.copy(count = state.count + action.by)
}

val decrementCount = reducerForActionType<CounterState, CounterAction.Decrement> { state, action ->
  state.copy(count = state.count - action.by)
}

val counterStateReducer =
  combineReducers(
    incrementCount,
    decrementCount,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
    nothingCounterReducer,
  )

fun <ElementT> Set<ElementT>.add(element: ElementT): Set<ElementT> =
  toMutableSet().apply { add(element) }

fun <ElementT> Set<ElementT>.remove(element: ElementT): Set<ElementT> =
  toMutableSet().apply { remove(element) }

val nothingToDoReducer = reducerForActionType<ToDoState, ToDoAction> { state, action -> state }

val removeToDoItem = reducerForActionType<ToDoState, ToDoAction.Remove> { state, action ->
  state.copy(toDoItems = state.toDoItems.remove(action.toDoItem))
}

val addToDoItem = reducerForActionType<ToDoState, ToDoAction.Add> { state, action ->
  state.copy(toDoItems = state.toDoItems.add(action.toDoItem))
}

val toDoStateReducer =
  combineReducers(
    removeToDoItem,
    addToDoItem,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
    nothingToDoReducer,
  )

@Inject lateinit var storeViewModel: StoreViewModel<AppState>

class CounterViewComponent(inflater: LayoutInflater) : ViewComponent<CounterState>() {
  override val binding: CounterLayoutBinding = CounterLayoutBinding.inflate(inflater)
}

class MainActivity : ReduxActivity<AppState, CounterState>() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onCreateViewModel(): StoreViewModel<AppState> = storeViewModel

  override fun onCreateViewComponent(): ViewComponent<CounterState> =
    CounterViewComponent(layoutInflater)

  override fun onSelectState(state: AppState): CounterState = state.counterState
}
