package com.carterhudson.redux_kotlin_android

import com.carterhudson.redux_kotlin_android.presentation.ReduxSubscriptionManager
import com.carterhudson.redux_kotlin_android.util.State
import com.carterhudson.redux_kotlin_android.util.createStoreWithSideEffects
import io.kotest.core.spec.style.BehaviorSpec

class ReduxStoreManagerTest : BehaviorSpec() {

  val store = createStoreWithSideEffects({ state: State, _ -> state }, object : State {})

  init {
    Given("a redux store manager") {
      val storeManager = ReduxSubscriptionManager(store)

      And("a view component") {

      }

      When("it is subscribed to") {

      }
    }
  }
}