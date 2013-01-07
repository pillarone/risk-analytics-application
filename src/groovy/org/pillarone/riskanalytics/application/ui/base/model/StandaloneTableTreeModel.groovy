package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
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
            case this.NAME: return NAME;
            case this.TAGS: return super.TAGS;
            case this.CREATION_DATE: return super.CREATION_DATE;
            case this.LAST_MODIFICATION_DATE: return super.LAST_MODIFICATION_DATE;
        }
        return column
    }

    Object getValueAt(Object node, int columnIndex) {
        super.getValueAt(node, getColumnIndex(columnIndex))
    }


}
