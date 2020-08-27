package com.carterhudson.redux_kotlin_android_tools.util

interface Renderer<StateT : State> {
  fun render(state: StateT)
}