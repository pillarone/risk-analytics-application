package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component


@CompileStatic
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class StandaloneTableTreeModel extends NavigationTableTreeModel {

    static final int NAME = 0
    static final int TAGS = 1
    static final int CREATION_DATE = 2
    static final int LAST_MODIFICATION_DATE = 3
    int columnCount = 4

    protected int getColumnIndex(int column) {
        switch (column) {
            case TAGS: return super.TAGS;
            case CREATION_DATE: return super.CREATION_DATE;
            case LAST_MODIFICATION_DATE: return super.LAST_MODIFICATION_DATE;
            default: return column
        }
    }

    Object getValueAt(Object node, int columnIndex) {
        super.getValueAt(node, getColumnIndex(columnIndex))
    }
}
