package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.apache.commons.lang.builder.HashCodeBuilder
import com.ulcjava.base.application.ULCContainer
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
import org.pillarone.riskanalytics.application.ui.result.model.AbstractResultViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class ResultUIItem extends ModellingUIItem {

    public ResultUIItem(RiskAnalyticsMainModel model, Model simulationModel, Simulation simulation) {
        super(model, simulationModel, simulation)
    }

    ULCContainer createDetailView() {
        AbstractResultViewModel resultViewModel = getViewModel()
        ResultView view = createView(resultViewModel)
        return view.content
    }

    abstract protected ResultView createView(AbstractResultViewModel model)

    @Override
    void rename(String newName) {
        ITableTreeNode itemNode = navigationTableTreeModel.findNodeForItem(navigationTableTreeModel.root, this)
        itemNode.userObject = newName
        navigationTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(itemNode) as Object[]))
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("results-active.png")
    }

    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof ModellingUIItem)) return false
        return item.modelClass == obj.item.modelClass && item.name == obj.item.name
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        hcb.append(item.modelClass.toString())
        hcb.append(item.modelClass.name)
        return hcb.toHashCode()
    }


}
