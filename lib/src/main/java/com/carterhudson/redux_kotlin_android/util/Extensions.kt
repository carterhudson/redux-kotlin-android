package com.carterhudson.redux_kotlin_android.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference
import kotlin.collections.set

//gets a layout inflater from a view's context
fun View.getInflater(): LayoutInflater = LayoutInflater.from(context)

//wrapper around lazy that turns off thread safety - should only be used for operations that
//will 100% only take place on a single thread (e.g. binding ui element references)
fun <ElementT> bindLazy(initializer: () -> ElementT): Lazy<ElementT> =
  lazy(LazyThreadSafetyMode.NONE, initializer)

//creates a state list drawable for carecredit checkmarks and applies it to the compound button
fun <CompoundButtonT : CompoundButton> CompoundButtonT.setDrawableStates(
  @DrawableRes
  checked: Int,
  @DrawableRes
  unchecked: Int
) =
  apply {
    StateListDrawable()
      .apply {
        addState(
          arrayOf(android.R.attr.state_checked).toIntArray(),
          context.getDrawable(checked)
        )

        addState(
          arrayOf(-android.R.attr.state_checked).toIntArray(),
          context.getDrawable(unchecked)
        )
      }
      .let(::setButtonDrawable)
  }

//used largely to trigger lazy binding on views that are otherwise untouched
fun <ViewT> ViewT.touch(): ViewT = this

//unchecked com.syf.cc.cast to type passed as reified parameter
inline fun <reified ClassT> Any.cast(): ClassT = (this as ClassT)

//toggles enabled status based on a predicate
fun View.enabledIf(predicate: () -> Boolean) {
  this.isEnabled = predicate()
}

//toggles presence based on a predicate
fun View.presentIf(predicate: () -> Boolean) {
  visibility = if (predicate()) View.VISIBLE else View.GONE
}

// sets text if input is not null and toggles visibility accordingly
fun TextView.setTextAndPresentIfNotNull(input: String?) {
  presentIf {
    !input.isNullOrEmpty()
  }

  input?.let { text = input }
}

//toggles visibility based on a predicate
fun View.visibleIf(predicate: () -> Boolean) {
  visibility = if (predicate()) View.VISIBLE else View.INVISIBLE
}

//launch an activity from an activity instance using parameterized type
inline fun <reified ActivityT : AppCompatActivity> AppCompatActivity.launch() {
  Intent(this, ActivityT::class.java).let(::startActivity)
}

//Less ugly way to com.syf.cc.observe LiveData in a Fragment
fun <ValueT> LifecycleOwner.observe(liveData: LiveData<ValueT>, onNext: (ValueT) -> Unit) {
  liveData.observe(this, Observer {
    onNext(it)
  })
}

fun View.gone(gone: Boolean) {
  visibility = if (gone) View.GONE else View.VISIBLE
}

fun View.visible(visible: Boolean) {
  visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun <ValueT> LiveData<ValueT>.asMutable() = cast<MutableLiveData<ValueT>>()

fun <ValueT> LiveData<ValueT>.emit(value: ValueT) {
  asMutable().value = value
}

inline fun Boolean.ifTrue(block: () -> Unit): Boolean = apply {
  if (this) block()
}

inline fun Boolean.ifFalse(block: () -> Unit): Boolean = apply {
  if (!this) block()
}

//fun exceptionLogger() = CoroutineExceptionHandler { _, error -> Timber.e(error) }

fun String.spanWithDrawable(
  context: Context,
  @DrawableRes
  drawableRes: Int,
  startIndex: Int,
  endIndex: Int
): SpannableString =
  ImageSpan(context, drawableRes, ImageSpan.ALIGN_BASELINE).let { imgSpan ->
    SpannableString(this).apply {
      setSpan(imgSpan, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }
  }

fun EditText.setReadOnly(readOnly: Boolean = true) {
  isFocusable = !readOnly
  isFocusableInTouchMode = !readOnly
  isLongClickable = !readOnly
  isCursorVisible = !readOnly
}

fun ViewBinding.requireContext() =
  root.context ?: throw IllegalStateException("$this is not attached to a context.")

fun View.getString(
  @StringRes
  id: Int
): String = context.getString(id)

fun Any?.isNotNull() = this != null

fun String?.isNotNullOrEmpty() = !this.isNullOrEmpty()

fun <ElementT> List<ElementT>?.nullIfEmpty() = if (this?.isEmpty() == true) null else this

enum class SizeUnit {
  PX,
  DP
}

fun Context.pxToDp(px: Number): Number = px.toFloat() / resources.displayMetrics.density

fun Context.dpToPx(dp: Number): Number = dp.toFloat() * resources.displayMetrics.density

fun Context.getWindowSize(sizeFormat: SizeUnit): Point =
  resources?.displayMetrics?.let { metrics ->
    Point().apply {
      x = when (sizeFormat) {
        SizeUnit.PX -> metrics.widthPixels
        SizeUnit.DP -> pxToDp(metrics.widthPixels).toInt()
      }

      y = when (sizeFormat) {
        SizeUnit.PX -> metrics.heightPixels
        SizeUnit.DP -> pxToDp(metrics.heightPixels).toInt()
      }
    }
  } ?: Point(0, 0)

fun Drawable.toBitmap(): Bitmap =
  //create the empty bitmap with w / h
  Bitmap.createBitmap(
    intrinsicWidth,
    intrinsicHeight,
    Bitmap.Config.ARGB_8888
  ).also { bitmap ->
    //draw the drawable to the canvas, and thus to the bitmap
    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
    draw(Canvas(bitmap))
  }

fun SparseBooleanArray.toMap(): Map<Int, Boolean> =
  mutableMapOf<Int, Boolean>().also { map ->
    for (idx: Int in 0 until size()) {
      map[keyAt(idx)] = valueAt(idx)
    }
  }

fun RecyclerView.ViewHolder.getInflater(): LayoutInflater = itemView.getInflater()

fun Set<Any>.asMutable() = this as MutableSet<Any>

@ColorInt
fun Context.getColorCompat(
  @ColorRes
  id: Int
) = ContextCompat.getColor(this, id)

fun <AnyT> AnyT.weak(): WeakReference<AnyT> = WeakReference(this)