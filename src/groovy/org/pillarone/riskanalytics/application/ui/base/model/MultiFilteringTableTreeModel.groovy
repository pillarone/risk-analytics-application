package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MultiFilteringTableTreeModel extends FilteringTableTreeModel {

    List<ITableTreeFilter> filters

    public MultiFilteringTableTreeModel(ITableTreeModel model) {
        super(model, null)
        this.filters = []
    }

    @Override
    protected boolean isAcceptedNode(ITableTreeNode node) {
        if (!filters || filters.size() == 0) return true
        if (isNotInitialized(node)) return true
        for (ITableTreeFilter filter: filters) {
            boolean nodeAccepted = filter.acceptNode(node)
            if (!nodeAccepted && !(node instanceof SimulationNode)) {
                node.childCount.times {
                    nodeAccepted |= isAcceptedNode((node.getChildAt(it)))
                }
            }
            if (!nodeAccepted) return false
        }
        return true
    }



    public void addFilter(ITableTreeFilter filter) {
        def exist = filters.find {it.column == filter.column}
        if (!exist) {
            filters << filter
        }

    }

    public void applyFilter(ITableTreeFilter filter) {
        filters.clear()
        addFilter(filter)
        applyFilter()
    }

    public ITableTreeFilter getFilter(int columnIndex) {
        return filters.find { ModellingItemNodeFilter filter -> filter.column == columnIndex }
    }

    public void removeFilter(ITableTreeFilter filter) {
        filters.remove(filter)
    }

    public void refresh() {
        model.refresh()
    }

    private boolean isNotInitialized(ITableTreeNode node) {
        if (node instanceof ParameterizationNode && (!node.values || ((ParameterizationNode) node).values.isEmpty())) return true
        return false
    }



}
