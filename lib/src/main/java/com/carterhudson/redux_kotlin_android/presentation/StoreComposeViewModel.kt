package com.carterhudson.redux_kotlin_android.presentation

import androidx.lifecycle.ViewModel
import com.carterhudson.redux_kotlin_android.util.ReduxAction
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.SideEffectSubject
import com.carterhudson.redux_kotlin_android.util.cast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.reduxkotlin.Store

open class StoreComposeViewModel<StateT : ReduxState>(
  private val store: Store<StateT>,
) : ViewModel() {

  private val _state = MutableStateFlow(store.getState())
  val state = _state

  private lateinit var _sideEffect: MutableStateFlow<SideEffect<StateT>>
  lateinit var sideEffect: StateFlow<SideEffect<StateT>>
    private set

  private val stateSubscription = store.subscribe {
    _state.value = store.getState()
  }

  data class SideEffect<StateT : ReduxState>(
    val state: StateT,
    val action: Any,
  )

  private val postDispatchSubscription = run {
    require(store is SideEffectSubject<*>) {
      """StoreViewModel requires the store to also be a SideEffectSubject. Please create
        |your store with the provided method: createStoreWithSideEffects(reducer, state).
      """.trimMargin()
    }

    store.cast<SideEffectSubject<StateT>>().onSideEffect { s, a ->

      val sideEffect = SideEffect(s, a)

      when {
        // initialize on first dispatch complete
        !::_sideEffect.isInitialized -> {
          _sideEffect = MutableStateFlow(sideEffect)
          this.sideEffect = _sideEffect
        }
        // emit on the rest
        else -> _sideEffect.value = sideEffect
      }
    }
  }

  val dispatcher: TypeSafeDispatcher = TypeSafeDispatcher { action: ReduxAction ->
    store.dispatch(action)
  }

  override fun onCleared() {
    super.onCleared()
    stateSubscription()
    postDispatchSubscription()
  }
}

