package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.model.ChangeIndexerListener
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameter.ParameterizationTag
import org.pillarone.riskanalytics.core.parameter.comment.CommentDAO
import org.pillarone.riskanalytics.core.parameter.comment.workflow.WorkflowCommentDAO
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.parameter.comment.ParameterizationCommentDAO

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeModel extends AbstractTableTreeModel {

    List<String> columnNames = ["Name", "State", "Tags", "TransactionName", "Quarter", "Comments", "ReviewComment", "Owner", "LastUpdateBy", "Created", "LastModification", "AssignedTo", "Visibility"]

    public static int NAME = 0
    public static int STATE = 1
    public static int TAGS = 2
    public static int TRANSACTION_NAME = 3
    public static int QUARTER = 4
    public static int COMMENTS = 5
    public static int REVIEW_COMMENT = 6
    public static int OWNER = 7
    public static int LAST_UPDATER = 8
    public static int CREATION_DATE = 9
    public static int LAST_MODIFICATION_DATE = 10
    public static int ASSIGNED_TO = 11
    public static int VISIBILITY = 12

    def columnValues = [:]
    int orderByColumn = -1
    boolean ascOrder
    ModellingInformationTableTreeBuilder builder
    DefaultMutableTableTreeNode root
    List parameterizationNodes
    ModellingItemNodeFilter filter
    List<ChangeIndexerListener> changeIndexerListeners
    List<TransactionInfo> transactionInfos
    RiskAnalyticsMainModel mainModel

    static DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy")

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


    public Object getValue(Parameterization parameterization, ParameterizationNode node, int columnIndex) {
        def value = null
        try {
            switch (columnIndex) {
                case NAME: value = node.abstractUIItem.item.name; break;
                case STATE: value = parameterization?.status?.getDisplayName(); break;
                case TAGS: value = parameterization?.tags?.join(","); break;
                case TRANSACTION_NAME:
                    if (parameterization.dealId) {
                        value = getTransactionName(parameterization.dealId)
                    };
                    break;
                case QUARTER:
                    if (parameterization.dealId && parameterization.valuationDate) {
                        value = DateFormatUtils.formatDetailed(parameterization.valuationDate)
                    };
                    break;
                case COMMENTS: value = parameterization.getSize(ParameterizationCommentDAO); break;
                case REVIEW_COMMENT: value = parameterization.getSize(WorkflowCommentDAO); break;
                case OWNER: value = parameterization?.getCreator()?.username; break;
                case LAST_UPDATER: value = parameterization?.getLastUpdater()?.username; break;
                case CREATION_DATE: value = DateFormatUtils.formatDetailed(parameterization.creationDate); break;
                case LAST_MODIFICATION_DATE: value = DateFormatUtils.formatDetailed(parameterization.modificationDate); break;
                case ASSIGNED_TO: return "---"
                case VISIBILITY: return "---"
                default: return ""

            }

        } catch (Exception ex) {
        }
        return value
    }

    public Object getValue(def item, ItemNode node, int columnIndex) {
        if (item instanceof ModellingItem) {
            switch (columnIndex) {
                case NAME: return node.abstractUIItem.item.name
                case COMMENTS: return (item instanceof Simulation) ? item.getSize(SimulationRun) : 0;
                case REVIEW_COMMENT: return 0;
                case OWNER: return item?.getCreator()?.username;
                case LAST_UPDATER: return item?.getLastUpdater()?.username;
                case CREATION_DATE: return DateFormatUtils.formatDetailed(item.creationDate)
                case LAST_MODIFICATION_DATE: return DateFormatUtils.formatDetailed(item.modificationDate)
                case ASSIGNED_TO: return "---"
                case VISIBILITY: return "---"
                default: return ""

            }
        }
        return null
    }

    public List getValues(int columnIndex) {
        Set values = new TreeSet()
        if (columnIndex == TAGS) {
            ParameterizationTag.withTransaction {status ->
                Collection all = ParameterizationTag.findAll()
                all.each {
                    String tagName = it.tag.name
                    values.add(tagName)
                }
            }
        } else {
            columnValues?.each {Parameterization parameterization, def value ->
                if (value[columnIndex]) {
                    switch (columnIndex) {
                        case CREATION_DATE:
                        case LAST_MODIFICATION_DATE:
                            if (value[columnIndex] instanceof DateTime)
                                values.add(simpleDateFormat.print(value[columnIndex]))
                            else
                                values.add(value[columnIndex]);
                            break;
                        default: values.add(value[columnIndex]); break;
                    }
                }
            }

        }
        return values as List
    }

    public void putValues(ItemNode node) {
        for (int column = 0; column < columnNames.size() - 2; column++) {
            addColumnValue(node.abstractUIItem.item, node, column, getValue(node.abstractUIItem.item, node, column))
        }
    }

    public void addColumnValue(Parameterization parameterization, ParameterizationNode node, int column, Object value) {
        if (columnValues[parameterization] == null)
            columnValues[parameterization] = new Object[columnNames.size() - 1]
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
        for (ChangeIndexerListener listener: changeIndexerListeners)
            listener.indexChanged()
    }

    public int getColumnIndex(int column) {
        return column
    }

    private String getTransactionName(Long dealId) {
        try {
            if (transactionInfos == null) {
                transactionInfos = RemotingUtils.getTransactionService().allTransactions
            }
            TransactionInfo transactionInfo = transactionInfos.find {it.dealId == dealId}
            if (transactionInfo)
                return transactionInfo.getName()
        } catch (Exception ex) {
            if (dealId)
                return String.valueOf(dealId)
        }
        return ""
    }


}
