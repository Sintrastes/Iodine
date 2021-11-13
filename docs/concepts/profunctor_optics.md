
Profunctors
-----------

In addition to comonads, another concept from the pure functional programming community that Iodine makes use of is that of _profunctors_ and _profunctor optics_.

Profunctors are a generalization of functions. Essentially, they can be thought of as objects with an "input type", and an "output type" -- where these input and output types can be "adapted" by providing the appropriate type of function.

```kotlin
interface Profunctor<P> {
    fun Hk2<P,A,B>.lmap(f: (X) -> A): Hk2<P,X,B>
    fun Hk2<P,A,B>.rmap(f: (B) -> X): Hk2<P,A,X>
}
```

Forms
-----

For every `Component` interface in Iodine, such as `ComponentImpl`, `MooreComponent`, and so on -- there is a comporresponding interface for a `Form` that extends the given notion of a component with an _output type_. This interface is incredibly useful for components used for _data entry_ -- hence the name. Forms will have all the same generic parameters as Components, plus an additional one for it's output type. So, for instance, something like `Form<Ctx,I,E,A,B>`, rather than `Component<Ctx,I,E,A>`.

Defining forms this way allows us to view forms as an important example of a `Profunctor`, as witnessed by the `lmap` and `rmap` methods defined for such forms. 

Profunctor optics
-----------------

_Optics_ are usually concieved as a type of _modular data accessor_s for immutable data structures in functional languages. However, optics also have [another side](https://medium.com/@wigahluk/another-look-through-optics-ffd253336e9c) less seen -- and this can be seen through the lens (pun intended) of _profunctor optics_.

...

Applications of Profunctor Optics to Forms
------------------------------------------

...
