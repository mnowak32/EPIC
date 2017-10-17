package pl.cdbr.epic

import javafx.application.Application
import javafx.stage.Stage
import pl.cdbr.epic.service.Database
import pl.cdbr.epic.ui.MainWindow
import tornadofx.*

class EpicApp : App(MainWindow::class) {
    override fun start(stage: Stage) {
        if (!Database.isInitialized()) {
            Database.initialize()
        }
        Database.load()

        super.start(stage)
    }

    override fun stop() {
        super.stop()

        Database.save()
    }
}

fun main(args: Array<String>) {
    Application.launch(EpicApp::class.java, *args)
}