## Iodine

<p align="center">
  <img src="http://sintrastes.github.io/iodine/iodine.svg">
</p>

Introduction
============

Iodine is a functional UI toolkit for Kotlin built on top of [Flow](https://kotlinlang.org/docs/flow.html), [Jetpack Compose](https://developer.android.com/jetpack/compose) , and [Arrow](https://arrow-kt.io/). Where Arrow intends to be a "Functional companion to Kotlin's Standard Library", Iodine aims to be a functional companion to Jetpack Compose (including for [desktop](https://github.com/JetBrains/compose-jb) and multi-platform). 

![HComponent](http://sintrastes.github.io/iodine/HComponent.png)

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

