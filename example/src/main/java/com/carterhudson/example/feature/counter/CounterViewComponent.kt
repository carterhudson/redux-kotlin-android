package com.carterhudson.example.feature.counter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.carterhudson.example.feature.counter.CounterAction.Decrement
import com.carterhudson.example.feature.counter.CounterAction.Increment
import com.carterhudson.example.databinding.CounterLayoutBinding
import com.carterhudson.redux_kotlin_android.presentation.ViewComponent
import org.reduxkotlin.Dispatcher

class CounterViewComponent(
  container: ViewGroup? = null,
  inflater: LayoutInflater,
  dispatch: Dispatcher
) : ViewComponent<CounterState>() {

  override val binding: CounterLayoutBinding =
    CounterLayoutBinding.inflate(inflater, container, false).apply {
      incrementButton.setOnClickListener {
        dispatch(Increment())
      }

      decrementButton.setOnClickListener {
        dispatch(Decrement())
      }
    }

  override fun render(state: CounterState) {
    binding.counterTextView.text = state.count.toString()
  }
}