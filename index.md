
Introduction
============

<p align="center">
  <img src="https://raw.githubusercontent.com/Sintrastes/Iodine/gh-pages/iodine.svg">
</p>


Iodine is a functional UI toolkit for Kotlin built on top of [Flow](https://kotlinlang.org/docs/flow.html), [Jetpack Compose](https://developer.android.com/jetpack/compose) , and [Arrow](https://arrow-kt.io/). Where Arrow intends to be a "Functional companion to Kotlin's Standard Library", Iodine aims to be a functional companion to Jetpack Compose (including for [desktop](https://github.com/JetBrains/compose-jb) and multi-platform targets). 

To get started, check out [basic concepts](#basic-concepts) for a short introduction to some of core concepts and types used by Iodine -- or [examples](#examples) for some concrete examples of how to build up some basic applications with Iodine components.

Background
----------

Declarative functional UI programming has a long and varied history, going back to at least to the [Fudgets](https://en.wikipedia.org/wiki/Fudgets) library in the 1990s. More recently, in the 2010s, the [React](https://reactjs.org/) framework popularized functional declarative UI programming in the Javascript ecosystem. Since then, there have been a number of different approaches to this paradigm, from the popular to the [more](https://elm-lang.org/) [obscure](https://owickstrom.github.io/gi-gtk-declarative/) -- in many languages and platforms.

Amidst this great array of diversity and complexity, a common organizing principle is the idea of a _Component_. To quote the documentation from [Halogen](https://purescript-halogen.github.io/purescript-halogen/guide/02-Introducing-Components.html):

 > Halogen uses a component architecture. That means that Halogen uses components to let you split your UI into independent, reusable pieces and think about each piece in isolation. You can then combine components together to produce sophisticated applications.

<!-- TODO: Add a page for comparisons to the Halogen framework/types. -->

Iodine takes heavily from the ideas of the Halogen framework ([iodine](https://en.wikipedia.org/wiki/Iodine) is a type of [halogen](https://en.wikipedia.org/wiki/Halogen), after all) -- in particular, the idea of using generic parameters to expose the _public API_ of a UI component. From there, the approaches diverge somewhat. Whereas Halogen is designed to work well in a [purely functional language](https://www.purescript.org/), with support for some helpful but not very commonly implemented [type system features](https://github.com/purescript/documentation/blob/master/language/Types.md#row-polymorphism), Iodine is designed with similar goals in mind, but implemented in a way as to work more idiomatically with Kotlin's multi-paradigm philosophy -- and to take advantage of features unique to Kotlin, such as typesafe builder DSLs and functions with receivers.

Another strain of ideas that Iodine is inspired by the idea of using _comonads_ to model user interface (c.f. [Declarative UIs are the Future — And the Future is Comonadic!](https://functorial.com/the-future-is-comonadic/main.pdf), Phil Friedman, 2018). A similar approach is taken by the Swift framework [Bow Arch](https://arch.bow-swift.io/). Direct support for this style of programming is provided by the `iodine-core-comonadic-ui` package.

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

Comonadic Components
====================

Now that we've introduced the basic idea of what a component is in Iodine, let's take a look at how _comonads_ ome into the picture. For those not interested in the theory, continue on to the subsections of this section to see some examples of how this can be used.

...

Store Components
----------------

```kotlin
data class Store<S, A>(
    val state: S,
    val view: (S) -> A
)
```

Moore Components
----------------

```kotlin
data class Moore<E, A>(
    val value: A,
    val next: (E) -> Moore<E, A>
)
```

![HComponent](https://raw.githubusercontent.com/Sintrastes/Iodine/gh-pages/HComponent.png)

Cofree Components
----------------

```kotlin
data class Cofree<F, A>(
    val state: A,
    val next: Hk<F, Cofree<F,A>>
)
```

Profunctor optics
=================

In addition to comonads, another concept from the pure functional programming community that Iodine makes use of is that of _profunctors_ and _profunctor optics_.

Examples
========

Building a basic component
--------------------------


Input validation
----------------

One common problem often that has to be addressed when building graphical user interfaces is how to handle _form input validation_. Iodine provides a couple of utility classes for dealing with exactly this problem. For example, consider the following example for how to build a component with input validation for a textbox entry for an integer.

```kotlin
// Example Component
object: ValidatingStoreComponent<String, Int, InvalidInteger>(initialValue) {
    override fun view(input: String) =
        input.toIntOrNull()?.let { Either.Right(it) }
            ?: Either.Left(InvalidInteger)

     @Composable
     override fun contents(error: InvalidInteger?, contents: String) {
         Column {
             TextField(
                 value = contents,
                     onValueChange = { newValue ->
                         ctx.defaultScope.launch {
                             mutInput.emit(newValue)
                         }
                     },
                 label = {
                     Text("")
                 }
             )
         if (error == InvalidInteger) {
             Text(
                 text = "Not a valid integer",
                 color = Color.Red,
                 fontSize = 16.sp
             )
     }
}

Combining components
--------------------


Building a calculator app
-------------------------

```

