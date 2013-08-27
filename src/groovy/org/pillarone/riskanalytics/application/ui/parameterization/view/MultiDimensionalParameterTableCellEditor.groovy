package org.pillarone.riskanalytics.application.ui.parameterization.view

import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import com.ulcjava.base.application.datatype.IDataType
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterTableModel
import org.joda.time.DateTime
import org.pillarone.ulc.server.ULCNonEmptyTextField

class MultiDimensionalParameterTableCellEditor extends DefaultCellEditor {

    int column

    public MultiDimensionalParameterTableCellEditor() {
        super(new ULCTextField())
    }

    public IEditorComponent getTableCellEditorComponent(ULCTable ulcTable, Object value, int row) {
        return createEditor(ulcTable, value, row, getTableModel(ulcTable).getPossibleValues(row, column))
    }

    private IEditorComponent createEditor(ULCTable table, def value, int row, def values) {
        IEditorComponent editorComponent = getEditor(table,value,row,values)
        editorComponent.dataType = getDataType(getMultiDimensionalParameter(table), value)
        return editorComponent
    }

    IEditorComponent getEditor(def table,def value, int row) {
        super.getTableCellEditorComponent(table, value, row)
    }

    IEditorComponent getEditor(def table,Number value, int row) {
        new ULCNonEmptyTextField()
    }

    private IEditorComponent createEditor(ULCTable table, def value, int row, List values) {
        return new ULCComboBox(values.sort())
    }

    protected IDataType getDataType(AbstractMultiDimensionalParameter parameter, def value) {
        return DataTypeFactory.getDataType(value, false)
    }

    protected IDataType getDataType(ConstrainedMultiDimensionalParameter parameter, def value) {
        if (column == 0) return null        // as the first column contains the row index
        Class columnType = parameter.constraints.getColumnType(column - 1)
        switch (columnType) {
            case Integer:
                return DataTypeFactory.getIntegerDataTypeForNonEdit()
            case Double:
                return DataTypeFactory.getDoubleDataTypeForNonEdit()
            case Date:
                return DataTypeFactory.getDateDataType()
            case DateTime:
                return DataTypeFactory.getDateDataType()
        }
        return null
    }

    protected AbstractMultiDimensionalParameter getMultiDimensionalParameter(ULCTable table) {
        return getTableModel(table).multiDimensionalParam
    }

    protected MultiDimensionalParameterTableModel getTableModel(ULCTable table) {
        return table.model
    }
}