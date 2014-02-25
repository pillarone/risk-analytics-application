package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.search.IEventConsumer
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.ModellingTableTreeColumn
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import static org.pillarone.riskanalytics.application.search.ModellingItemSearchService.*

class ModellingInformationTableTreeModel extends AbstractTableTreeModel {
    protected static Log LOG = LogFactory.getLog(ModellingInformationTableTreeModel)

    static List<String> columnNames = ["Name", "State", "Tags", "TransactionName", "Owner", "LastUpdateBy", "Created", "LastModification"]
    @Lazy
    ModellingItemSearchService service = { getInstance() }()
    ModellingInformationTableTreeBuilder builder
    private ModellingTableTreeColumn enumModellingTableTreeColumn
    RiskAnalyticsMainModel mainModel
    Map columnValues = [:]
    int orderByColumn = -1
    boolean ascOrder

    public static int NAME = 0
    public static int STATE = 1
    public static int TAGS = 2
    public static int TRANSACTION_NAME = 3
    public static int OWNER = 4
    public static int LAST_UPDATER = 5
    public static int CREATION_DATE = 6
    public static int LAST_MODIFICATION_DATE = 7

    int columnCount = columnNames.size()

    FilterDefinition currentFilter = new FilterDefinition()

    private static String logTreeStructureUpdatesKey = "ModellingInformationTableTreeModel.logTreeStructureUpdates";
    private
    static boolean logTreeStructureUpdates = Boolean.valueOf(System.getProperty(logTreeStructureUpdatesKey, "false"));

    @Override
    ITableTreeNode getRoot() {
        builder.root
    }

    ModellingInformationTableTreeModel(RiskAnalyticsMainModel mainModel) {
        this.mainModel = mainModel
        enumModellingTableTreeColumn = new ModellingTableTreeColumn()
        builder = new ModellingInformationTableTreeBuilder(this, mainModel)
        if (logTreeStructureUpdates) {
            LOG.info("-D" + logTreeStructureUpdatesKey + " is true, will log tree structure updates");
        } else {
            LOG.info("-D" + logTreeStructureUpdatesKey + " not true, will NOT log tree structure updates");
        }
    }

    public void buildTreeNodes() {
        List<ModellingItem> modellingItems = getFilteredItems()
        builder.buildTreeNodes(modellingItems)
    }

    public List<ModellingItem> getFilteredItems() {
        return service.search(currentFilter.toQuery())
    }

    Object getValueAt(Object node, int i) {
        return getValue(node, i)
    }

    public def addNodeForItem(Model model) {
        builder.addNodeForItem(model)
    }

    public void addNodeForItem(BatchUIItem item) {
        builder.addNodeForItem(item)
    }

    public static ModellingInformationTableTreeModel getInstance(RiskAnalyticsMainModel mainModel) {
        if (UserContext.hasCurrentUser()) {
            return new ModellingInformationTableTreeModel(mainModel)
        } else {
            return new StandaloneTableTreeModel(mainModel)
        }
    }

    public String getColumnName(int i) {
        return UIUtils.getText(ModellingInformationTableTreeModel.class, this.columnNames[getColumnIndex(i)])
    }

    public String getColumnFilterName(int i) {
        return UIUtils.getText(ModellingInformationTableTreeModel.class, this.columnNames[i])
    }

    protected int getColumnIndex(int column) {
        return column
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

    private Object getValue(ItemNode node, int columnIndex) {
        return enumModellingTableTreeColumn.getEnumModellingTableTreeColumnFor(columnIndex).getValue(node.abstractUIItem.item)
    }

    private Object getValue(def node, int columnIndex) {
        getColumnValue(node, columnIndex)
    }

    String getColumnValue(def node, int columnIndex) {
        return columnIndex == 0 ? node.getValueAt(columnIndex) : null
    }

    private Object getValue(BatchRunNode node, int columnIndex) {
        getColumnValue(node, columnIndex)
    }

    public void putValues(ItemNode node) {
        for (int column = 0; column < columnNames.size(); column++) {
            addColumnValue(node.abstractUIItem.item, node, column, getValue(node, column))
        }
    }

    private addColumnValue(Parameterization parameterization, ParameterizationNode node, int column, Object value) {
        if (columnValues[parameterization] == null)
            columnValues[parameterization] = new Object[columnNames.size()]
        columnValues[parameterization][column] = value
        node.values[column] = value
    }

    private addColumnValue(def item, SimulationNode node, int column, Object value) {
        node.values[column] = value
    }

    private addColumnValue(def item, ResultConfigurationNode node, int column, Object value) {
        node.values[column] = value
    }

    private addColumnValue(def item, def node, int column, Object value) {
    }

    public void updateTreeStructure(IEventConsumer consumer) {
        List<ModellingItemEvent> wrapped = updateAndGetItemsFromModellingItemFactory(getPendingEvents(consumer))

        wrapped.each { ModellingItemEvent itemEvent ->
//          if (isAcceptedByCurrentFilter(itemEvent.item)) { //Uncomment later to fix PMO-2691
            switch (itemEvent.eventType) {
                case ModellingItemEventType.ADDED:
                    builder.addNodeForItem(itemEvent.item)
                    break;
                case ModellingItemEventType.REMOVED:
                    builder.removeNodeForItem(itemEvent.item)
                    break;
                case ModellingItemEventType.UPDATED:
                    builder.itemChanged(itemEvent.item)
                    break;
            }
//          } //Uncomment later to fix PMO-2691
        }
// try fix PMO-2679 - Detlef added event firing to add new p14n to dropdown list inside simulation window, but it disables the 'open results' button too after the sim.
//        if (items){
//            mainModel.fireModelChanged()
//        }
    }

    private List<ModellingItemEvent> updateAndGetItemsFromModellingItemFactory(List<ModellingItemEvent> items) {
        items.collect { ModellingItemEvent itemEvent ->
            def wrappedEvent = new ModellingItemEvent(
                    item: ModellingItemFactory.updateOrCreateModellingItem(itemEvent.item),
                    eventType: itemEvent.eventType
            )
            if (itemEvent.eventType == ModellingItemEventType.REMOVED) {
                ModellingItemFactory.remove(itemEvent.item)
            }
            wrappedEvent
        }
    }

// Uncomment later to fix PMO-2691
//    boolean isAcceptedByCurrentFilter(ModellingItem item) {
//        return currentFilter.toQuery().every {
//            it.accept(item)
//        }
//    }

    public List<ModellingItemEvent> getPendingEvents(IEventConsumer consumer) {
        service.getPendingEvents(consumer)
    }

    public void removeNodeForItem(BatchUIItem batchUIItem) {
        builder.removeNodeForItem batchUIItem
    }

    public void refresh() {
        ExceptionSafe.protect {
            refreshService()
            builder.removeAll()
            buildTreeNodes()
            nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
        }
    }

    private void refreshService() {
        service.refresh()
    }

    public void order(int column, boolean asc) {
        orderByColumn = column
        ascOrder = asc
        builder.order(getComparator(column, asc))
    }

    private Comparator getComparator(int column, boolean ascOrder) {
        return { x, y -> ascOrder ? x.values[column] <=> y.values[column] : y.values[column] <=> x.values[column] } as Comparator
    }

    void filterTree(FilterDefinition filterDefinition) {
        LOG.debug("Apply filter definition start.")
        List<ModellingItem> currentItems = builder.modellingItems
        currentFilter = filterDefinition
        List<ModellingItem> filteredItems = getFilteredItems()

        builder.removeNodesForItems(currentItems)
        builder.addNodesForItems(filteredItems)

        LOG.debug("Apply filter definition done.")
    }
}
