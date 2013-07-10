package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import com.ulcjava.base.server.ULCSession
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.ModellingTableTreeColumn
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ModellingInformationTableTreeModel extends AbstractTableTreeModel {
    static List<String> columnNames = ["Name", "State", "Tags", "TransactionName", "Owner", "LastUpdateBy", "Created", "LastModification"]
    final IMutableTableTreeNode root = new DefaultMutableTableTreeNode("root")
    ModellingItemSearchService service = ModellingItemSearchService.getInstance()
    ModellingInformationTableTreeBuilder builder
    private ModellingTableTreeColumn enumModellingTableTreeColumn
    Map columnValues = [:]


    final int columnCount = columnNames.size()

    ModellingInformationTableTreeModel() {
        enumModellingTableTreeColumn = new ModellingTableTreeColumn()
        List<ModellingItem> modellingItems = service.getAllItems()
        builder = new ModellingInformationTableTreeBuilder(this)
        builder.buildTreeNodes(modellingItems)
        root = builder.root
    }

    Object getValueAt(Object node, int i) {
        if (i == 0) {
            return "${node.getValueAt(0)}".toString()
        } else if (node instanceof ItemNode) {
            return getValue(node.abstractUIItem.item, node, i)
        }
        return ""
    }

    public Object getChild(Object parent, int index) {
        return parent.getChildAt(index)
    }

    public int getChildCount(Object node) {
        return node.childCount
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0
    }

    public int getIndexOfChild(Object parent, Object child) {
        return parent.getIndex(child)
    }

    public synchronized Object getValue(def item, ItemNode node, int columnIndex) {
        if (!(item instanceof ModellingItem)) return ""
        try {
            return enumModellingTableTreeColumn.getEnumModellingTableTreeColumnFor(columnIndex).getValue(item, node)
        } catch (Exception ex) {
        }
        return null
    }

    public void putValues(ItemNode node) {
        for (int column = 0; column < columnNames.size(); column++) {
            addColumnValue(node.abstractUIItem.item, node, column, getValue(node.abstractUIItem.item, node, column))
        }
    }

    public void addColumnValue(Parameterization parameterization, ParameterizationNode node, int column, Object value) {
        if (columnValues[parameterization] == null)
            columnValues[parameterization] = new Object[columnNames.size()]
        columnValues[parameterization][column] = value
        node.values[column] = value
    }

    public void addColumnValue(def item, SimulationNode node, int column, Object value) {
        node.values[column] = value
    }

    public void addColumnValue(def item, ResultConfigurationNode node, int column, Object value) {
        node.values[column] = value
    }

    public void addColumnValue(def item, def node, int column, Object value) {
        if (node instanceof SimulationNode)
            node.values[column] = value
    }

    public void updateTreeStructure(ULCSession session){

        List<ModellingItemSearchService.ModellingItemEvent> items = service.getPendingEvents(session)
        items.each {ModellingItemSearchService.ModellingItemEvent itemEvent ->
            switch (itemEvent.eventType){
                case ModellingItemSearchService.ModellingItemEventType.ADDED:
                    builder.addNodeForItem(itemEvent.item)
                    break;
                case ModellingItemSearchService.ModellingItemEventType.REMOVED:
                    builder.removeNodeForItem(itemEvent.item)
                    break;
                case ModellingItemSearchService.ModellingItemEventType.UPDATED:
                    builder.itemChanged(itemEvent.item)
            }
        }

    }

}
