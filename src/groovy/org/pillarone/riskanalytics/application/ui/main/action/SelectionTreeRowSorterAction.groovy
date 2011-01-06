package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeRowSorterAction extends ResourceBasedAction {

    ITableTreeModel model
    int column
    boolean ascOrder

    public SelectionTreeRowSorterAction(ITableTreeModel model, boolean asc, int column) {
        super(asc ? "AscRowSorterAction" : "DescRowSorterAction")
        this.model = model
        this.ascOrder = asc
        this.column = column
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        model.order(column, ascOrder)
    }

}
