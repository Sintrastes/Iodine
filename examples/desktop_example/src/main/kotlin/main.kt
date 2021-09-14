
import com.bedelln.iodine.components.*
import com.bedelln.iodine.desktop.*
import com.bedelln.iodine.*
import com.bedelln.iodine.interfaces.*
import com.bedelln.iodine.desktop.ctx.WindowCtx
import com.bedelln.iodine.tools.AlertDialog

fun main() = IodineWindow(
    title = "Iodine for Desktop Demo",
    contents = ActionButton<WindowCtx>(
        text = "Hello!",
        action = Tool.noop()
            // AlertDialog(
            //     title = "My alert",
            //     contents = TextEntry()
            // )
            // .lmap { it: Unit -> "test" }
            // .rmap { }
    )
        .ignoreEvents()
)