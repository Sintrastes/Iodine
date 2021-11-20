
Tools
-----

Beyond components and forms, the next important concept in Iodine is that of a `Tool`.

Whereas `Component`s can be thought of as an enhanced `@Composable` function -- `Tool`s can be thought of as enhanced `suspend` functions. 

```kotlin
interface Tool<in C: IodineContext, out E, out A> {
    val events: Flow<E>

    suspend fun runTool(ctx: C): A
}
```

Similarly to `Component`s, `Tool`s make use of a context parameter extending `IodineContext` that can be used to provide basic dependencies needed for UI manipulation, as well as potentially other relevant data via functional dependency injection (although, once Kotlin gets context receivers, that will become less important). Additionally, `Tool`s come equipped with an event parameter `E` for events that may be asynchrounously emitted while the tool is running.

`Tool`s are generally meant to be used for actions that manipulate the surrounding user interface, prompt the user to preform some set of actions, and then eventually to return a result (the key word _eventually_ being why we make `runTool` a `suspend fun`. For example, a modal dialog adds a dialog to the current window (or screen, or page -- depending on platform), potentially prompts the user to enter in some data, or to manipulate some widgets in the dialog, and then eventually to select one of the buttons of the dialog to close it out (for instance, "Ok" or "Cancel").

Again, much like components, `Tool`s also come in a `ToolDescription` flavor. `ToolDescription`s add an input parameter and so are defined as follows:

```kotlin
typealias ToolDescription<C,E,A,B>
    = Description<C, A, Tool<C, E, B>>
```

Composing tools
---------------

By invoking the companion object of `Tool`, we can combine mutliple `ToolDescription`s in sequence [monadically](https://arrow-kt.io/docs/patterns/monad_comprehensions/) by using `.bind()`.

```kotlin
Tool {
    val res = ModalPrompt("Click OK to get a toast!").bind()
    if (res != null) {
        Toast("Here's your toast.").bind()
    }
}
```

Iodine also provides a set of additional monadic comprehensions for tools returning nullable and `Either` results.

Finally, Iodine also provides a set of primitive functions for combining tools, such as `compose` and `thenTool`. However, we supsect that in most cases, the monad comprehension syntax will be the most convinient and readable. 

The duality of tools and components
-----------------------------------

In the section on comonadic components, we saw that each comonad `W` has an associated "dual" monad `M` which allows us to define an operation `fun <A> W<A>.explore(action: M<Unit>): W<A>` which lets us preform a set of actions that manipulates the state of the comonad `W<A>` to return a new comonad representing the new state of the comonad after preforming the given actions.

As it turns out, there is a dual way of viewing this duality between Monads and Comonads. However, this is (as far as the author knows at the time of writing) not as well studied, so for the time being, we will simply look at an example of this phenomenon.

Recall that with components, we have an "interaction interface" `I` to fill in for the action monad `M` which acts as a kind of "controller" or "view model" for the component that manipulates it's state. Can something similar be done for tools?

The answer is: Yes. ...

