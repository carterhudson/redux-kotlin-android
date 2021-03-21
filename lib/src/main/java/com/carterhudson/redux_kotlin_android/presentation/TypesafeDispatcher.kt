package com.carterhudson.redux_kotlin_android.presentation

import com.carterhudson.redux_kotlin_android.util.ReduxAction

// enforces type-safety for action dispatching
fun interface TypesafeDispatcher {
  fun dispatch(action: ReduxAction)
}