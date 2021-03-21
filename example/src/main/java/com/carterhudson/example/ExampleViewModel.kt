package com.carterhudson.example

import com.carterhudson.redux_kotlin_android.presentation.StoreViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import org.reduxkotlin.Store

@HiltViewModel
class ExampleViewModel @Inject constructor(store: Store<AppState>) :
  StoreViewModel<AppState>(store)