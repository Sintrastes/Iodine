<h1 align="center">Iodine</h1>
<p align="center">

  <img width=10% src="res/iodine.svg?token=AA7AHVO4G25UIIT3P6G6TIDAONGOE">
<p align="center">Typed components for <a href="https://developer.android.com/jetpack/compose">Jetpack Compose</a>, inspired by <a href="https://github.com/purescript-halogen/purescript-halogen">Halogen</a>.  </p> 
<p align="center">
  <a href="https://kotlinlang.org/">
    <img src="https://img.shields.io/badge/Language-Kotlin-blue">
  </a>
  <a href="https://github.com/Sintrastes/iodine/actions/workflows/ci.yml">
    <img src="https://github.com/sintrastes/iodine/workflows/CI/badge.svg">
  </a>
  <a href="https://sintrastes.github.io/iodine/docs/">
    <img src="https://github.com/sintrastes/iodine/workflows/DOCS/badge.svg">
  </a>
  <a href="https://github.com/Sintrastes/iodine/blob/main/LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-blue">
  </a>
</p>


Introduction
============

Iodine is a functional UI toolkit for Kotlin built on top of [Flow](https://kotlinlang.org/docs/flow.html), [Jetpack Compose](https://developer.android.com/jetpack/compose) , and [Arrow](https://arrow-kt.io/). Where Arrow intends to be a "Functional companion to Kotlin's Standard Library", Iodine aims to be a functional companion to Jetpack Compose (including for [desktop](https://github.com/JetBrains/compose-jb) and multi-platform). 

⚠️ WARNING: This project is of experimental/pre-release quality. Things are likely to break. 
 Interfaces are likely to change. Documentation may not be entirely accurate or complete. 
 The implementation may not be the most responsive. 

✏️Getting Started
--------------------

To get started with Iodine, clone this project and run `gradlew :core:jar :desktop:jar` 
 to build the `iodine-core` and `iodine-desktop` `jar`s needed for a Compose for Desktop 
 project, then, place these in the `libs` folder of your project, and add the following 
 to your `build.gradle.kts`:

```kotlin
dependencies {
    ...
    implementation(files("$projectDir/libs/iodine-core.jar"))
    implementation(files("$projectDir/libs/iodine-desktop.jar"))
    ...
}
```

An example of this can be found in the [compose for desktop project example](examples/desktop_example).

Similarly, to get started with an Android project, you can run `gradlew :core:jar :android:build`,
 place the built jar and aar in the `libs` folder of your project, and add the following
to your `build.gradle.kts`:

```kotlin
dependencies {
    ...
    implementation(files("$projectDir/libs/iodine-core.jar"))
    implementation(files("$projectDir/libs/iodine-android.aar"))
    ...
}
```

An example of this can be found in the [android project example](examples/IodineAndroidApp).

Like this project?
------------------

If you use Iodine, or find it useful, consider supporting it's development efforts by buying me a coffee ☕, or a beer 🍺!

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/donate?business=45F7QR92B4XUY&no_recurring=0&currency_code=USD)
[![Donate with Ethereum](https://en.cryptobadges.io/badge/micro/0x61531fCA114507138ebefc74Db5C152845b77Cad)](https://en.cryptobadges.io/donate/0x61531fCA114507138ebefc74Db5C152845b77Cad)

📚 Learn More
-------------

For a more comprehensive tutorial, visit the [project page](https://sintrastes.github.io/iodine/) for Iodine,
 or, if you would prefer to dig straight into the KDocs, you can find those [here](https://sintrastes.github.io/iodine/docs/). For a quick introduction, continue reading!

Concepts
=========

Iodine enchances the concept of "composable functions" from Jetpack Compose by introducing two new concepts: `Tool`s, and `Components`.

Components
----------

`Component`s can be thought of as re-useabe UI components with a typed interface specified through the use of generic parameters. Most user-facing code will not interace with `Component`s, directly, but rather with `ComponentDescription`s -- which can be thought of "recipies for building a component".

`ComponentDescription`s have four generic parameters describing how they can interoperate with other components. In order, they are: 

  1. `C`: The "Context" in which the component is defined. This is largely platform-specific (for instance, on Compose for Desktop, this will usually be either `WindowCtx` or `SystemCtx`, but in some cases for custom application-specific components, this could be something more sepcific, facilitating [functional depedency injection](https://arrow-kt.io/docs/0.10/patterns/dependency_injection/).
  2. `E`: The type of events that can be emited from the component (via a `Flow<E>`). Can be `Void` if no events are ever emited.
  3. `A`: The type used to initalize the state of the component. Can be `Unit` if there are no meaningful inputs that are needed when initalizing the state.
  4. `B`: A type representing the state that can be polled of this component (via a `StateFlow<B>`). Can be `Unit` if the component exposes no state.

For example, one of Iodine's core components `TextEntry` has type `ComponentDescription<WindowCtx, Void, String, String>` -- as it must be initialized with an initial `String` contents, and at any time yields a value of type `String`. It does not expose events beyond it's exposed `String` output value, so it's type of events is `Void`, and a `TextEntry` needs to be associated with a particular window, so it's context is `WindowCtx`.

Tools
-----

A `Tool` . Much like for components, most of the time we will actually work with `ToolDescription`s, which follows the same `C,E,A,B` parameter naming scheme as for components, with slightly different semantics:

  1. `C`: The context in which the tool is defined. 
  2. `E`: The type of events that can be broadcast from the tool during it's execution (via a `Flow<E>`). (Again, can be `Void` if no events are emited)
  3. `A`: The type used to initalize the state of the tool (alternatively -- could be interpreted as the *input type* of the tool). Can be `Unit` if the tool does not require any initialization arguments.
  4. `B`: A type representing the eventual return value of the tool. Can be `Unit` if there is no meaningful return value.

Whereas a `ComponentDescription` represents a specific part of the screen, such as a button, a text box, or an input form, a `ToolDescription` represents an action that can be preformed, possibly chaning the state of the enclosing UI, or opening up a different UI (such as a window or an activity), and finally returning with a value. Since these actions usually depend on user input (e.x. wait for the user to enter in some data and press OK on a dialog box), or external I/O (e.x. poll some data from a database, and display a loading icon until this finishes), the `Tool` interface is defined using a `suspend` fun:

```kotlin
interface Tool<in C,out A> {
    suspend fun runTool(ctx: C): A
}
```

Composition
-----------

Tools and components can be combined into larger and more complex components/tools by making use of the many extension functions that `Iodine` provides for these interfaces. Many of these extension functions fall under patterns that will be familiar to functional programmers, for example:

  1. `lmap`, `rmap`, and `dimap`: Profunctor operations.
  2. `map`, `mapEvents`: Functor operations.
  3. `identity`, `compose`: Category operations. 
  4. `pure`, `bind`: Monad operations.
  5. `mapCtx`: Contravariant functor operations

💡 Related Projects
-------------------

  * [Bow Arch](https://github.com/bow-swift/bow-arch): UI-framework for Swift with a similar approach to Iodine.
  * [Halogen](https://github.com/purescript-halogen/purescript-halogen): Inspiration for Iodine's approach of using strongly typed UI composable components.
  * [Yesod](https://www.yesodweb.com/): Inspiration for Iodine's Applicative form interface.
