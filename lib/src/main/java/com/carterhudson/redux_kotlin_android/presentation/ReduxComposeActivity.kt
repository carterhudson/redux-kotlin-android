package com.carterhudson.redux_kotlin_android.presentation

import com.carterhudson.redux_kotlin_android.util.ReduxState


abstract class ReduxComposeActivity<StateT : ReduxState> : ReduxActivity<StateT, Nothing>()