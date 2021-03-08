package com.carterhudson.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.carterhudson.example.feature.counter.CounterState
import com.carterhudson.example.feature.counter.CounterViewRenderer
import com.carterhudson.redux_kotlin_android.presentation.ReduxActivity
import com.carterhudson.redux_kotlin_android.presentation.StoreViewModel
import com.carterhudson.redux_kotlin_android.presentation.ViewRenderer
import javax.inject.Inject

class MainActivity : ReduxActivity<AppState, CounterState>() {

  @Inject lateinit var factory: ViewModelProvider.Factory

  override fun onCreate(savedInstanceState: Bundle?) {
    ExampleApp.injector.inject(this)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateViewModel(): StoreViewModel<AppState> {
    return viewModels<StoreViewModel<AppState>> { factory }.value
  }

  override fun onCreateRenderer(): ViewRenderer<CounterState> {
    return CounterViewRenderer(inflater = layoutInflater, dispatch = dispatch)
  }

  override fun onSelectState(state: AppState): CounterState {
    return state.counterState
  }

  override fun performSideEffect(
    state: AppState,
    action: Any
  ) {
    Toast.makeText(this, "Side effect triggered for $action!", Toast.LENGTH_SHORT).show()
  }
}
