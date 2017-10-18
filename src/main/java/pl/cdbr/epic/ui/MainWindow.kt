package pl.cdbr.epic.ui

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
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
    val partsList = Database.parts.observable().filtered { true }

    var txtSearch: TextField by singleAssign()
    var pagPages: Pagination by singleAssign()
    var tabPages: TableView<Part> by singleAssign()
    var labTotal: Label by singleAssign()

    val searchConfig = Database.config.searchConfig

    val itemsPerPage = 10

    override val root = borderpane {
        prefWidth = 1000.0
        center {
            tabPages = tableview<Part> {
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
                borderpaneConstraints {
                    alignment = Pos.CENTER
                }
                button("New part...") {
                    action {
                        openPartEditor()
                    }
                }
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

                menubutton("⋯") {
                    checkmenuitem("by Name").selectedProperty().bindBidirectional(searchConfig.nameProperty)
                    checkmenuitem("by Description").selectedProperty().bindBidirectional(searchConfig.descProperty)
                    checkmenuitem("by Value").selectedProperty().bindBidirectional(searchConfig.valueProperty)
                    checkmenuitem("by Type and Subtype").selectedProperty().bindBidirectional(searchConfig.typeSubtypeProperty)
                    checkmenuitem("by Supplier").selectedProperty().bindBidirectional(searchConfig.supplierProperty)
                    checkmenuitem("by Package").selectedProperty().bindBidirectional(searchConfig.packProperty)
                }
                labTotal = label("Items count: ${partsList.size}")
            }
        }
        bottom {
            pagPages = pagination(pageCount(), 0) {
                currentPageIndexProperty().addListener { _, _, newValue -> showPageNum(newValue.toInt()) }
            }
        }
    }

    init {
        showPageNum(0)
    }

    fun showPageNum(pageNum: Int) {
        println("Showing pageNum: $pageNum")
        val from = pageNum * itemsPerPage
        val to = from + itemsPerPage
        val toTrimmed = if (to > partsList.size) { partsList.size } else { to }
        tabPages.items = partsList.subList(from, toTrimmed).observable()
    }

    fun pageCount() = (partsList.size + itemsPerPage - 1) /  itemsPerPage

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
                                (part.subtype.name.contains(searchRx) || part.subtype.type.name.contains(searchRx)))
            }
        }
        updateCount()
    }

    private fun updateCount() {
        pagPages.pageCount = pageCount()
        labTotal.text = "Items count: ${partsList.size}"
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
        println("From editor: ${part}")
        if (part != null) {
            Database.savePart(part)
            doSearch(txtSearch.text)
        }
    }
}