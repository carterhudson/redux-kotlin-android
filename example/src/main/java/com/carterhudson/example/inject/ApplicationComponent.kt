package com.carterhudson.example.inject

import com.carterhudson.example.AppState
import com.carterhudson.example.MainActivity
import com.carterhudson.redux_kotlin_android.presentation.ReduxViewModel
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
  fun inject(mainActivity: MainActivity)

  fun appViewModel(): ReduxViewModel<AppState>
}