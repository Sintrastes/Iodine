
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

It turns out the answer to this is (at least for a particular case of free monads): Yes. 

Consider that for "traditional" uses of free monads, the free monad is built from functors that look like the following:

```kotlin
sealed class CalculatorF<A> {
    data class EnterDigit<A>(digit: Int, rest: A): CalculatorF<A>
    data class EnterSymbol<A>(symbol: Symbol, rest: A): CalculatorF<A>
}
```

In order words, they are a "sum" of all of the different functions that can be injected into the free monad. However, consider that instead, we look at a free monad built from the dual product functor:

```kotlin
interface CoCalculatorF<A> {
    fun onEnterDigit(digit: Int): A
    fun onEnterSymbol(symbol: Symbol): A
}
```

In this case, we can view `Free<CoCalculatorF, A>` as a kind of "deffered result" of type `A`, where the result depends on the selected "onX" branch we take on each iteration of the product functor. Let's look at an example (TODO: Convert this to Kotlin):

```haskell
calculatorTool :: Calculator Int
calculatorTool = go (CalculatorState [])
  where go st = Free $ CoCalculatorF {
    onEnterDigit = \d -> trace "Entered digit" $
        go (CalculatorState $ (symbols st) ++ [Left d]),
    onEnterSymbol = \s -> trace "Entered symbol" $
        case s of
          Enter -> case extractResult (calculate st) of
              Just res -> Pure $ res
              Nothing  -> trace "Could not extract result" $ go (calculate st)
          Clear -> go $ CalculatorState []
          otherwise -> go
              (CalculatorState $
                   (symbols st) ++ [Right s])
  }
```

In other words, this can be viewed as a kind of abstract description of the state transitions of a `Tool`, as much as the free comonad over a functor like `CoCalculatorF` can be viewed as an abstract description of the state transitions of a `Component`.

Similarly then, with `CoFree<CalculatorF, Unit>` we can `explore` this description to update it's state.

```kotlin
fun Free<CoCalculatorF, A>.explore(actions: CoFree<CalculatorF, Unit>): Free<CoCalculatorF, A> {
    ...
}
```

Now, hopefully you can appreciate why this is a "dual way" of looking at a duality -- this flips the view of "comonads as spaces" and "monads as events to manipulate that state space" entirely on it's head! Now our monad is a space (albeit, one where the state we are about is at the _leaves_ of the state space, not at every node), and our comonad is in fact isomorphic to an infinite stream of actions used to explore the "space" of the monad.

In this "dual view", there are a few subtleties that need to be addressed when seeking to apply this result to practical situations, however. For example, if we were going to test a `Tool` defined via an abstract state description like this, we may want to look at _finite_ sequences of actions at a time, before checking a result. To do so, we have to add an additional case to our `CalculatorF` to provide a base case for the recursion in `Cofree` -- thus breaking the duality between our two functors:

```kotlin
sealed class CalculatorF<A> {
    data class EnterDigit<A>(digit: Int, rest: A): CalculatorF<A>
    data class EnterSymbol<A>(symbol: Symbol, rest: A): CalculatorF<A>
    class Done<A>(): CalculatorF<A>
}
```

However, the upside to `CoFree<CalculatorF, Unit>` being an infinite sequence of actions it that it actually lets define the perhaps helpful function:

```kotlin
fun Free<CoCalculatorF, A>.execute(actions: CoFree<CalculatorF, Unit>): A {
    ...
}
```

This function will execute the sequence of actions provided until our Monad is fully evaluated. Note however, that this is a partial function. There is noting to gaurntee that the sequence of actions provided will ever lead to the tool being executed to completion.

