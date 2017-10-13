package pl.cdbr.epic

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.transformation.FilteredList
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.text.TextAlignment
import pl.cdbr.epic.model.Part
import pl.cdbr.epic.model.SearchConfig
import tornadofx.*

class MainWindow : View() {
    val partsList = FilteredList(Part.testData().observable())

    var txtSearch: TextField by singleAssign()
    val searchConfig = SearchConfig()

    override val root = borderpane {
        prefWidth = 1000.0
        center {
            tableview<Part> {
                items = partsList
                columnResizePolicy = SmartResize.POLICY

                column<Part, String>("Group", { ReadOnlyObjectWrapper<String>(it.value.subtype.type.group.name) }).prefWidth(90.0)
                column<Part, String>("Type", { ReadOnlyObjectWrapper<String>(it.value.subtype.type.name) }).prefWidth(90.0)
                column<Part, String>("SubType", { ReadOnlyObjectWrapper<String>(it.value.subtype.name) }).prefWidth(90.0)
                column("Name", Part::name).prefWidth(120.0)
                column("Description", Part::description).prefWidth(430.0).remainingWidth()
                column<Part, String>("Supplier", { ReadOnlyObjectWrapper<String>(it.value.supplier.name) }).prefWidth(90.0)
                column("Value", Part::value).prefWidth(90.0)
            }
        }
        top {
            toolbar {
                prefHeight = 40.0
                borderpaneConstraints {
                    alignment = Pos.CENTER
                }
                button("New item...")
                separator(Orientation.HORIZONTAL) {
                    prefWidth = 100.0
                }
                label("Search")
                txtSearch = textfield {
                    action {
                        doSearch(this.text)
                    }
                    setOnKeyPressed {
                        if (it.code == KeyCode.ESCAPE) {
                            this.text = ""
                            doSearch(this.text)
                        }
                    }
                }
                button(">") {
                    textAlignment = TextAlignment.CENTER
                    action {
                        doSearch(txtSearch.text)
                    }
                }
                
                contextmenu {
                    checkmenuitem("by Name").selectedProperty().bindBidirectional(searchConfig.nameProperty)
                    checkmenuitem("by Description").selectedProperty().bindBidirectional(searchConfig.descProperty)
                    checkmenuitem("by Value").selectedProperty().bindBidirectional(searchConfig.valueProperty)
                    checkmenuitem("by Type and Subtype").selectedProperty().bindBidirectional(searchConfig.typeSubtypeProperty)
                    checkmenuitem("by Supplier").selectedProperty().bindBidirectional(searchConfig.supplierProperty)
                    checkmenuitem("by Package").selectedProperty().bindBidirectional(searchConfig.packProperty)
                }
            }
        }
        bottom {
            hbox {
                prefHeight = 20.0
                borderpaneConstraints {
                    alignment = Pos.CENTER
                }
            }
        }
    }

    init {
        with(searchConfig) {
            name = true
            desc = true
            value = true
            supplier = false
            pack = false
            typeSubtype = false
        }
    }

    fun doSearch(query: String) {
        if (query.isBlank()) {
            partsList.setPredicate { true }
        } else {
            val searchRx = Regex(query, RegexOption.IGNORE_CASE)
            partsList.setPredicate { part ->
                (searchConfig.name && part.name.contains(searchRx)) ||
                        (searchConfig.desc && part.description.contains(searchRx)) ||
                        (searchConfig.value && part.value.contains(searchRx)) ||
                        (searchConfig.supplier && part.supplier.name.contains(searchRx)) ||
                        (searchConfig.pack && part.pack.name.contains(searchRx)) ||
                        (searchConfig.typeSubtype &&
                                (part.subtype.name.contains(searchRx)) || part.subtype.type.name.contains(searchRx))
            }
        }
    }
}