package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

class RefreshAction extends ResourceBasedAction {
    AbstractTableTreeModel tableTreeModel

    public RefreshAction(AbstractTableTreeModel tableTreeModel) {
        super("Refresh")
        this.tableTreeModel = tableTreeModel
    }


    public void doActionPerformed(ActionEvent event) {
        tableTreeModel.refresh()
    }
}
