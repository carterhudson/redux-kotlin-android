package com.carterhudson.redux_kotlin_android.presentation

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.lifecycle.LifecycleAction

abstract class ReduxActivity<StateT : ReduxState, RenderStateT : ReduxState> : ComponentActivity() {


}