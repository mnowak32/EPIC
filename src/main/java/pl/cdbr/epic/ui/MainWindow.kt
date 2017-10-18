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
    val pageCounter = SimpleIntegerProperty(0)
    val currentPage = SimpleIntegerProperty(0)
    val partsOnPage = SimpleListProperty<Part>(partsList)
    val total = SimpleStringProperty(" Items count: ")

    val searchConfig = Database.config.searchConfig

    val itemsPerPage = 5

    override val root = borderpane {
        prefWidth = 1000.0
        center {
            tableview(partsOnPage.value) {
                itemsProperty().bind(partsOnPage)
                columnResizePolicy = SmartResize.POLICY

                column<Part, String>("Group", { ReadOnlyObjectWrapper<String>(it.value.subtype.type.group.name) }).prefWidth(90.0)
                column<Part, String>("Type", { ReadOnlyObjectWrapper<String>(it.value.subtype.type.name) }).prefWidth(90.0)
                column<Part, String>("SubType", { ReadOnlyObjectWrapper<String>(it.value.subtype.name) }).prefWidth(90.0)
                column("Name", Part::name).prefWidth(100.0)
                column("Description", Part::description).prefWidth(400.0).remainingWidth()
                column<Part, String>("Supplier", { ReadOnlyObjectWrapper<String>(it.value.supplier.name) }).prefWidth(90.0)
                column("Value", Part::value).prefWidth(80.0)
                column<Part, String>("Package", { ReadOnlyObjectWrapper<String>(it.value.pack.name) }).prefWidth(60.0)

                onDoubleClick {
                    openPartEditor(selectedItem)
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
                textfield(searchTerm) {
                    action {
                        doSearch()
                    }
                    setOnKeyPressed {
                        if (it.code == KeyCode.ESCAPE) {
                            this.text = ""
                            doSearch()
                        }
                    }
                }
                button(">") {
                    textAlignment = TextAlignment.CENTER
                    action {
                        doSearch()
                    }
                }

                menubutton("⋯") {
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
        bottom {
            pagination(pageCounter.value, 0) {
                pageCountProperty().bind(pageCounter)
                currentPageIndexProperty().apply {
                    bindBidirectional(currentPage)
                    addListener { _, _, newValue -> showPageNum(newValue.toInt()) }
                }
            }
        }
    }

    init {
        doSearch()
    }

    fun showPageNum(pageNum: Int) {
        println("Showing pageNum: $pageNum")
        val from = pageNum * itemsPerPage
        val to = minOf(from + itemsPerPage, partsList.size)
        partsOnPage.value = partsList.subList(from, to).observable()
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
        println("updating count")
        pageCounter.value = (partsList.size + itemsPerPage - 1) /  itemsPerPage
        total.value = " Items count: ${partsList.size}"
        currentPage.value = 0
        showPageNum(0)
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

