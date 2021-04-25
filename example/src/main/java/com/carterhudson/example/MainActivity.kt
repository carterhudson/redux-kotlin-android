package com.carterhudson.example

import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.carterhudson.example.feature.counter.CounterState
import com.carterhudson.example.feature.counter.CounterViewRenderer
import com.carterhudson.redux_kotlin_android.presentation.ReduxCompatActivity
import com.carterhudson.redux_kotlin_android.presentation.StoreViewModel
import com.carterhudson.redux_kotlin_android.presentation.ViewRenderer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : ReduxCompatActivity<AppState, CounterState>() {
  init {
    lifecycleScope.launchWhenResumed {
      storeViewModel.stateFlow.collect {
        renderer?.render(it.counterState)
      }
    }

    lifecycleScope.launchWhenResumed {
      storeViewModel.sideEffect.collect {
        Toast
          .makeText(
            this@MainActivity,
            "Side effect triggered for ${it.action}!",
            Toast.LENGTH_SHORT
          )
          .show()
      }
    }
  }

  override fun onCreateViewModel(): StoreViewModel<AppState> {
    return viewModels<ExampleViewModel>().value
  }

  override fun onCreateRenderer(): ViewRenderer<CounterState> {
    return CounterViewRenderer(inflater = layoutInflater, dispatcher = dispatcher)
  }
}
