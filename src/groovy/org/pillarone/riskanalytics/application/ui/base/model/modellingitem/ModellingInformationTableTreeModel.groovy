package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.ModellingTableTreeColumn
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.search.CacheItemEventQueue
import org.pillarone.riskanalytics.application.ui.simulation.model.IBatchListener
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.registry.IModelRegistryListener
import org.pillarone.riskanalytics.core.modellingitem.CacheItem
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.search.CacheItemSearchService
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import javax.annotation.PostConstruct

import static org.pillarone.riskanalytics.core.search.CacheItemEvent.EventType.*

class ModellingInformationTableTreeModel extends AbstractTableTreeModel implements IModelRegistryListener, IBatchListener {
    protected static Log LOG = LogFactory.getLog(ModellingInformationTableTreeModel)

    static List<String> COLUMN_NAMES = ["Name", "State", "Tags", "TransactionName", "Owner", "LastUpdateBy", "Created", "LastModification"]
    CacheItemEventQueue navigationTableTreeModelQueue
    CacheItemSearchService cacheItemSearchService
    protected ModellingInformationTableTreeBuilder builder
    private ModellingTableTreeColumn enumModellingTableTreeColumn
    RiskAnalyticsMainModel riskAnalyticsMainModel
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

    int columnCount = COLUMN_NAMES.size()

    FilterDefinition currentFilter = new FilterDefinition()

    private static String logTreeStructureUpdatesKey = "ModellingInformationTableTreeModel.logTreeStructureUpdates";
    private
    static boolean logTreeStructureUpdates = Boolean.valueOf(System.getProperty(logTreeStructureUpdatesKey, "false"));

    @Override
    ITableTreeNode getRoot() {
        builder.root
    }

    ModellingInformationTableTreeModel() {
        enumModellingTableTreeColumn = new ModellingTableTreeColumn()
        if (logTreeStructureUpdates) {
            LOG.info("-D" + logTreeStructureUpdatesKey + " is true, will log tree structure updates");
        } else {
            LOG.info("-D" + logTreeStructureUpdatesKey + " not true, will NOT log tree structure updates");
        }
    }

    @PostConstruct
    void initialize() {
        riskAnalyticsMainModel.addBatchListener(this)
        builder = new ModellingInformationTableTreeBuilder(this, riskAnalyticsMainModel)
        buildTreeNodes()
    }

    void buildTreeNodes() {
        builder.buildTreeNodes(filteredItems)
    }

    void modelAdded(Class modelClass) {
        addNodeForItem(modelClass.newInstance() as Model)
    }

    public List<ModellingItem> getFilteredItems() {
        ModellingItemFactory.getOrCreateModellingItems(cacheItemSearchService.search(currentFilter.toQuery()))
    }

    Object getValueAt(Object node, int i) {
        return getValue(node, i)
    }

    public def addNodeForItem(Model model) {
        builder.addNodeForItem(model)
    }

    @Override
    void newBatchAdded(BatchRun batchRun) {
        builder.addNodeForItem(new BatchUIItem(riskAnalyticsMainModel, batchRun))
    }

    public String getColumnName(int i) {
        return UIUtils.getText(ModellingInformationTableTreeModel.class, COLUMN_NAMES[getColumnIndex(i)])
    }

    public String getColumnFilterName(int i) {
        return UIUtils.getText(ModellingInformationTableTreeModel.class, COLUMN_NAMES[i])
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
        for (int column = 0; column < COLUMN_NAMES.size(); column++) {
            addColumnValue(node.abstractUIItem.item, node, column, getValue(node, column))
        }
    }

    private addColumnValue(Parameterization parameterization, ParameterizationNode node, int column, Object value) {
        if (columnValues[parameterization] == null)
            columnValues[parameterization] = new Object[COLUMN_NAMES.size()]
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

    public void updateTreeStructure() {
        //only update tree for items which are accepted by the current filter.
        //for deleted elements we also have to update the tree, because the items for deletion are not fully mapped, so it could be that the filter does not work correctly.
        def events = getPendingEvents()
        eachNotFilteredOrDeleted(events) { ItemEvent itemEvent ->
            switch (itemEvent.eventType) {
                case ADDED:
                    builder.addNodeForItem(itemEvent.modellingItem)
                    break;
                case REMOVED:
                    builder.removeNodeForItem(itemEvent.modellingItem)
                    break;
                case UPDATED:
                    builder.itemChanged(itemEvent.modellingItem)
                    break;
            }
        }
        if (events) {
            riskAnalyticsMainModel.fireModelChanged()
        }
    }


    private eachNotFilteredOrDeleted(List<ItemEvent> events, Closure c) {
        events.each {
            if (it.eventType == REMOVED || isAcceptedByCurrentFilter(it.cacheItem)) {
                c.call(it)
            }
        }

    }

    boolean isAcceptedByCurrentFilter(CacheItem item) {
        return currentFilter.toQuery().every {
            it.accept(item)
        }
    }

    public List<ItemEvent> getPendingEvents() {
        navigationTableTreeModelQueue.pollCacheItemEvents().collect { CacheItemEvent event ->
            ModellingItem modellingItem
            if (event.eventType == REMOVED) {
                modellingItem = ModellingItemFactory.getOrCreateModellingItem(event.item)
                ModellingItemFactory.remove(modellingItem)
            } else {
                modellingItem = ModellingItemFactory.updateOrCreateModellingItem(event.item)
            }
            new ItemEvent(
                    cacheItem: event.item,
                    modellingItem: modellingItem,
                    eventType: event.eventType
            )
        }
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
        cacheItemSearchService.refresh()
        ModellingItemFactory.clear()
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
        currentFilter = filterDefinition
        builder.removeNodesForItems(builder.modellingItems)
        builder.addNodesForItems(filteredItems)
        LOG.debug("Apply filter definition done.")
    }

    static class ItemEvent {
        CacheItem cacheItem
        ModellingItem modellingItem
        CacheItemEvent.EventType eventType

        @Override
        String toString() {
            return "$cacheItem $eventType"
        }
    }
}
