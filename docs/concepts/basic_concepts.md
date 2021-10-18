
Basic Concepts
==============

The two basic concepts that Iodine makes use of is that of a _Component_, and that of a _View Model_. Roughly speaking, a _Component_ is reusable bundle of behavior and a view (i.e. a `@Composable fun`) of that behavior's internal _state_. Oftentimes, it may be convinient to keep the "pure" UI behavior logic decoupled from the actual way in which such logic is rendered -- for this purpose, we have the concept of a `ViewModel`. A `ViewModel` is pretty much what it sounds like -- a model of a view, or in our case a `Component`.

View models and Components
--------------------------

To start, let's dig into how Iodine defines a `ViewModel`:

```kotlin
interface Settable<in A> {
    fun setValue(newValue: A) { }
}

interface ViewModel<out I, out E, S, in A>: Settable<A> {
    val impl: I
    val events: Flow<E>
    val state: StateFlow<S>
}
```

Theoretically, Iodine could have just gotten away with using `I` here -- as most _view models_ as used in UI design patterns such as _Model-View-ViewModel_ are just plain old interfaces. However, for convinience, we decided to break up the idea of a view model into three distinct components:

  1. `I`: The "core" interface of the ViewModel -- this should be an interface that describes methods that can be used to interact with the view model.
  2. `E`: The type of events that this view model can emit asynchronously at any time. Again, this could be construed as just another method of `I` -- but as this is such a common feature of view models, we reify the type `E` as a seperate part of the API.
  3. `S`: The type of internal state used by the view model. Usually, in a traditional view model, this would be a `private` part of the implementation of the API. However, for an Iodine `ViewModel`, we expose this inner state as a reactive `StateFlow<S>` so this view model can be bound to a rendering function to produce a full `Component`.

`Component`s are just like view models, but together with a function for rendering the state of the `ViewModel`. Once a `ViewModel` has been bound to a mechanism for rendering it's state, there is no longer a reason to care about the state type `S`. Thus, whereas a `ComponentImpl` is defined as follows:

```kotlin
interface ComponentImpl<out Ctx, out I, out E, S, in A>: ViewModel<I,E,S,A> {
     @Composable fun contents(state: S)
 }
```

most of the time we want to use a `Component`, we want to keep the internal state `S` _encapsulated_. This can be accomplished with a star projection, which is how Iodine defines an honest-to-goodness `Component`:

```kotlin
 typealias Component<Ctx,I,E,A> = ComponentImpl<Ctx,I,E,*,A>
```

