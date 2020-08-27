package com.carterhudson.example

import com.carterhudson.redux_kotlin_android_tools.presentation.StoreSubscriptionManager
import com.carterhudson.redux_kotlin_android_tools.presentation.StoreViewModel
import com.carterhudson.redux_kotlin_android_tools.util.Action
import dagger.Module
import dagger.Provides
import org.reduxkotlin.Reducer
import org.reduxkotlin.Store
import org.reduxkotlin.createStore
import org.reduxkotlin.reducerForActionType

@Module
class PresentationModule {
  @Provides
  fun provideAppState() = AppState()

  @Provides
  fun provideAppStateReducer(): Reducer<AppState> = reducerForActionType<AppState, Action> { state, action ->
    state.copy(
      counterState = counterStateReducer(state.counterState, action),
      toDoState = toDoStateReducer(state.toDoState, action)
    )
  }

  @Provides
  fun provideStore(reducer: @JvmSuppressWildcards Reducer<AppState>, state: AppState) = createStore(reducer, state)

  @Provides
  fun provideStoreSubManager(store: Store<AppState>) = StoreSubscriptionManager(store)

  @Provides
  fun provideStoreViewModel(subMgr: StoreSubscriptionManager<AppState>) = StoreViewModel(subMgr)
}
