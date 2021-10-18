Higher kinded types in Kotlin
=============================

Higher-kinded-types are a type of _generic programing_ feature that allows for generic parameters like `F` to stand for _not just_ types
 like `Int` or `Double` -- but _types_ which take _other types_ as arguments. The most common example often encounted is containers, like `F = List`, `F = Sequence`, or `F = Map`.
 
 Kotlin does not natively support such generic type parameters -- if you don't believe me, just try plugging just `List` (not `List<A>`, `List<Int>`, or `List<*>` -- just `List`) into something expecting a generic parameter.
 
 Iodine makes use of these higher-kinded-types by making use of a [lightweight encoding](https://www.cl.cam.ac.uk/~jdy22/papers/lightweight-higher-kinded-polymorphism.pdf) of this feature, as some features (such as making use of an interface `I` to define the type of interactions with a component) would be difficult to implement in a clean, declarative way without them. Additionally, Iodine makes use of some more [experimental/advanced](../concepts/comonadic_components.md) features by making use of this encoding.
 
 To allow for the highest level of interoperability with other frameworks using an encoding of Higher-kinded types in Kotlin/on the JVM, Iodine currently makes use of the [kindedJ](https://github.com/KindedJ/KindedJ) library in it's encoding.
 
 The core ideas of this approach are that:
 
   1. `Hk<Fw,A>` is a dummy "tagging" interface used to represent the higher-kinded application `F<A>`.
   2. The generic type `F<A>` should _extend_ the interface `Hk<Fw, A>`. Due to sub-typing, this means that
     we can use an `F<A>` wherever an `Hk<F.W, A>` is expected in an API.
   3. `Fw` is a "witness" for the higher-kinded type `F`. In other words, a dummy `object` used to represent `F` as an argument to `Hk` (remember -- we can't use `F` itself, which is the whole point of this encoding).
   4. There is a "witness function", `Hk<Fw,A>.fix(): F<A>` that lets us do the conversion the other way around. This requires type-casting, but
     should be safe as long as for each `Fw`, there is only one `F` that implements `Hk<Fw, A>`.
     
For example, to represent `MyType` as a higher-kind with the witness `MyType.W`:

```kotlin

data class MyType<A>(val someValue: A, val someOtherValue: A): Hk<MyType.W, A> {
    // Witness for MyType
    object W
}

@Supress("UncheckedCast")
fun Hk<MyType.W, A>.fix(): MyType<A> {
    return this as MyType<A>
}

```

