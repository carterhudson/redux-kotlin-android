package com.carterhudson.example.feature.counter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carterhudson.example.databinding.CounterLayoutBinding
import com.carterhudson.example.feature.counter.CounterAction.Decrement
import com.carterhudson.example.feature.counter.CounterAction.Increment
import com.carterhudson.redux_kotlin_android.presentation.TypesafeDispatcher
import com.carterhudson.redux_kotlin_android.presentation.ViewRenderer

class CounterViewRenderer(
  container: ViewGroup? = null,
  inflater: LayoutInflater,
  dispatcher: TypesafeDispatcher
) : ViewRenderer<CounterState>() {

  val binding: CounterLayoutBinding =
    CounterLayoutBinding.inflate(inflater, container, false).apply {
      incrementButton.setOnClickListener {
        dispatcher.dispatch(Increment())
      }

      decrementButton.setOnClickListener {
        dispatcher.dispatch(Decrement())
      }
    }

  override fun root(): View = binding.root

  override fun render(state: CounterState) {
    binding.counterTextView.text = state.count.toString()
  }
}