package com.carterhudson.example.inject

import com.carterhudson.example.AppState
import com.carterhudson.example.feature.counter.counterStateReducer
import com.carterhudson.example.feature.todo.toDoStateReducer
import com.carterhudson.redux_kotlin_android.util.ReduxAction
import com.carterhudson.redux_kotlin_android.util.createStoreWithSideEffects
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.reduxkotlin.Reducer
import org.reduxkotlin.reducerForActionType

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
  @Provides
  fun provideAppState() = AppState()

  @Provides
  fun provideAppStateReducer(): Reducer<@JvmSuppressWildcards AppState> =
    reducerForActionType<AppState, ReduxAction> { state, action ->
      state.copy(
        counterState = counterStateReducer(state.counterState, action),
        toDoState = toDoStateReducer(state.toDoState, action)
      )
    }

  @Provides
  fun provideStore(
    reducer: Reducer<@JvmSuppressWildcards AppState>,
    state: AppState
  ) = createStoreWithSideEffects(reducer, state)
}
