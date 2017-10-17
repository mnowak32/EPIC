package pl.cdbr.epic.ui

import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import pl.cdbr.epic.model.Group
import pl.cdbr.epic.model.Hierarchy
import pl.cdbr.epic.model.Part
import pl.cdbr.epic.model.Type
import pl.cdbr.epic.service.Database
import tornadofx.*

class EditorFragment : Fragment("Part editor") {
    val mode = (params["mode"] as EditorMode?) ?: EditorMode.NEW
    val part = (params["part"] as Part?)

    var selectedId = SimpleStringProperty()
    val groupsList = Hierarchy.groups.map { it.name }.observable()
    val selectedGroup = SimpleStringProperty()
    val typesList = mutableListOf<String>().observable()
    val selectedType = SimpleStringProperty()
    val subtypesList = mutableListOf<String>().observable()
    val selectedSubtype = SimpleStringProperty()
    val selectedName = SimpleStringProperty()
    val selectedValue = SimpleStringProperty()

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

        button("OK") {
            action {
                close()
            }
            onGrid(7, 3)
        }
    }


    init {
        selectedGroup.onChange { newGroup ->
            typesList.setAll(Hierarchy.types.filter { it.group.name == newGroup }.map { it.name })
        }

        selectedType.onChange { newType ->
            subtypesList.setAll(Hierarchy.subtypes.filter { it.type.name == newType }.map { it.name })
        }

        if (mode == EditorMode.NEW) {
            selectedId.value = Database.newPartId().toString()
        } else if (mode == EditorMode.EDIT && part != null) {
            selectedId.value = part.id.toString()
            selectedGroup.value = part.subtype.type.group.name
            selectedType.value = part.subtype.type.name
            selectedSubtype.value = part.subtype.name
            selectedName.value = part.name
            selectedValue.value = part.value
        }
    }
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