
Introduction
============

<p align="center">
  <img src="https://raw.githubusercontent.com/Sintrastes/Iodine/gh-pages/iodine.svg">
</p>


Iodine is a functional UI toolkit for Kotlin built on top of [Flow](https://kotlinlang.org/docs/flow.html), [Jetpack Compose](https://developer.android.com/jetpack/compose) , and [Arrow](https://arrow-kt.io/). Where Arrow intends to be a "Functional companion to Kotlin's Standard Library", Iodine aims to be a functional companion to Jetpack Compose (including for [desktop](https://github.com/JetBrains/compose-jb) and multi-platform targets). 

Iodine makes use of a [component-based architecture](#background), while also taking some inspirations from traditionally object-oriented approaches such as [Model-View-ViewModel](https://docs.microsoft.com/en-us/xamarin/xamarin-forms/enterprise-application-patterns/mvvm). 

To get started, check out [basic concepts](concepts/basic_concepts.md) for a short introduction to some of core concepts and types used by Iodine -- or [examples](examples/basic_examples.md) for some concrete examples of how to build up some basic applications with Iodine components.

Background
----------

Declarative functional UI programming has a long and varied history, going back to at least to the [Fudgets](https://en.wikipedia.org/wiki/Fudgets) library in the 1990s. More recently, in the 2010s, the [React](https://reactjs.org/) framework popularized functional declarative UI programming in the Javascript ecosystem. Since then, there have been a number of different approaches to this paradigm, from the [popular](https://developer.apple.com/xcode/swiftui/) to the [more](https://elm-lang.org/) [obscure](https://owickstrom.github.io/gi-gtk-declarative/) -- in many [languages](https://yew.rs/) and platforms.

Amidst this great array of diversity and complexity, a common organizing principle is the idea of a _Component_. To quote the documentation from [Halogen](https://purescript-halogen.github.io/purescript-halogen/guide/02-Introducing-Components.html):

 > Halogen uses a component architecture. That means that Halogen uses components to let you split your UI into independent, reusable pieces and think about each piece in isolation. You can then combine components together to produce sophisticated applications.

Iodine takes heavily from the ideas of the Halogen framework ([iodine](https://en.wikipedia.org/wiki/Iodine) is a type of [halogen](https://en.wikipedia.org/wiki/Halogen), after all) -- in particular, the idea of using generic parameters to expose the _public API_ of a UI component. From there, the approaches diverge somewhat. Whereas Halogen is designed to work well in a [purely functional language](https://www.purescript.org/), with support for some helpful but not very commonly implemented [type system features](https://github.com/purescript/documentation/blob/master/language/Types.md#row-polymorphism), Iodine is designed with similar goals in mind, but implemented in a way as to work more idiomatically with Kotlin's multi-paradigm philosophy -- and to take advantage of features unique to Kotlin, such as typesafe builder DSLs and functions with receivers.

Users coming from the Halogen library might want to take a look at a more [in-depth comparison](additional/halogen_comparison.md).

Another strain of ideas that Iodine is inspired by the idea of using _comonads_ to model user interface (c.f. [Declarative UIs are the Future — And the Future is Comonadic!](https://functorial.com/the-future-is-comonadic/main.pdf), Phil Friedman, 2018). A similar approach is taken by the Swift framework [Bow Arch](https://arch.bow-swift.io/). Direct support for this style of programming is provided by the `iodine-core-comonadic-ui` package.



