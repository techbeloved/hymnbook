import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.techbeloved.hymnbook.shared.MainView

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MainView()
    }
}