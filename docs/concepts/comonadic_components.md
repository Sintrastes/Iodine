

Comonadic Components
--------------------

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
