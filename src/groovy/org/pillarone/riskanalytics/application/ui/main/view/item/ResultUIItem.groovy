package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class ResultUIItem extends ModellingUIItem {

    public ResultUIItem(RiskAnalyticsMainModel model, Model simulationModel, Simulation simulation) {
        super(model, simulationModel, simulation)
    }

    @Override
    void rename(String newName) {
        ITableTreeNode itemNode = navigationTableTreeModel.findNodeForItem(navigationTableTreeModel.root, this)
        //todo fja
        //closeItem(item.modelClass.newInstance(), item)
        itemNode.userObject = newName
        navigationTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(itemNode) as Object[]))
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("results-active.png")
    }

}
