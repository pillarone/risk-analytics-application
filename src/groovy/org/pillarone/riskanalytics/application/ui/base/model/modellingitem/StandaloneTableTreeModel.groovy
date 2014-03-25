package org.pillarone.riskanalytics.application.ui.base.model.modellingitem
import groovy.transform.CompileStatic

@CompileStatic
class StandaloneTableTreeModel extends ModellingInformationTableTreeModel {

    static int NAME = 0
    static int TAGS = 1
    static int CREATION_DATE = 2
    static int LAST_MODIFICATION_DATE = 3
    int columnCount = 4

    protected int getColumnIndex(int column) {
        switch (column) {
            case TAGS: return super.TAGS;
            case CREATION_DATE: return super.CREATION_DATE;
            case LAST_MODIFICATION_DATE: return super.LAST_MODIFICATION_DATE;
        }
        return column
    }

    Object getValueAt(Object node, int columnIndex) {
        super.getValueAt(node, getColumnIndex(columnIndex))
    }


}
