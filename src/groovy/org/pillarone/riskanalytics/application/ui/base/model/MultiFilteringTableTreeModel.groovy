package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode

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
        for (ITableTreeFilter filter: filters) {
            boolean nodeAccepted = filter.acceptNode(node)
            if (!nodeAccepted) {
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

    public ITableTreeFilter getFilter(int columnIndex) {
        return filters.find { ParameterizationNodeFilter filter -> filter.column == columnIndex }
    }

    public void removeFilter(ITableTreeFilter filter) {
        filters.remove(filter)
    }

    public void refresh() {
        filters.clear()
        model.refresh()
    }
}
