package com.carterhudson.redux_kotlin_android.presentation

import androidx.lifecycle.ViewModel
import com.carterhudson.redux_kotlin_android.util.PostDispatchObservable
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.StateObservable

/**
 * A lifecycle-sensitive proxy / manager for a [ReduxStoreManager]. The [storeManager] is valid
 * only within the lifetime of a [ViewModel]. When [ViewModel.onCleared] is called, the
 * [storeManager] will be disposed, and cannot be reused.
 *
 * @param StateT the [State] descendant that is emitted by the [storeManager]
 * @property storeManager the [ReduxStoreManager] that is being managed / scoped.
 */
open class ReduxViewModel<StateT : State>(private val storeManager: ReduxStoreManager<StateT>) :
    ViewModel(),
    StateObservable<StateT> by storeManager,
    PostDispatchObservable<StateT> by storeManager {

    val dispatch = storeManager.dispatch

    /**
     * A [ViewModel] lifecycle method signifying the end of the [storeManager]'s lifetime.
     */
    override fun onCleared() {
        super.onCleared()
        storeManager.dispose()
    }
}