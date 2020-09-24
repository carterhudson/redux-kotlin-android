### Tools for working with [redux-kotlin](https://github.com/reduxkotlin/redux-kotlin) in an Android environment.
[![](https://jitpack.io/v/carterhudson/redux-kotlin-android.svg)](https://jitpack.io/#carterhudson/redux-kotlin-android)

# Getting Started
This library provides a ready-made framework for working with Redux in a Kotlin Android environment. Getting off the ground is relatively easy, given that you are familiar with Redux or transactional state management. It provides two main entry points in `ReduxActivity` and `ReduxFragment`:
```kotlin
class MainActivity : ReduxActivity<AppState, CounterState>() {  
  
  override fun onCreate(savedInstanceState: Bundle?) {  
    super.onCreate(savedInstanceState)  
    ExampleApp.injector.inject(this)  
  }  
  
  override fun onCreateViewModel(): ReduxViewModel<AppState> = ExampleApp.injector.appViewModel()  
  
  override fun onCreateViewComponent(): ViewComponent<CounterState> =  
    CounterViewComponent(inflater = layoutInflater, dispatch = reduxViewModel.dispatch)  
  
  override fun onSelectState(state: AppState): CounterState = state.counterState  
  
  override fun performSideEffect(  
      state: AppState,  
      action: Any  
  ) {  
    Toast.makeText(this, "Side effect triggered for $action!", Toast.LENGTH_SHORT).show()  
  }  
}
```
## State
State is exactly what it sounds like - just values. It should be an immutable data class that implements the `State` interface represents the current state of your application or screen. A simple `State` hierarchy for an application might look something like this:
```kotlin
data class CounterState(val count: Int = 0) : State

data class ToDoState(val toDoItems: Set<String> = emptySet()) : State

data class AppState(  
  val counterState: CounterState = CounterState(),  
  val toDoState: ToDoState = ToDoState()  
) : State
```
## ViewComponent
This is a simple class, for extension, that handles binding state values to the UI in a `render(...)` function. Whenever a new state is computed and emitted, the render function will be invoked. In order to prevent excessive or unnecessary calls to the render function, subscriptions to state changes are **distinct** by default. This can be changed by overriding the `distinct(): Boolean` function in a given activity or fragment. This is also where you bind your UI to emit actions. A simple `ViewComponent` implementation for a Counter UI might look like this:
```kotlin
class CounterViewComponent(  
  container: ViewGroup? = null,  
  inflater: LayoutInflater,  
  dispatch: Dispatcher  
) : ViewComponent<CounterState>() {  
  
  override val binding: CounterLayoutBinding =  
    CounterLayoutBinding.inflate(inflater, container, false).apply {  
      incrementButton.setOnClickListener {  
        dispatch(Increment())
      }  
  
      decrementButton.setOnClickListener {  
        dispatch(Decrement())  
      }  
    }  
    
  override fun render(state: CounterState) {  
    binding.counterTextView.text = state.count.toString()  
  }  
}
```
## ReduxViewModel
A `ReduxViewModel` is for use with instances of `ReduxActivity` and `ReduxFragment`. It's responsible for managing subscriptions to the redux store across configuration changes. There will typically be a single instance per application.

## State Selector
When subscribing via `ReduxViewModel` or `ReduxStoreManager`, you're given the option to provide a function for mapping application state to sub-states. A simple selection method might look something like:
```kotlin
override fun onSelectState(state: AppState): CounterState = state.counterState
```
## Asynchronous Operations
Traditionally, Redux uses different varieties of Middleware (thunk, saga, promise, etc.) to handle async operations. While you could resort to using middleware for your async operations, the problem on Android, however, is that we generally need to scope our async operations to the lifecycle of a particular component, like a Fragment or Activity. This leads to hacky solutions for exporting `LifecycleOwner`s to middleware, or having singletons hold a long-lived reference to the current `LifecycleOwner`. If you've got many fragments, this gets complicated. These solutions are often times complex and prone to leaks. We also get some nice tools to handle operations inside these lifecycle-sensitive components, like Kotlin Coroutines and scopes like `lifecycleScope { ... }`, which encourage using Fragments & Activities as Controllers rather than Views. 

For those reasons, the default `Store` instance provided by this library is enhanced via Redux Store Enhancer to allow post-dispatch operations. You can subscribe to the current state + the action returned by the `dispatch()` function. `ReduxFragment` and `ReduxActivity` provide `performSideEffect(state, action)` for you to override. You're free to abstract away your async operations in whatever manner you please. Subscriptions to state & side effects are also lifecycle sensitive, and are auto-paused / resumed / canceled.

## Notes
- It's entirely possible and allowable to forego the use of `ReduxViewModel`, `ReduxActivity`, and `ReduxFragment` and simply use the enhanced store offered here.
- It's also entirely possible, though not encouraged, to write your own Async Middleware.
- If you find yourself applying the above two points, you may not need this framework :)

# Examples
An example application is provided [here](https://github.com/carterhudson/redux-kotlin-android/tree/master/example).

## License
```
   Copyright 2020 Carter Hudson

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
