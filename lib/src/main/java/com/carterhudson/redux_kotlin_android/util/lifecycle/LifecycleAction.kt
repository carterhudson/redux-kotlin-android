package com.carterhudson.redux_kotlin_android.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.carterhudson.redux_kotlin_android.util.ReduxAction

interface LifecycleOwnerReferenceHolder {
  val owner: LifecycleOwner
}

sealed class LifecycleAction : ReduxAction, LifecycleOwnerReferenceHolder {
  class Creating(override val owner: LifecycleOwner) : LifecycleAction()
  class Pausing(override val owner: LifecycleOwner) : LifecycleAction()
  class Resuming(override val owner: LifecycleOwner) : LifecycleAction()
  class Starting(override val owner: LifecycleOwner) : LifecycleAction()
  class Stopping(override val owner: LifecycleOwner) : LifecycleAction()
  class Destroying(override val owner: LifecycleOwner) : LifecycleAction()
}