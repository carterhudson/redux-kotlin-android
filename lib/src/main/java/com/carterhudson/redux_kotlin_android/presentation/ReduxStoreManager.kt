package com.carterhudson.redux_kotlin_android.presentation

import com.carterhudson.redux_kotlin_android.util.PostDispatchObservable
import com.carterhudson.redux_kotlin_android.util.PostDispatchObserver
import com.carterhudson.redux_kotlin_android.util.PostDispatchSubject
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.StateObservable
import com.carterhudson.redux_kotlin_android.util.StateObserver
import com.carterhudson.redux_kotlin_android.util.StoreManagerSubscription
import com.carterhudson.redux_kotlin_android.util.cast
import com.carterhudson.redux_kotlin_android.util.notifyAll
import org.reduxkotlin.Store
import org.reduxkotlin.StoreSubscription

/**
 * An observable subject that is both a [StateObservable] and [CommandSubject]. Serves as a managed
 * proxy for handling [Transition]s emitted by the [store]
 *
 * @param StateT the [Store] state type
 * @property store the store instance that is subscribed to
 */
open class ReduxStoreManager<StateT : State>(private val store: Store<StateT>) :
    StateObservable<StateT>,
    PostDispatchObservable<StateT> {

    private var stateObservers = mutableSetOf<StateObserver<StateT, *>>()
    private var actionObservers = mutableSetOf<PostDispatchObserver<StateT>>()

    private val storeSubscription: StoreSubscription = store.subscribe {
        store.getState().let(stateObservers::notifyAll)
    }

    private val storeActionSubscription =
        store.cast<PostDispatchSubject<StateT>>().subscribe { state, action ->
            actionObservers.notifyAll(state, action)
        }

    val dispatch = store.dispatch

    override fun <SlicedStateT : State> subscribe(
        handle: (SlicedStateT) -> Unit,
        distinct: Boolean,
        select: (StateT) -> SlicedStateT
    ): StoreManagerSubscription {

        val subscription = object : StoreManagerSubscription() {}

        val observer = StateObserver(
            handle,
            distinct,
            select,
            subscription
        )

        stateObservers.add(observer)

        /* Emit state on subscribe */
        observer.notify(store.getState())

        return subscription
    }

    override fun subscribe(postDispatchHandler: (StateT, Any) -> Unit): StoreManagerSubscription {

        val subscription = object : StoreManagerSubscription() {}

        actionObservers.add(PostDispatchObserver(postDispatchHandler, subscription))

        return subscription
    }

    fun dispose() {
        storeSubscription()
        storeActionSubscription()
        stateObservers.clear()
        actionObservers.clear()
    }
}