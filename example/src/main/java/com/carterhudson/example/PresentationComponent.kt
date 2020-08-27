package com.carterhudson.example

import dagger.Component

@Component(modules = [PresentationModule::class])
interface PresentationComponent {
  fun inject(mainActivity: MainActivity)
}