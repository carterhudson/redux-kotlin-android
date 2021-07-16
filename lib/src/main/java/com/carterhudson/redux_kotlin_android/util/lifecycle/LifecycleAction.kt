package com.carterhudson.redux_kotlin_android.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.carterhudson.redux_kotlin_android.util.ReduxAction

interface LifecycleOwnerReferenceHolder {
  val owner: LifecycleOwner
}

sealed class LifecycleAction : ReduxAction, LifecycleOwnerReferenceHolder {
  class Created(override val owner: LifecycleOwner) : LifecycleAction()
  class Started(override val owner: LifecycleOwner) : LifecycleAction()
  class Resumed(override val owner: LifecycleOwner) : LifecycleAction()
  class Paused(override val owner: LifecycleOwner) : LifecycleAction()
  class Stopped(override val owner: LifecycleOwner) : LifecycleAction()
  class Destroyed(override val owner: LifecycleOwner) : LifecycleAction()
  sealed class FragmentAction : LifecycleAction() {
    class CreatingView(override val owner: LifecycleOwner) : FragmentAction()
    class ViewCreated(override val owner: LifecycleOwner) : FragmentAction()
  }
}