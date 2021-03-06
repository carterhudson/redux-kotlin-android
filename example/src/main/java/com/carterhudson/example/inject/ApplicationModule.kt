package com.carterhudson.example.inject

import com.carterhudson.example.AppState
import com.carterhudson.example.feature.counter.counterStateReducer
import com.carterhudson.example.feature.todo.toDoStateReducer
import com.carterhudson.redux_kotlin_android.presentation.ReduxSubscriptionManager
import com.carterhudson.redux_kotlin_android.presentation.ReduxViewModel
import com.carterhudson.redux_kotlin_android.util.Action
import com.carterhudson.redux_kotlin_android.util.createStoreWithSideEffects
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
    reducerForActionType<AppState, Action> { state, action ->
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
  fun provideStoreSubManager(store: Store<AppState>) = ReduxSubscriptionManager(store)

  @Provides
  fun provideStoreViewModel(subMgr: ReduxSubscriptionManager<AppState>) = ReduxViewModel(subMgr)
}
