package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCSplitPane
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.result.model.AbstractResultViewModel
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction

class DeterministicResultView extends ResultView {

    public DeterministicResultView(AbstractResultViewModel model, RiskAnalyticsMainModel mainModel) {
        super(model, mainModel)
    }

    @Override
    protected void initTree() {
        super.initTree()
        model.periodCount.times {int index ->
            ULCTableTreeColumn column = new ResultTableTreeColumn(index + 1, this, new MeanFunction())
            column.setMinWidth(110)
            column.setHeaderRenderer(new CenteredHeaderRenderer())
            tree.viewPortTableTree.addColumn column
        }
    }

    protected ULCContainer layoutContent(ULCContainer content) {
        ULCBoxPane contentPane = new ULCBoxPane(1, 1)
        splitPane = new ULCSplitPane(ULCSplitPane.VERTICAL_SPLIT)
        splitPane.oneTouchExpandable = true
        splitPane.setResizeWeight(1)
        splitPane.setDividerSize(10)

        splitPane.setDividerLocation(ParameterView.DIVIDER)
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane)

        tabbedPane.removeAll()
        tabbedPane.addTab(getText("TreeView"), UIUtils.getIcon(getText("TreeView.icon")), content)
        tabbedPane.addTab(getText("Settings"), UIUtils.getIcon(getText("Settings.icon")), new ResultSettingsView(model.item, mainModel).content)
        tabbedPane.setCloseableTab(0, false)
        tabbedPane.setCloseableTab(1, false)

        splitPane.add(tabbedPane);
        splitPane.add(commentAndErrorView.tabbedPane)
        return splitPane
    }

    protected void addToolBarElements(ULCToolBar toolbar) {
        toolbar.addSeparator()
        addPrecisionFunctions(toolbar)
    }

}