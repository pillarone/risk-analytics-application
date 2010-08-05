package com.canoo.ulc.community.table.server;

import com.ulcjava.base.application.ULCTable;
import com.ulcjava.base.application.table.ITableModel;

//
// Workarount for PMO-919: Headings Lost by Undocking Result Window (Tree View)
//


class ULCFixedTable extends ULCTable {

    public ULCFixedTable(ITableModel iTableModel) {
        super(iTableModel);
    }

    protected String typeString() {
        return "com.canoo.ulc.community.table.client.UIFixedTable";
    }

}
