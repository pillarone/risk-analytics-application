package org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable

import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.IListCellRenderer
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.table.ULCTableHeader
import com.ulcjava.base.application.ULCList
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.border.ULCAbstractBorder
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.border.ULCBevelBorder


class CustomTableRowHeaderRenderer extends ULCLabel implements IListCellRenderer {
    public CustomTableRowHeaderRenderer (ULCTable table) {
        ULCTableHeader header = table.getTableHeader()
        setOpaque(true)
        //setBorder(UIManagerHelper.getBorder("TableHeader.cellBorder"))

        setBorder (BorderFactory.createRaisedBevelBorder())
        setHorizontalAlignment(CENTER)
        setForeground(header.getForeground())
        setBackground(header.getBackground())
        setFont(header.getFont())
    }

    public IRendererComponent getListCellRendererComponent (ULCList list, Object value, boolean isSelected, boolean cellHasFocus, int index) {
        return this
    }
}
