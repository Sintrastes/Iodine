
import com.bedelln.iodine.*
import com.bedelln.iodine.components.ActionButton
import com.bedelln.iodine.components.TextEntry
import com.bedelln.iodine.desktop.ComposeTkWindow
import com.bedelln.iodine.tools.AlertDialog

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