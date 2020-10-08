package com.carterhudson.redux_kotlin_android.presentation

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding

fun ViewBinding.requireContext() =
  root.context ?: throw IllegalStateException("$this is not attached to a context.")

fun ViewBinding.getString(
  @StringRes
  id: Int
): String = this.requireContext().getString(id)

@ColorInt
fun Context.getColorIntFromResource(
  @ColorRes
  id: Int
): Int = getColorCompat(id)

@ColorInt
fun Context.getColorCompat(
  @ColorRes
  id: Int
) = ContextCompat.getColor(this, id)