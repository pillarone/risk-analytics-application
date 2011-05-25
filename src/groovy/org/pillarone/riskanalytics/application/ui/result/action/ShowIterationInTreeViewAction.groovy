package org.pillarone.riskanalytics.application.ui.result.action

import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.util.LocaleResources

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowIterationInTreeViewAction extends SingleIterationAction {
    ResultView resultView
    ULCTable iteationTable

    public ShowIterationInTreeViewAction(model, tree, valueField, ResultView resultView, ULCTable iterationTable) {
        super(model, tree, valueField, "ShowIterationInTreeView")
        this.@resultView = resultView
        this.@iteationTable = iterationTable
    }


    public void doActionPerformed(ActionEvent event) {
        iteationTable.selectedRows.each {
            int iteration = iteationTable.model.getValueAt(it, 0)
            def function = function(iteration)
            addFunction(function)
            openedValues << iteration
        }

        resultView.tabbedPane.getTabCount().times {int tabIndex ->
            if (resultView.tabbedPane.getTitleAt(tabIndex) == LocaleResources.getString("ResultView.TreeView")) {
                resultView.tabbedPane.selectedIndex = tabIndex
            }
        }
        //todo fja change to RAMM mainModel
        resultView.p1ratModel.openItem(resultView.model.model, resultView.model.item)
    }
}
