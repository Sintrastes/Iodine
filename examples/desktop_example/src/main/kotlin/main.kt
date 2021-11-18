
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import com.bedelln.iodine.components.*
import com.bedelln.iodine.desktop.*
import com.bedelln.iodine.interfaces.*
import com.bedelln.iodine.tools.AlertDialog

@OptIn(ExperimentalMaterialApi::class)
fun main() = iodineApplication<Any,Any,Any>(
    initialValue = Unit,
    content = {
        IodineWindow(
            title = "Iodine for Desktop Demo",
            contents = ActionButton(
                text = "Hello!",
                action = AlertDialog(
                    title = "My alert",
                    contents = TextEntry()
                )
                    .lmap { it: Unit -> "Test" }
                    .rmap { }
            )
        )
    }
)

