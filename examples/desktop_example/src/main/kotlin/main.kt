
import androidx.compose.foundation.layout.Arrangement
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

@OptIn(ExperimentalMaterialApi::class)
fun main() = iodineDesktopApplication(
    content = {
        IodineWindow(
            title = "Iodine for Desktop Demo",
            contents = Column(
                modifier = Modifier.padding(5.0.dp)
            ) {
                lateinit var slider: Form<Slider.Action, Void, Unit, Float>

                - ActionButton(
                    text = "Hello!",
                    action = AlertDialog(
                        title = "My alert",
                        contents = TextEntry()
                    )
                        .lmap { "Test" }
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
                    - Text("Set a value: ")
                    slider = remember { - (Slider(Modifier.width(30.dp)).imap { _: Unit -> 0.5f }) }
                    - Text(slider.result().toString())
                    Unit
                }

                - Row<WindowCtx>(
                    horizontalAlignment = Alignment.CenterVertically
                ) {
                    - Text("Set the thing: ")
                    - (Switch(

                    ).imap { _: Unit -> true })
                }
                - ( IntEntry().imap { _: Unit -> "42" } )
                - ( DoubleEntry().imap { _: Unit -> "42" } )

                val closeButton = - Button(
                    text = "Close application",
                    modifier = Modifier
                )

                /************* Interactions ****************/

                // On click of the notification button, reset the slider.
                ctx.defaultScope.launch {
                    resetButton.events.collect { event ->
                        when (event) {
                            is Button.Event.OnClick -> {
                                slider.impl
                                    .setPosition(0.5f)
                            }
                        }
                    }
                }

                // On clicking the close button, close the application
                ctx.defaultScope.launch {
                    closeButton.events.collect { event ->
                        if (event is Button.Event.OnClick)
                            ctx.windowScope.cancel()
                    }
                }
            }
        )
    }
)

