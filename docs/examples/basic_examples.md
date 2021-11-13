
Examples
========

Building a basic component
--------------------------


Input validation
----------------

One common problem often that has to be addressed when building graphical user interfaces is how to handle _form input validation_. Iodine provides a couple of utility classes for dealing with exactly this problem. For example, consider the following example for how to build a component with input validation for a textbox entry for an integer.

```kotlin
// Example Component
object: ValidatingStoreComponent<String, Int, InvalidInteger>(initialValue) {
    override fun view(input: String) =
        input.toIntOrNull()?.let { Either.Right(it) }
            ?: Either.Left(InvalidInteger)

     @Composable
     override fun contents(error: InvalidInteger?, contents: String) {
         Column {
             TextField(
                 value = contents,
                     onValueChange = { newValue ->
                         ctx.defaultScope.launch {
                             mutInput.emit(newValue)
                         }
                     },
                 label = {
                     Text("")
                 }
             )
         if (error == InvalidInteger) {
             Text(
                 text = "Not a valid integer",
                 color = Color.Red,
                 fontSize = 16.sp
             )
     }
}
```

Combining components
--------------------

The easiest way to combine smaller components into larger components in Iodine is to make use of one of the provided _builders_. For components which are not `Form`s -- you'll generally want to make use of `ComponentBuilder`.

```kotlin

```

For combining `Form`s, a little bit more care is needed. Currently Iodine only directly supports building up forms whose input and output types are immutable data classes. For such cases, we can make use of the [buildable-kt](https://github.com/Sintrastes/buildable-kt) library. 

`buildable-kt` comes with an annotation `@GenBuildable`, which can be applied to a data class in order to automatically generate an implementation of the `Buildable` interface that Iodine needs in order to be able to construct a form for a complete type from smaller forms that are used for inputting it's constituent peices. 

To illustrate this, let's look at an example of how `buildable-kt` can be used alongside Iodine's `FormBuilder` to build up a form:

```kotlin
@GenBuildable
data class Person(
    val name: String,
    val age: Int
) {
    companion object { }
}

object PersonForm: SFormDescription<IodineCtx, Person> by (
    FormBuilder {
        
    }
)
``` 




