import androidx.compose.desktop.Window
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.bedelln.composetk.components.ActionButton
import com.bedelln.composetk.desktop.ComposeTk
import com.bedelln.composetk.tools.AlertDialog

fun main() = Window {
    val dialogState = remember { mutableStateOf(false) }

    Button(onClick = { dialogState.value = true }) {
        Text(text = "Open dialog")
    }

    if (dialogState.value) {
        Dialog(
            onDismissRequest = { dialogState.value = false }
        ) {
            // Dialog's content
        }
    }
}

    /*ComposeTk(
    title = "Compose Tk Demo",
    contents = ActionButton(
        text = "Hello!",
        action = AlertDialog("My alert") {
            Button(
                content = {
                    Text("Hello from my alert!")
                },
                onClick = { }
            )
        }
    )
) */

    /* Window {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}*/