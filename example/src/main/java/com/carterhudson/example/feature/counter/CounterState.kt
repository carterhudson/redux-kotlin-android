package com.carterhudson.example.feature.counter

import com.carterhudson.redux_kotlin_android.util.ReduxState

data class CounterState(val count: Int = 0) : ReduxState