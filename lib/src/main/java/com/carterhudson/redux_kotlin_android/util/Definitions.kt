package com.carterhudson.redux_kotlin_android.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.carterhudson.redux_kotlin_android.presentation.StoreSubscriptionManager
import com.carterhudson.redux_kotlin_android.presentation.StoreViewModel
import com.carterhudson.redux_kotlin_android.util.enhancer.allowSideEffects
import org.reduxkotlin.Reducer
import org.reduxkotlin.StoreSubscription
import org.reduxkotlin.createStore

interface ReduxAction

interface ReduxState

fun <StateT : ReduxState> createStoreWithSideEffects(
  reducer: Reducer<StateT>,
  initialState: StateT
) = createStore(reducer, initialState, allowSideEffects())

interface Renderer<StateT : ReduxState> {
  fun render(state: StateT)
}

fun <StateT : ReduxState> Collection<StateObserver<StateT, *>>.notifyAll(next: StateT) =
  forEach {
    it.notify(next)
  }

fun <StateT : ReduxState> Collection<SideEffectObserver<StateT>>.notifyAll(
  state: StateT,
  action: ReduxAction
) = forEach {
  it.notify(state, action)
}

fun Collection<ManagedSubscription>.pause() = forEach(ManagedSubscription::pause)

fun Collection<ManagedSubscription>.resume() = forEach(ManagedSubscription::resume)

fun Collection<ManagedSubscription>.cancel() = forEach(ManagedSubscription::cancel)

fun MutableCollection<ManagedSubscription>.addAll(vararg subscriptions: ManagedSubscription) =
  addAll(subscriptions)

/**
 * Handler for side-effects
 */
fun interface SideEffectHandler<StateT : ReduxState> {
  fun onSideEffect(state: StateT, action: ReduxAction)
}

/**
 * Since the manager just proxies observation for the store, it's an observable
 */
fun interface SideEffectObservable<StateT : ReduxState> {
  fun addSideEffectHandler(sideEffectHandler: SideEffectHandler<StateT>): ManagedSubscription
}

/**
 * Since the Store keeps state, it is a subject
 */
fun interface SideEffectSubject<StateT : ReduxState> {
  fun onSideEffect(sideEffectHandler: SideEffectHandler<StateT>): StoreSubscription
}

inline fun <reified ClassT> Any.cast() = this as ClassT

inline fun <reified ClassT> Any.safeCast() = this as? ClassT

/**
 * An interface that allows a [Renderer] to subscribe for selective state changes
 *
 * @param InStateT the original, non-transformed state
 */
interface StateObservable<InStateT : ReduxState> {

  /**
   * Supports subscribing to slices of [InStateT]
   *
   * @param OutStateT the state the [handleState] cares about
   * @param handleState receives [OutStateT] yielded by [select] and does something with it
   * @param distinct a flag to only call [handleState] with distinct [OutStateT]s
   * @param select a function that transforms [InStateT] to [OutStateT]
   * @return a [ManagedSubscription]
   */
  fun <OutStateT : ReduxState> onStateChanged(
    select: (state: InStateT) -> OutStateT,
    distinct: Boolean = true,
    handleState: (state: OutStateT) -> Unit,
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
//inline fun <reified ViewModelT : ViewModel> AppCompatActivity.viewModelProviders(crossinline factoryDelegate: () -> ViewModelT): ViewModelT =
//  ViewModelProvider(
//    this,
//    createFactoryWithDelegate(factoryDelegate)
//  ).get(ViewModelT::class.java)
//
///**
// * Allows [Fragment] instances to call [viewModelProviders]
// */
//inline fun <reified ViewModelT : ViewModel> Fragment.viewModelProviders(crossinline factoryDelegate: () -> ViewModelT): ViewModelT =
//  ViewModelProvider(
//    this,
//    createFactoryWithDelegate(factoryDelegate)
//  ).get(ViewModelT::class.java)
//
///**
// * Creates a ViewModel factory.
// *
// * The factory delegate is a lambda that provides an instance of a ViewModel.
// * This whole flow lets us tie into Android's VM management via ViewModelProviders, but lets us
// * create the actual VM instance via dependency injection & dagger.
// */
//@Suppress("UNCHECKED_CAST")
//inline fun <reified ViewModelT : ViewModel> createFactoryWithDelegate(crossinline factoryDelegate: () -> ViewModelT): ViewModelProvider.Factory =

//class ReduxAndroid<StateT : State>(initialState: StateT, reducer: Reducer<StateT>) {
//  val store: Store<StateT>
//
//  val subscriptionManager: StoreSubscriptionManager<StateT>
//
//  val viewModelFactory: ViewModelProvider.Factory
//
//  init {
//    if (exists) {
//      throw IllegalStateException("An instance of ReduxAndroid already exists; please use that instance. If you need to create more than one instance (e.g. for testing purposes), use ::reset()")
//    }
//
//    store = createStoreWithSideEffects(reducer, initialState)
//    subscriptionManager = StoreSubscriptionManager(store)
@Suppress("UNCHECKED_CAST")
inline fun <reified StateT : ReduxState> viewModelFactory(subscriptionManager: StoreSubscriptionManager<StateT>): ViewModelProvider.Factory {
  return object : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return StoreViewModel(subscriptionManager) as T
    }
  }
}

//    exists = true
//  }

//  companion object {
//    private var exists = false
//
//    fun reset() {
//      exists = false
//    }
//  }
//}