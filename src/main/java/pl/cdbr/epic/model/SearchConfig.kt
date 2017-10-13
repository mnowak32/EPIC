package pl.cdbr.epic.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.CheckMenuItem
import tornadofx.*

class SearchConfig() {
    val nameProperty = SimpleBooleanProperty()
    var name by nameProperty

    val descProperty = SimpleBooleanProperty()
    var desc by descProperty

    val valueProperty = SimpleBooleanProperty()
    var value by valueProperty

    val typeSubtypeProperty = SimpleBooleanProperty()
    var typeSubtype by typeSubtypeProperty

    val supplierProperty = SimpleBooleanProperty()
    var supplier by supplierProperty

    val packProperty = SimpleBooleanProperty()
    var pack by packProperty
}