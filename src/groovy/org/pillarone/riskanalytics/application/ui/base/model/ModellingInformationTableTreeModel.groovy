package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.model.ChangeIndexerListener
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeModel extends AbstractTableTreeModel {

    List<String> columnNames = ["Name", "State", "Tags", "TransactionName", "Owner", "LastUpdateBy", "Created", "LastModification"]

    public static int NAME = 0
    public static int STATE = 1
    public static int TAGS = 2
    public static int TRANSACTION_NAME = 3
    public static int OWNER = 4
    public static int LAST_UPDATER = 5
    public static int CREATION_DATE = 6
    public static int LAST_MODIFICATION_DATE = 7
//    public static int ASSIGNED_TO = 10
//    public static int VISIBILITY = 11

    def columnValues = [:]
    int orderByColumn = -1
    boolean ascOrder
    ModellingInformationTableTreeBuilder builder
    DefaultMutableTableTreeNode root
    List parameterizationNodes
    ModellingItemNodeFilter filter
    List<ChangeIndexerListener> changeIndexerListeners
    RiskAnalyticsMainModel mainModel
    ModellingTableTreeColumn enumModellingTableTreeColumn = new ModellingTableTreeColumn()

    public static DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy")

    static Log LOG = LogFactory.getLog(ModellingInformationTableTreeModel)

    public ModellingInformationTableTreeModel(RiskAnalyticsMainModel mainModel) {
        this.mainModel = mainModel
        builder = new ModellingInformationTableTreeBuilder(this, mainModel)
        changeIndexerListeners = []
    }

    public static ModellingInformationTableTreeModel getInstance(RiskAnalyticsMainModel mainModel) {
        if (UserContext.hasCurrentUser()) {
            return new ModellingInformationTableTreeModel(mainModel)
        } else {
            return new StandaloneTableTreeModel(mainModel)
        }
    }

    public def buildTreeNodes() {
        builder.buildTreeNodes()
        root = builder.root
        extractNodeNames()
    }

    public void order(int column, boolean asc) {
        orderByColumn = column
        ascOrder = asc
        builder.order(getComparator(column, asc))
    }

    protected void extractNodeNames() {
        parameterizationNodes = []
        collectChildNames(root, parameterizationNodes)
    }

    protected def collectChildNames(ParameterizationNode node, List parameterizationNodes) {
        if (!parameterizationNodes.contains(node))
            parameterizationNodes << node
        node.childCount.times {
            collectChildNames(node.getChildAt(it), parameterizationNodes)
        }
    }

    protected def collectChildNames(ITableTreeNode node, List parameterizationNodes) {
        node.childCount.times {
            collectChildNames(node.getChildAt(it), parameterizationNodes)
        }
    }

    int getColumnCount() {
        return columnNames.size();
    }

    public String getColumnName(int i) {
        return UIUtils.getText(ModellingInformationTableTreeModel.class, this.columnNames[getColumnIndex(i)])
    }

    public String getColumnFilterName(int i) {
        return UIUtils.getText(ModellingInformationTableTreeModel.class, this.columnNames[i])
    }


    Object getValueAt(Object node, int i) {
        if (i == 0) {
            return "${node.getValueAt(0)}".toString()
        } else if (node instanceof ItemNode) {
            return getValue(node.abstractUIItem.item, node, i)
        }
        return ""
    }

    public Object getRoot() {
        return root
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

    public List getValues(int columnIndex) {
        if (columnIndex == ModellingInformationTableTreeModel.TAGS)
            return ModellingTableTreeColumnValues.getTagsValues()
        return ModellingTableTreeColumnValues.getValues(columnValues, columnIndex)
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

    public void refresh(AbstractUIItem item = null) {
        ExceptionSafe.protect {
            indexerChanged()
            item ? builder.refresh(item) : builder.refresh()
        }
    }

    public void addNodeForItem(Object item) {
        indexerChanged()
        builder.addNodeForItem(item)
    }

    public void itemChanged(ModellingItem item) {
        indexerChanged()
        builder.itemChanged(item)
    }

    public def addNodeForItem(Model model) {
        indexerChanged()
        builder.addNodeForItem model
    }

    ITableTreeNode findNodeForItem(ITableTreeNode node, Object item) {
        return TableTreeBuilderUtils.findNodeForItem(node, item)
    }

    public void removeAllGroupNodeChildren(ItemGroupNode groupNode) {
        builder.removeAllGroupNodeChildren groupNode
        indexerChanged()
    }

    public void removeNodeForItem(ModellingUIItem modellingUIItem) {
        builder.removeNodeForItem modellingUIItem
        indexerChanged()
    }

    public void removeNodeForItem(BatchUIItem batchUIItem) {
        builder.removeNodeForItem batchUIItem
    }

    public void refreshBatchNode() {
        builder.refreshBatchNode()
    }


    private def createAndInsertItemNode(DefaultMutableTableTreeNode node, ModellingItem item) {
        indexerChanged()
        builder.createAndInsertItemNode(node, item)
    }

    private def createAndInsertItemNode(DefaultMutableTableTreeNode node, BatchRun batchRun) {
        builder.createAndInsertItemNode(node, batchRun)
    }

    private Comparator getComparator(int column, boolean ascOrder) {
        return { x, y -> ascOrder ? x.values[column] <=> y.values[column] : y.values[column] <=> x.values[column] } as Comparator
    }

    public void addChangeIndexerListener(ChangeIndexerListener listener) {
        changeIndexerListeners << listener
    }

    public void removeChangeIndexerListener(ChangeIndexerListener listener) {
        changeIndexerListeners.remove(listener)
    }

    public void indexerChanged() {
        for (ChangeIndexerListener listener : changeIndexerListeners)
            listener.indexChanged()
    }

    public int getColumnIndex(int column) {
        return column
    }


}
