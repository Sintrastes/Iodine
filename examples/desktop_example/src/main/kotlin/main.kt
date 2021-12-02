
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bedelln.iodine.components.*
import com.bedelln.iodine.components.builders.Column
import com.bedelln.iodine.components.builders.Row
import com.bedelln.iodine.components.text.DoubleEntry
import com.bedelln.iodine.components.text.IntEntry
import com.bedelln.iodine.desktop.*
import com.bedelln.iodine.desktop.ctx.WindowCtx
import com.bedelln.iodine.interfaces.*
import com.bedelln.iodine.tools.AlertDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

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

