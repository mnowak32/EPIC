package pl.cdbr.epic.ui

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Node
import javafx.scene.input.KeyCode
import pl.cdbr.epic.model.*
import pl.cdbr.epic.service.Database
import tornadofx.*

class EditorFragment : Fragment("Part editor") {
    val mode = (params["mode"] as EditorMode?) ?: EditorMode.NEW
    var part = (params["part"] as Part?)
    var partModel = PartModel(part)
    val mainWindow = (params["parent"] as MainWindow?)

    val groupsList = Hierarchy.groups.map { it.name }.observable()
    val typesList = mutableListOf<String>().observable()
    val subtypesList = mutableListOf<String>().observable()
    val errorMessage = SimpleStringProperty()
    val packsList = Packager.packs.map { it.name }.observable()

    override val root = form {
        fieldset("ID / Name / Value") {
            field {
                label(partModel.id)
                textfield(partModel.name)
                textfield(partModel.value)
            }
        }
        fieldset("Hierarchy") {
            field {
                combobox(partModel.groupName, groupsList) {
                    isEditable = true
                }
                combobox(partModel.typeName, typesList) {
                    isEditable = true
                }
                combobox(partModel.subtypeName, subtypesList) {
                    isEditable = true
                }
            }
        }
        fieldset("Other") {
            field("Package") {
                combobox(partModel.packName, packsList) {
                    isEditable = true
                }

            }
        }
        fieldset("Description") {
            textarea(partModel.description) {
                prefRowCount = 10
            }
        }


        fieldset {
            field {
                button("Save") {
                    action {
                        doSave()
                    }
                    disableProperty().bind(isInvalid())
                }
                button("Cancel") {
                    action {
                        close()
                    }
                }
            }
        }

        setOnKeyPressed {
            if (it.code == KeyCode.ENTER && it.isControlDown) {
                if (!isInvalid().value) {
                    doSave()
                }
            }
        }
    }


    init {

        partModel.groupName.onChange { newGroup ->
            typesList.setAll(Hierarchy.types.filter { it.group.name == newGroup }.map { it.name })
            partModel.typeName.value = if (typesList.size > 0) { typesList.first() } else { "" }
        }

        partModel.typeName.onChange { newType ->
            subtypesList.setAll(Hierarchy.subtypes.filter { it.type.name == newType }.map { it.name })
            partModel.subtypeName.value = if (subtypesList.size > 0) { subtypesList.first() } else { "" }
        }

        if (mode == EditorMode.NEW) {
            part = Part(Database.newPartId())
        } else if (mode == EditorMode.EDIT && part != null) {
            title = "Part editor: ${part.name} (${part.value})"
        }
    }

    fun doSave() {
        if (!isInvalid().value) {
            val result = Part(
                    id = selectedId.value,
                    name = selectedName.value,
                    subtype = Hierarchy.of(selectedGroup.value, selectedType.value, selectedSubtype.value),
                    supplier = Supplier("none"),
                    value = selectedValue.value ?: "",
                    description = selectedDescription.value,
                    count = 1,
                    pack = Packager.of(selectedPack.value)
            )
            mainWindow?.editorFinished(result)
            close()
        } else {
            errorMessage.value = "Fix ya form!"
        }
    }

    fun isInvalid() = (partModel.name.toProperty().isNull)
}

//fun <T : Node> T.onGrid(row: Int, col: Int, rSpan: Int = 1, cSpan: Int = 1): T {
//    val gpc = GridPaneConstraint(this).apply {
//        rowIndex = row
//        columnIndex = col
//        rowSpan = rSpan
//        columnSpan = cSpan
//    }
//    return gpc.applyToNode(this)
//}

enum class EditorMode {
    NEW, EDIT
}