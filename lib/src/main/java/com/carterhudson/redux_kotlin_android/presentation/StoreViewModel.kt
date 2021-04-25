package com.carterhudson.redux_kotlin_android.presentation

import androidx.lifecycle.ViewModel
import com.carterhudson.redux_kotlin_android.util.ReduxAction
import com.carterhudson.redux_kotlin_android.util.ReduxState
import com.carterhudson.redux_kotlin_android.util.SideEffectSubject
import com.carterhudson.redux_kotlin_android.util.cast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.reduxkotlin.Store

open class StoreViewModel<StateT : ReduxState>(
  private val store: Store<StateT>,
) : ViewModel() {

  private val _state = MutableStateFlow(store.getState())
  val stateFlow = _state

  private lateinit var _sideEffect: MutableStateFlow<SideEffect<StateT>>
  lateinit var sideEffect: StateFlow<SideEffect<StateT>>
    private set

  private val stateSubscription = store.subscribe {
    _state.setValue(store.getState())
  }

  data class SideEffect<StateT : ReduxState>(
    val state: StateT,
    val action: Any,
  )

  private val postDispatchSubscription = run {
    require(store is SideEffectSubject<*>) {
      """StoreViewModel requires the store to also be a SideEffectSubject.
        |Please use the allowSideEffects() store enhancer.
      """.trimMargin()
    }

    store.cast<SideEffectSubject<StateT>>().subscribeSideEffectHandler { s, a ->

      val sideEffect = SideEffect(s, a)

      when {
        // initialize on first dispatch complete
        !::_sideEffect.isInitialized -> {
          _sideEffect = MutableStateFlow(sideEffect)
          this.sideEffect = _sideEffect
        }
        // emit on the rest
        else -> _sideEffect.setValue(sideEffect)
      }
    }
  }

  val dispatcher: TypesafeDispatcher = TypesafeDispatcher { action: ReduxAction ->
    store.dispatch(action)
  }

  override fun onCleared() {
    super.onCleared()
    stateSubscription()
    postDispatchSubscription()
  }
}

