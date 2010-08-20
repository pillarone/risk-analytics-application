package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory

public class ResultIterationDataTableCellRenderer extends DefaultTableCellRenderer {
    ULCNumberDataType numberDataType
    int horizontalAlignment
    int columnIndex = 0

    public ResultIterationDataTableCellRenderer(int index) {
        numberDataType = localNumberDataType
        columnIndex = index
        if (index == 0) {
            numberDataType.setGroupingUsed true
            numberDataType.setInteger true
            horizontalAlignment = ULCLabel.CENTER
        } else {
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
            horizontalAlignment = ULCLabel.RIGHT
        }
    }


    public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
        if (columnIndex > 0)
            updateFractionDigits(table)
        setDataType numberDataType
        setHorizontalAlignment(horizontalAlignment)
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row)
    }

    protected ULCNumberDataType getLocalNumberDataType() {
        if (!numberDataType) {
            numberDataType = DataTypeFactory.numberDataType
        }
        return numberDataType
    }

    public void updateFractionDigits(table) {
        if (table?.model?.numberDataType) {
            getLocalNumberDataType().setMinFractionDigits table.model.numberDataType.minFractionDigits
            getLocalNumberDataType().setMaxFractionDigits table.model.numberDataType.maxFractionDigits
        }
    }
}