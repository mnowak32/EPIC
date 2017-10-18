package pl.cdbr.epic.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.CheckMenuItem
import tornadofx.*

class AppConfig {
    @JsonIgnore
    val searchConfigProperty = SimpleObjectProperty<SearchConfig>()
    var searchConfig by searchConfigProperty

    companion object {
        fun default() = AppConfig().apply {
            searchConfig = SearchConfig.default()
        }
    }
}

class SearchConfig() {
    @JsonIgnore
    val nameProperty = SimpleBooleanProperty()
    var name by nameProperty

    @JsonIgnore
    val descProperty = SimpleBooleanProperty()
    var desc by descProperty

    @JsonIgnore
    val valueProperty = SimpleBooleanProperty()
    var value by valueProperty

    @JsonIgnore
    val typeSubtypeProperty = SimpleBooleanProperty()
    var typeSubtype by typeSubtypeProperty

    @JsonIgnore
    val supplierProperty = SimpleBooleanProperty()
    var supplier by supplierProperty

    @JsonIgnore
    val packProperty = SimpleBooleanProperty()
    var pack by packProperty

    companion object {
        fun default() = SearchConfig().apply {
            name = true
            desc = true
            value = true
            supplier = false
            pack = false
            typeSubtype = false
        }
    }
}