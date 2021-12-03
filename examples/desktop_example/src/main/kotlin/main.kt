
import com.bedelln.iodine.desktop.aurora.auroraIodineApplication
import com.bedelln.iodine.desktop.aurora.components.Button

fun main() = auroraIodineApplication {
    Button("Hello, aurora!")
}

/*
@OptIn(ExperimentalMaterialApi::class)
fun main() = iodineDesktopApplication(
    content = {
        IodineWindow(
            title = "Iodine for Desktop Demo",
            contents = Column(
                modifier = Modifier.padding(5.0.dp)
            ) {

                - ActionButton(
                    text = "Hello!",
                    action = AlertDialog(
                        title = "My alert",
                        contents = TextEntry()
                    )
                        .lmap { it: Unit -> "Test" }
                )
                - ActionButton(
                    text = "Do a notification",
                    action = Notification(
                        title = "Iodine notification",
                        message = "Notified!"
                    )
                )
                val resetButton = - Button("Reset")

                - Row<WindowCtx>(
                    horizontalAlignment = Alignment.CenterVertically
                ) {
                    - Text().initialValue("Set a value: ")
                    slider = - Slider(Modifier.width(30.dp))
                        .initialValue(0.5f)
                    Unit
                }

                sliderText = - Text().initialValue("")

                - Row<WindowCtx>(
                    horizontalAlignment = Alignment.CenterVertically
                ) {
                    - Text().initialValue("Set the thing: ")
                    - Switch().initialValue(true)
                }
                - IntEntry().initialValue("42")
                - DoubleEntry().initialValue("42")

                val closeButton = - Button(
                    text = "Close application",
                    modifier = Modifier
                )

                /************* Interactions ****************/

                // On click of the notification button, reset the slider.
                resetButton.on(ctx, Button.Event.OnClick::class) {
                    slider.impl
                        .setPosition(0.5f)
                }

                // On clicking the close button, close the application
                closeButton.on(ctx, Button.Event.OnClick::class) {
                    ctx.windowScope.cancel()
                }

                // Set the slider text to the value of the slider.
                slider.onValueChange(ctx) {
                    sliderText.setValue(it.toString())
                }
            }
        )
    }
)

lateinit var slider: Form<Slider.Action, Void, Unit, Float>
lateinit var sliderText: Component<Unit, Void, Unit>

 */
