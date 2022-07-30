
Pre-v1 Comparison
=================

Prior to the official v1 release of Iodine (and specifically, going back from the 1a1333e39dafeebfe429c54b8471346ffcaf2bbc commit and earlier in the codebase), Iodine had a lot of differences with the current design. So much so, that it might be fair to consider it as a completely different library.

For historical reasons, and in case you are one of the few people who read some of the pre-v1 version of Iodine's documentation or code-base, this section of the documentation exists to motivate some of the breaking API changes that led to where Iodine is today.

Original Implementation
-----------------------

Originally, a lot of the design of Iodine was influenced by some ideas I had of how to implement a declarative UI framework on top of vanilla Android (i.e. `View`) based UIs. The basic idea was to define an interface for "typed components" (somewhat inspired by the Halogen library), together with various utilities for combining and composing said components into more complex UIs. This looked something like:

```kotlin
interface Component<E, A, B> {
    val view: View
    val events: Flow<E>
    fun onSetValue(newValue: A)
    fun getValue(): B
}
```

This is great if raw `View`-based UIs are all you have, and allows you to do a lot of cool things very succinctly, especially when combined with other parts of the library like tools (asynchronous UI-modifying workflows that let you easily compose things like sequences of alert dialogs), or with things like arrow-optics, which then lets you build up components with incredibly flexible data binding operations, and is incredibly useful for removing a lot of the boilerplate around building up complex data entry forms.

Some of these advantages (such as both the ease of building up complex data entry forms, as well as composing asynchronous UI-modifying workflows) went beyond raw compose, so I decided to try to implement this idea on top of Jetpack Compose instead of Android `View`s -- this was the initial idea behind the initial implementation of `Iodine`, /Users/nathan/Code/Iodine-gh-pages/docs/additional/pre-v1_comparison.mdand the `Component`s there were essentially the same as the Android `View`-based version, but with the `View` swapped out for a composable function:

```kotlin
interface Component<E, A, B> {
    @Composable
    fun contents()

    val events: Flow<E>
    fun onSetValue(newValue: A)
    fun getValue(): StateFlow<B>
}
```

Eventually I realized that this design didn't really take full advantage of everything compose had to offer -- and didn't really seem to integrate well into the existing compose ecosystem. Wrappers like `iodineApplication` had to be used, Iodine components and other composable functions couldn't be easily mixed. So despite the aforementioned advantages, it wasn't very great.

The New Approach
----------------

It turns out, making use of all that compose has to offer rather than just using an `@Composable` function as a drop-in replacement for `View`, this interface can be simplified considerably. For instance, `onSetValue` can be replaced by adding an `A` parameter to the composable, and `getValue` is unnecessary, because we can just return a value of type `B` from the composable.

This also has the advantage (much like Bow Arch, which Iodine also takes inspiration from) of allowing us to call Iodine components directly from regular composable functions (via the `contents` composable), thus making Iodine much less "opinionated", and letting us eschew the need for "special" functions like `iodineApplication`.

