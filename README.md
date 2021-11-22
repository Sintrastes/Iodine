<h1 align="center">Iodine</h1>
<p align="center">
  <a href="https://sintrastes.github.io/iodine/">
    <img width=10% src="res/iodine.svg?token=AA7AHVO4G25UIIT3P6G6TIDAONGOE">
  </a>
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

[Iodine](https://sintrastes.github.io/iodine/) is a functional UI toolkit for Kotlin built on top of [Flow](https://kotlinlang.org/docs/flow.html), [Jetpack Compose](https://developer.android.com/jetpack/compose) , and [Arrow](https://arrow-kt.io/). Where Arrow intends to be a "Functional companion to Kotlin's Standard Library", Iodine aims to be a functional companion to Jetpack Compose (including for [desktop](https://github.com/JetBrains/compose-jb) and multi-platform). 

⚠️ WARNING: This project is alpha quality software built on-top-of beta quality software (compose multi-platform). Things may break. 
 Interfaces are likely to change without notice or deprecation cycles between versions. Documentation may not be entirely accurate or complete. While Iodine could very well be used today to build applications, at this time we do not reccomend it for production use.

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

💡 Related Projects
-------------------

  * [Bow Arch](https://github.com/bow-swift/bow-arch): UI-framework for Swift with a similar approach to Iodine.
  * [Halogen](https://github.com/purescript-halogen/purescript-halogen): Inspiration for Iodine's approach of using strongly typed UI composable components.
  * [Yesod](https://www.yesodweb.com/): Inspiration for Iodine's Applicative form interface.
