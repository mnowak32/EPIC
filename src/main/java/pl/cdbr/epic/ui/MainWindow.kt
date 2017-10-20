package pl.cdbr.epic.ui

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.text.TextAlignment
import pl.cdbr.epic.model.Part
import pl.cdbr.epic.model.SearchConfig
import pl.cdbr.epic.service.Database
import tornadofx.*
import tornadofx.Stylesheet.Companion.button
import tornadofx.Stylesheet.Companion.label

class MainWindow : View() {
    val partsList = Database.parts.filtered { true }

    val searchTerm = SimpleStringProperty("")
    val total = SimpleStringProperty(" Items count: ")

    val searchConfig = Database.config.searchConfig

    val itemsPerPage = 5

    override val root = borderpane {
        var txtSearch: TextField by singleAssign()
        var menuSearch: MenuButton by singleAssign()

        prefWidth = 1000.0
        center {
            tableview(partsList) {
                columnResizePolicy = SmartResize.POLICY

                column<Part, String>("Group", { ReadOnlyObjectWrapper<String>(it.value.subtype.type.group.name) }).prefWidth(90.0)
                column<Part, String>("Type", { ReadOnlyObjectWrapper<String>(it.value.subtype.type.name) }).prefWidth(90.0)
                column<Part, String>("SubType", { ReadOnlyObjectWrapper<String>(it.value.subtype.name) }).prefWidth(90.0)
                column("Name", Part::name).prefWidth(100.0)
                column("Description", Part::description).prefWidth(400.0).remainingWidth()
                column<Part, String>("Supplier", { ReadOnlyObjectWrapper<String>(it.value.supplier.name) }).prefWidth(90.0)
                column("Value", Part::value).prefWidth(80.0)
                column<Part, String>("Package", { ReadOnlyObjectWrapper<String>(it.value.pack.name) }).prefWidth(60.0)

                setRowFactory {
                    TableRow<Part>().apply {
                        onDoubleClick {
                            openPartEditor(selectedItem)
                        }
                    }
                }
            }
        }
        top {
            toolbar {
                prefHeight = 40.0
                button("New part...") {
                    action {
                        openPartEditor()
                    }
                }
                separator(Orientation.HORIZONTAL) {
                    prefWidth = 100.0
                }
                label("Search")
                txtSearch = textfield(searchTerm) {
                    action {
                        doSearch()
                    }
                    setOnKeyPressed { ev ->
                        when(ev.code) {
                            KeyCode.ESCAPE -> {
                                text = ""
                                doSearch()
                            }
                            KeyCode.DOWN -> {
                                menuSearch.show()
                            }
                            else -> {}
                        }
                    }
                }
                button(">") {
                    textAlignment = TextAlignment.CENTER
                    action {
                        doSearch()
                    }
                }

                menuSearch = menubutton("⋯") {
                    checkmenuitem("by Name").selectedProperty().bindBidirectional(searchConfig.nameProperty)
                    checkmenuitem("by Description").selectedProperty().bindBidirectional(searchConfig.descProperty)
                    checkmenuitem("by Value").selectedProperty().bindBidirectional(searchConfig.valueProperty)
                    checkmenuitem("by Type and Subtype").selectedProperty().bindBidirectional(searchConfig.typeSubtypeProperty)
                    checkmenuitem("by Supplier").selectedProperty().bindBidirectional(searchConfig.supplierProperty)
                    checkmenuitem("by Package").selectedProperty().bindBidirectional(searchConfig.packProperty)
                }
                label(total) {
                    textProperty().bind(total)
                }
            }
        }

        setOnKeyPressed { ev ->
            when(ev.code) {
                KeyCode.SLASH -> {
                    if (!txtSearch.isFocused) {
//                        ev.consume()
                        //changing focus immediately causes key press to be picked up by the textfield...
                        runLater {
                            txtSearch.requestFocus()
                        }
                    }
                }
//                KeyCode.
                else -> {}
            }
        }
    }

    init {
        doSearch()
    }

    fun doSearch() {
        val query = searchTerm.value
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
                                (part.subtype.name.contains(searchRx) || part.subtype.type.name.contains(searchRx)))
            }
        }
        updateCount()
    }

    private fun updateCount() {
        total.value = " Items count: ${partsList.size}"
    }

    fun openPartEditor(part: Part? = null) {
        val params = if (part == null) {
            mapOf("mode" to EditorMode.NEW, "parent" to this)
        } else {
            mapOf("mode" to EditorMode.EDIT, "part" to part, "parent" to this)
        }
        val fragment = find(EditorFragment::class, params)
        openInternalWindow(fragment)
    }

    fun editorFinished(part: Part? = null) {
        println("From editor: $part")
        if (part != null) {
            Database.savePart(part)
            doSearch()
        }
    }
}

