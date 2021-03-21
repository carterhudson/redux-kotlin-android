package com.carterhudson.example.inject

import com.carterhudson.example.AppState
import com.carterhudson.example.feature.counter.counterStateReducer
import com.carterhudson.example.feature.todo.toDoStateReducer
import com.carterhudson.redux_kotlin_android.presentation.StoreSubscriptionManager
import com.carterhudson.redux_kotlin_android.util.ReduxAction
import com.carterhudson.redux_kotlin_android.util.createStoreWithSideEffects
import com.carterhudson.redux_kotlin_android.util.viewModelFactory
import dagger.Module
import dagger.Provides
import org.reduxkotlin.Reducer
import org.reduxkotlin.Store
import org.reduxkotlin.reducerForActionType

@Module
class ApplicationModule {
  @Provides
  fun provideAppState() = AppState()

  @Provides
  fun provideAppStateReducer(): Reducer<AppState> =
    reducerForActionType<AppState, ReduxAction> { state, action ->
      state.copy(
        counterState = counterStateReducer(state.counterState, action),
        toDoState = toDoStateReducer(state.toDoState, action)
      )
    }

  @Provides
  fun provideStore(
    reducer: @JvmSuppressWildcards Reducer<AppState>,
    state: AppState
  ) = createStoreWithSideEffects(reducer, state)

  @Provides
  fun provideStoreSubManager(store: Store<AppState>) = StoreSubscriptionManager(store)

  @Provides
  fun provideViewModelFactory(subscriptionManager: StoreSubscriptionManager<AppState>) =
    viewModelFactory(subscriptionManager)
}
