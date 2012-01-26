package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.IRendererComponent
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory

/**
 *
 * @author ivo.nussbaumer
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    public int col
    public CustomTableCellRenderer (int col) {
        this.col = col
    }

    IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
        CustomTableModel model = (CustomTableModel)table.model
        CellRendererInformation rendererInformation = model.getCellRendererInformation(row, col)

        if (value instanceof String && ((String)value).isNumber() || value instanceof Number) {
            this.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT)
        } else {
            this.setHorizontalAlignment(DefaultTableCellRenderer.LEFT)
        }

        if (isSelected) {
            this.setBackground (Color.lightGray)
        } else {
            this.setBackground (Color.white)
        }

        this.setFont (this.getFont().deriveFont(Font.PLAIN))

        if (value instanceof String && ((String)value).isNumber() || value instanceof Number) {
            if (rendererInformation != null) {
                ULCNumberDataType dT = DataTypeFactory.numberDataType
                dT.minFractionDigits = rendererInformation.precision
                dT.maxFractionDigits = rendererInformation.precision
                setDataType (dT)
            }
        } else {
            setDataType (null)
            this.setFont (this.getFont().deriveFont(Font.BOLD))
        }

        return this
    }
}

public class CellRendererInformation {
    public int precision

    public CellRendererInformation() {
        precision = 2
    }
}
