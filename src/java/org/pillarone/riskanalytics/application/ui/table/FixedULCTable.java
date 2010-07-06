package org.pillarone.riskanalytics.application.ui.table;

import com.ulcjava.base.application.ULCTable;
import com.ulcjava.base.application.table.ITableModel;

//
// Workarount for PMO-919: Headings Lost by Undocking Result Window (Tree View)
//


class FixedULCTable extends ULCTable {

    public FixedULCTable(ITableModel iTableModel) {
        super(iTableModel);
    }

    protected String typeString() {
        return FixedUITable.class.getName();
    }

}
