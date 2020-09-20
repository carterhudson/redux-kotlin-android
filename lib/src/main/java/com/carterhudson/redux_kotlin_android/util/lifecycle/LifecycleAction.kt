package com.carterhudson.redux_kotlin_android.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.carterhudson.redux_kotlin_android.util.Action

interface LifecycleOwnerReferenceHolder {
  val owner: LifecycleOwner
}

sealed class LifecycleAction : Action, LifecycleOwnerReferenceHolder {
  class CreatingView(override val owner: LifecycleOwner) : LifecycleAction()
  class ViewCreated(override val owner: LifecycleOwner) : LifecycleAction()
  class Pausing(override val owner: LifecycleOwner) : LifecycleAction()
  class Resuming(override val owner: LifecycleOwner) : LifecycleAction()
  class Starting(override val owner: LifecycleOwner) : LifecycleAction()
  class Stopping(override val owner: LifecycleOwner) : LifecycleAction()
  class Destroying(override val owner: LifecycleOwner) : LifecycleAction()
}