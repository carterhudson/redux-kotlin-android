package com.carterhudson.redux_kotlin_android.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carterhudson.redux_kotlin_android.util.enhancer.allowSideEffects
import org.reduxkotlin.Reducer
import org.reduxkotlin.StoreSubscription
import org.reduxkotlin.createStore

interface Action

interface State

fun <StateT : State> createStoreWithSideEffects(
  reducer: Reducer<StateT>,
  initialState: StateT
) = createStore(reducer, initialState, allowSideEffects())

interface Renderer<StateT : State> {
  fun render(state: StateT)
}

typealias StateSelector<InStateT, OutStateT> = (InStateT) -> OutStateT

fun <StateT : State> Collection<StateObserver<StateT, *>>.notifyAll(next: StateT) =
  forEach {
    it.notify(next)
  }

fun <StateT : State> Collection<PostDispatchObserver<StateT>>.notifyAll(
  state: StateT,
  action: Any
) = forEach { it.notify(state, action) }

fun Collection<ManagedSubscription>.pause() = forEach(ManagedSubscription::pause)

fun Collection<ManagedSubscription>.resume() = forEach(ManagedSubscription::resume)

fun Collection<ManagedSubscription>.cancel() = forEach(ManagedSubscription::cancel)

fun MutableCollection<ManagedSubscription>.addAll(vararg subscriptions: ManagedSubscription) =
  addAll(subscriptions)

typealias PostDispatchHandler<StateT> = (StateT, Any) -> Unit

/**
 * Since the manager just proxies observation for the store, it's an observable
 */
interface PostDispatchObservable<StateT : State> {
  fun subscribe(postDispatchHandler: PostDispatchHandler<StateT>): ManagedSubscription
}

/**
 * Since the Store keeps state, it is a subject
 */
interface PostDispatchSubject<StateT : State> {
  fun subscribe(postDispatchHandler: PostDispatchHandler<StateT>): StoreSubscription
}

inline fun <reified ClassT> Any.cast() = this as ClassT

inline fun <reified ClassT> Any.safeCast() = this as? ClassT

/**
 * An interface that allows a [Renderer] to subscribe for selective state changes
 *
 * @param InStateT the original, non-transformed state
 */
interface StateObservable<InStateT : State> {

  /**
   * Supports subscribing to slices of [InStateT]
   *
   * @param OutStateT the state the [handle] cares about
   * @param handle receives [OutStateT] yielded by [select] and does something with it
   * @param distinct a flag to only call [handle] with distinct [OutStateT]s
   * @param select a function that transforms [InStateT] to [OutStateT]
   * @return a [ManagedSubscription]
   */
  fun <OutStateT : State> subscribe(
    render: (state: OutStateT) -> Unit,
    distinct: Boolean = true,
    select: (state: InStateT) -> OutStateT
  ): ManagedSubscription
}

/**
 * Allows [AppCompatActivity] instances to call [viewModelProviders].
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
inline fun <reified ViewModelT : ViewModel> AppCompatActivity.viewModelProviders(crossinline factoryDelegate: () -> ViewModelT): ViewModelT =
  ViewModelProvider(
    this,
    createFactoryWithDelegate(factoryDelegate)
  ).get(ViewModelT::class.java)

/**
 * Allows [Fragment] instances to call [viewModelProviders]
 */
inline fun <reified ViewModelT : ViewModel> Fragment.viewModelProviders(crossinline factoryDelegate: () -> ViewModelT): ViewModelT =
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