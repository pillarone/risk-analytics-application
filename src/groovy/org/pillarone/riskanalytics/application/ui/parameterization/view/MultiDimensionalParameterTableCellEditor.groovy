package org.pillarone.riskanalytics.application.ui.parameterization.view

import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import com.ulcjava.base.application.*

class MultiDimensionalParameterTableCellEditor extends DefaultCellEditor {

    int column

    public MultiDimensionalParameterTableCellEditor() {
        super(new ULCTextField())
    }

    public IEditorComponent getTableCellEditorComponent(ULCTable ulcTable, Object value, int row) {
        return createEditor(ulcTable, value, row, ulcTable.model.getPossibleValues(row, column))
    }

    private IEditorComponent createEditor(ULCTable table, def value, int row, def values) {
        IEditorComponent editorComponent = super.getTableCellEditorComponent(table, value, row)
        editorComponent.dataType = DataTypeFactory.getDataType(value, true)
        return editorComponent
    }

    private IEditorComponent createEditor(ULCTable table, def value, int row, List values) {
        return new ULCComboBox(values.sort())
    }
}