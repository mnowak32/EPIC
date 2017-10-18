package pl.cdbr.epic.ui

import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import pl.cdbr.epic.model.*
import pl.cdbr.epic.service.Database
import tornadofx.*

class EditorFragment : Fragment("Part editor") {
    val mode = (params["mode"] as EditorMode?) ?: EditorMode.NEW
    val part = (params["part"] as Part?)
    val mainWindow = (params["parent"] as MainWindow?)

    var selectedId = SimpleIntegerProperty()
    val groupsList = Hierarchy.groups.map { it.name }.observable()
    val selectedGroup = SimpleStringProperty()
    val typesList = mutableListOf<String>().observable()
    val selectedType = SimpleStringProperty()
    val subtypesList = mutableListOf<String>().observable()
    val selectedSubtype = SimpleStringProperty()
    val selectedName = SimpleStringProperty()
    val selectedValue = SimpleStringProperty()
    val errorMessage = SimpleStringProperty()

    override val root = gridpane {
        label("ID").onGrid(1, 1)
        label(selectedId).onGrid(1, 2)

        label("Hierarchy").onGrid(2, 1)
        combobox(selectedGroup, groupsList) {
            isEditable = true
            onGrid(2, 2)
        }
        combobox(selectedType, typesList) {
            isEditable = true
            onGrid(2, 3)
        }
        combobox(selectedSubtype, subtypesList) {
            isEditable = true
            onGrid(2, 4)
        }

        label("Name").onGrid(3, 1)
        textfield(selectedName).onGrid(3, 2)
        label("Value").onGrid(3, 3)
        textfield(selectedValue).onGrid(3, 4)

        label(errorMessage).onGrid(6, 1)

        button("Save") {
            action {
                doSave()
            }
            onGrid(7, 3)
        }
        button("Cancel") {
            action {
                close()
            }
            onGrid(7, 4)
        }

        setOnKeyPressed {
            if (it.code == KeyCode.ENTER && it.isControlDown) {
                doSave()
            }
        }
    }


    init {

        selectedGroup.onChange { newGroup ->
            typesList.setAll(Hierarchy.types.filter { it.group.name == newGroup }.map { it.name })
            selectedType.value = if (typesList.size > 0) { typesList.first() } else { "" }
        }

        selectedType.onChange { newType ->
            subtypesList.setAll(Hierarchy.subtypes.filter { it.type.name == newType }.map { it.name })
            selectedSubtype.value = if (subtypesList.size > 0) { subtypesList.first() } else { "" }
        }

        if (mode == EditorMode.NEW) {
            selectedId.value = Database.newPartId()
        } else if (mode == EditorMode.EDIT && part != null) {
            title = "Part editor: ${part.name} (${part.value})"
            selectedId.value = part.id
            selectedGroup.value = part.subtype.type.group.name
            selectedType.value = part.subtype.type.name
            selectedSubtype.value = part.subtype.name
            selectedName.value = part.name
            selectedValue.value = part.value
        }
    }

    fun doSave() {
        if (validate()) {
            val result = Part(
                    id = selectedId.value,
                    name = selectedName.value,
                    subtype = Hierarchy.of(selectedGroup.value, selectedType.value, selectedSubtype.value),
                    supplier = Supplier("none"),
                    value = selectedValue.value ?: "",
                    description = "",
                    count = 1,
                    pack = Package("none")
            )
            mainWindow?.editorFinished(result)
            close()
        } else {
            errorMessage.value = "Fix ya form!"
        }
    }

    fun validate() = (selectedName.value != null)
}

fun <T : Node> T.onGrid(row: Int, col: Int, rSpan: Int = 1, cSpan: Int = 1): T {
    val gpc = GridPaneConstraint(this)
    with(gpc) {
        rowIndex = row
        columnIndex = col
        rowSpan = rSpan
        columnSpan = cSpan
    }
    return gpc.applyToNode(this)
}

enum class EditorMode {
    NEW, EDIT
}