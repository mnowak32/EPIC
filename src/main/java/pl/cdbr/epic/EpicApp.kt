package pl.cdbr.epic

import javafx.application.Application
import pl.cdbr.epic.service.Database
import tornadofx.*

class EpicApp : App(MainWindow::class) {
}

fun main(args: Array<String>) {
    if (!Database.isInitialized()) {
        Database.initialize()
    }
    Database.load()
    Application.launch(EpicApp::class.java, *args)
}