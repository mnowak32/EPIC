package pl.cdbr.epic

import javafx.application.Application
import tornadofx.*

class EpicApp : App(MainWindow::class) {
}

fun main(args: Array<String>) {
    Application.launch(EpicApp::class.java, *args)
}