package com.carterhudson.redux_kotlin_android_tools.util.lifecycle

import android.os.Bundle
import com.carterhudson.redux_kotlin_android_tools.util.Action

sealed class LifecycleAction : Action {
  sealed class Activity {
    object Paused : LifecycleAction()
    object Resumed : LifecycleAction()
    object Started : LifecycleAction()
    object Stopped : LifecycleAction()
    object Destroyed : LifecycleAction()
  }

  sealed class Fragment : LifecycleAction() {
    object ViewCreated : Fragment()
    object Paused : LifecycleAction()
    object Resumed : LifecycleAction()
    object Started : LifecycleAction()
    object Stopped : LifecycleAction()
    object Destroyed : LifecycleAction()
  }

  data class ArgumentsReceived(val bundle: Bundle) : LifecycleAction()
}