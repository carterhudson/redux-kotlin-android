package com.carterhudson.redux_kotlin_android_tools.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Allows [AppCompatActivity] instances to call [provideViewModel].
 *
 * Invokes [ViewModelProvider] on behalf of an [AppCompatActivity] to obtain a [ViewModel] instance tied
 * to said activity. The second parameter wants a [ViewModelProvider.Factory] so it can create an instance
 * if it's not already tracking one.
 *
 * The reified type parameter lets us access class info about the ViewModel
 * class this is being invoked for, which is what we need to obtain an instance from the factory.
 *
 * Since we're using an inline function in order to use the reified type parameter, we need to
 * specify that the [factoryDelegate] doesn't use the return keyword that would end the process early,
 * so we specify that it's crossinline
 */
inline fun <reified ViewModelT : ViewModel> AppCompatActivity.provideViewModel(crossinline factoryDelegate: () -> ViewModelT): ViewModelT =
  ViewModelProvider(
    this,
    createFactoryWithDelegate(factoryDelegate)
  ).get(ViewModelT::class.java)

/**
 * Allows [Fragment] instances to call [provideViewModel]
 */
inline fun <reified ViewModelT : ViewModel> Fragment.provideViewModel(crossinline factoryDelegate: () -> ViewModelT): ViewModelT =
  ViewModelProvider(
    this,
    createFactoryWithDelegate(factoryDelegate)
  ).get(ViewModelT::class.java)

/**
 * Creates a ViewModel factory.
 *
 * The factory delegate is a lambda that provides an instance of a ViewModel.
 * This whole flow lets us tie into Android's VM management via ViewModelProviders, but lets us
 * create the actual VM instance via dependency injection & dagger.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified ViewModelT : ViewModel> createFactoryWithDelegate(crossinline factoryDelegate: () -> ViewModelT): ViewModelProvider.Factory =
  object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
      when (modelClass) {
        ViewModelT::class.java -> factoryDelegate() as T

        else -> throw IllegalArgumentException("Unsupported model class: $modelClass")
      }
  }