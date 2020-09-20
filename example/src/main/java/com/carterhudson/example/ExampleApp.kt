package com.carterhudson.example

import android.app.Application
import com.carterhudson.example.inject.ApplicationComponent
import com.carterhudson.example.inject.ApplicationModule
import com.carterhudson.example.inject.DaggerApplicationComponent

class ExampleApp : Application() {
  companion object {
    val injector: ApplicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(ApplicationModule())
        .build()
  }
}

