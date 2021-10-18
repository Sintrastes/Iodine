
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






