package org.pillarone.riskanalytics.application.ui.main.view.item
import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import com.ulcjava.base.application.util.ULCIcon
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class SimulationResultUIItem extends ModellingUiItemWithModel {

    SimulationResultUIItem(Model model, Simulation simulation) {
        super(model, simulation)
    }

    @Override
    void rename(String newName) {
        ItemNode itemNode = TableTreeBuilderUtils.findNodeForItem(navigationTableTreeModel.root as IMutableTableTreeNode, this.item)
        itemNode.userObject = newName
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("results-active.png")
    }

    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof ModellingUIItem)) {
            return false
        }
        return item.modelClass == obj.item.modelClass && item.name == obj.item.name
    }

    @Override
    int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder()
        hcb.append(item.modelClass.toString())
        hcb.append(item.modelClass.name)
        return hcb.toHashCode()
    }

    @Override
    Simulation getItem() {
        return super.item as Simulation
    }
}
