package com.carterhudson.redux_kotlin_android.util

open class StoreManagerSubscription {

  protected var canceled: Boolean = false
  protected var paused: Boolean = false

  open fun cancel() {
    if (isCanceled()) {
      return
    }

    canceled = true
  }

  open fun isCanceled(): Boolean = canceled

  open fun pause() {
    if (isPaused() || isCanceled()) {
      return
    }

    paused = true
  }

  open fun resume() {
    if (!isPaused() || isCanceled()) {
      return
    }

    paused = false
  }

  fun isPaused(): Boolean = paused
}