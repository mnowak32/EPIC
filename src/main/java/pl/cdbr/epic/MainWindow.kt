package pl.cdbr.epic

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import pl.cdbr.epic.model.Part
import tornadofx.*

class MainWindow : View() {
    override val root : BorderPane by fxml("/fxml/MainWindow.fxml")
    val tabParts: TableView<Part> by fxid()
    val partsList = Part.testData().observable()

    val colId: TableColumn<Part, String> by fxid()
    val colGroup: TableColumn<Part, String> by fxid()
    val colType: TableColumn<Part, String> by fxid()
    val colSubtype: TableColumn<Part, String> by fxid()
    val colName: TableColumn<Part, String> by fxid()
    val colDescription: TableColumn<Part, String> by fxid()
    val colSupplier: TableColumn<Part, String> by fxid()
    val colValue: TableColumn<Part, String> by fxid()

    init {
        colId.cellValueFactory = PropertyValueFactory("id")
//        colGroup.cellValueFactory = { cdf: TableColumn.CellDataFeatures<Part, String> ->
//            ReadOnlyObjectWrapper<String>(cdf.value.subtype.type.group.name)
//        }
        colType.cellValueFactory = PropertyValueFactory("type")
        colSubtype.cellValueFactory = PropertyValueFactory("subtype")
        colName.cellValueFactory = PropertyValueFactory("name")
        colDescription.cellValueFactory = PropertyValueFactory("description")
        colSupplier.cellValueFactory = PropertyValueFactory("supplier")
        colValue.cellValueFactory = PropertyValueFactory("value")
        tabParts.items = FilteredList(partsList) { true }
    }
}