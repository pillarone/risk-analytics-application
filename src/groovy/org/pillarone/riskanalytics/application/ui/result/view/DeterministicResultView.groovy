package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.view.ResultSettingsView
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class DeterministicResultView extends ResultView {

    public DeterministicResultView(ResultViewModel model) {
        super(model)
    }


    protected ULCContainer layoutContent(ULCContainer content) {
        tabbedPane.removeAll()
        tabbedPane.addTab(getText("TreeView"), UIUtils.getIcon(getText("TreeView.icon")), content)
        tabbedPane.addTab(getText("Settings"), UIUtils.getIcon(getText("Settings.icon")), new ResultSettingsView(model.item, p1ratModel).content)
        tabbedPane.setCloseableTab(0, false)
        tabbedPane.setCloseableTab(1, false)
        return tabbedPane
    }

    protected void addToolBarElements(ULCToolBar toolbar) {
        toolbar.addSeparator()
        addPrecisionFunctions(toolbar)
    }



    protected void addColumns() {
        for (int i = 1; i < model.treeModel.columnCount + 1; i++) {
            ULCTableTreeColumn column = new ResultTableTreeColumn(i, tree.viewPortTableTree)
            column.setMinWidth(110)
            column.setHeaderRenderer(new CenteredHeaderRenderer())
            tree.viewPortTableTree.addColumn column
        }
    }

}