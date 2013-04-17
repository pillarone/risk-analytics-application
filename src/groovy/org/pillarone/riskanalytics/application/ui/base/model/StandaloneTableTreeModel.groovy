package org.pillarone.riskanalytics.application.ui.base.model

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class StandaloneTableTreeModel extends ModellingInformationTableTreeModel {

    static int NAME = 0
    static int TAGS = 1
    static int CREATION_DATE = 2
    static int LAST_MODIFICATION_DATE = 3

    public StandaloneTableTreeModel(RiskAnalyticsMainModel mainModel) {
        super(mainModel)
    }

    int getColumnCount() {
        4
    }


    @Override
    public int getColumnIndex(int column) {
        switch (column) {
            case NAME: return NAME;
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
