
Comparison with Halogen
=======================

If you're coming from [Halogen](https://purescript-halogen.github.io/purescript-halogen/), you will find Iodine very similar. Iodine takes a lot of cues from Halogen, but there are a few important differences to note.

Interfaces as `query`
---------------------

Veterans of Halogen are sure to notice the lack of the `query` parameter of `H.Component` in Iodine's components. That is because it has been replaced with
 an _interaction interface_ I. Now, I know what some of you might be thinking:
 
 > *gasp*! 
 \**clutches functional pearls\** -- How could one possibly replace the higher-kinded `query : * -> * -> *` with a simple interface?

To which I respond: Don't worry -- this actually has a bit of precedent, and [interesting theory](https://r6.ca/blog/20140210T181244Z.html) behind it. So if you need to legitimize your functional street cred, just be sure to mention something about the "van Laarhoven Free Monad". Also, to be fair, we still do need higher kinds when _defining_ the component -- let me explain.

It turns out that the Kotlin type `I.() -> A`, where `I` is an interface, is _almost like_ a free monad. This is not quite as powerful as the van Laarhoven encoding mentioned above, but close enough for our use-case of `I` as a parameter for describing the API for interacting with a typed component (hence, _interaction interface_).

Even better -- it turns out that `I` is pretty close to what we need in order to _interpret_ our "free-ish" monad `I.() -> A` -- that is, a [cofree comonad](https://dlaing.org/cofun/posts/free_and_cofree.html). However, this is where higher-kinds are necessary for our use-case. In Iodine, we make use of the following interface

```kotlin
interface IndexedAPI<I,Ix> {
    fun I.asIx(): Hk<Ix, Unit>
    fun Hk<Ix, Unit>.asInterface(): I
}
```

to witness the relationship between an _interface_ `I`, and it's "indexed" (or functorial) version `Ix`. Note that here, [`Hk<F,A>`](higher_kinds_in_kotlin.md) can be read as `F<A>`. For example:

```kotlin

interface Calculator {
    fun enterNumber(x: Int)
    fun enterSymbol(symbol: Symbol)
    fun viewScreen(): String
    
    companion object {
        fun indexed(): IndexedAPI<Calculator, IxCalculator.W> {
            ...
        }
    }
}

interface IxCalculator<K>: Hk<IxCalculator.W, K> {
    object W
    
    fun enterNumber(x: Int): K
    fun enterSymbol(symbol: Symbol): K
    fun viewScreen(): Pair<String, K>
}

```

Composing Components
--------------------

Although, after parsing out the differences between Halogen's `query` parameter and Iodine's `I`, Iodine provides a very similar external API for components, a major different arises in how these APIs are used. Whereas Halogen enforces a _tree-like_ hiearchy of components, with "parent" and "child" components, Iodine is not as strict -- and allows anyone with a reference to a `Component<Ctx,I,E,A>` to `interact` with it.

Monadic effects
---------------

Another difference between Iodine and Halogen is that whereas Halogen makes use of monads to model side-effecting computations, Iodine does not do so -- or rather, when it _does_, it does so in a slightly different way.

Monads can be encoded in Kotlin using the same mechanism used above to encode higher-kinded types -- so why not make use of them in Iodine? Well, for one, the encoding of higher-kinded-types in Kotlin is currently rather tedious -- requiring manual wrapping and unwrapping to get the types to line up properly. Thus, we want to avoid using that encoding _whenever possible_.

Additionally, Kotlin, unlike Purescript, is not a _pure_ functional language. There is not a notion of a _pure function_ that can be enforced by the compiler, and functions can produce arbitrary side-effects at any time. A function `(A) -> B` in Kotlin is like `a -> Eff b` in Purescript. 

However, that being said, even if one can already preform arbitrary side-effects in an Iodine comonent -- that does not mean that monads in general can not be of use. Iodine components still can have specified monadic effects -- we just don't call them that, and they look a little bit differently.

So how does this work? Essentially, we use a similar idea as was used with `I`. We can encode additional effects (technically [coeffects](http://tomasp.net/coeffects/)) using an additional context parameter `Ctx`.

For instance, if you need access to some service in your Iodine component, just extend `IodineCtx` with the appropriate interface, and use that as your `Ctx` parameter for your 
