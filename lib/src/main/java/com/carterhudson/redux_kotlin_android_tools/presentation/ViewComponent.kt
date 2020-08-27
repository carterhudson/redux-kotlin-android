package com.carterhudson.redux_kotlin_android_tools.presentation

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import com.carterhudson.redux_kotlin_android_tools.util.Renderer
import com.carterhudson.redux_kotlin_android_tools.util.State
import com.carterhudson.redux_kotlin_android_tools.util.getColorCompat

abstract class ViewComponent<StateT : State> : Renderer<StateT> {

  protected open val binding: ViewBinding? = null

  fun root() = binding?.root

  override fun render(state: StateT) {
    //optional
  }

  protected fun requireContext(): Context = binding?.requireContext()
    ?: throw IllegalStateException("Binding has not been initialized; can't obtain context")

  protected fun getString(resId: Int): String = requireContext().getString(resId)

  fun ViewBinding.requireContext() =
    root.context ?: throw IllegalStateException("$this is not attached to a context.")

  fun ViewBinding.getString(
    @StringRes
    id: Int
  ): String = this.requireContext().getString(id)

  @ColorInt
  fun getColorIntFromResource(
    @ColorRes
    id: Int
  ): Int = requireContext().getColorCompat(id)
}