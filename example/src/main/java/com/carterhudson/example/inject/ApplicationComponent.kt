package com.carterhudson.example.inject

import com.carterhudson.example.MainActivity
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
  fun inject(mainActivity: MainActivity)
}