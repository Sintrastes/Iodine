import androidx.compose.desktop.Window
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key.Companion.Window
import androidx.compose.ui.window.Dialog
import com.bedelln.composetk.*
import com.bedelln.composetk.components.ActionButton
import com.bedelln.composetk.components.TextEntry
import com.bedelln.composetk.desktop.ComposeTkWindow
import com.bedelln.composetk.desktop.Notification
import com.bedelln.composetk.tools.AlertDialog

fun main() = ComposeTkWindow(
    title = "Compose Tk Demo",
    contents = ActionButton(
        text = "Hello!",
        action = AlertDialog(
            title = "My alert",
            contents = TextEntry
        )
            .lmap { it: Unit -> "test" }
            .rmap { }
    )
)