package com.carterhudson.redux_kotlin_android_tools.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import java.lang.ref.WeakReference

object CoroutineScopeHolder {

  private var coroutineScope: WeakReference<CoroutineScope> = GlobalScope.weak()

  fun setAsyncScope(scope: CoroutineScope) {
    coroutineScope = scope.weak()
  }

  fun getAsyncScope() = coroutineScope.get()
}