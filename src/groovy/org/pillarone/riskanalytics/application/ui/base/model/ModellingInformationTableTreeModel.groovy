package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.text.SimpleDateFormat
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.parameter.comment.CommentDAO
import org.pillarone.riskanalytics.core.parameter.comment.workflow.WorkflowCommentDAO
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeModel extends AbstractTableTreeModel {

    List<String> columnNames = ["Name", "State", "Tags", "Comments", "ReviewComment", "Owner", "LastUpdateBy", "Created", "LastModification", "AssignedTo", "Visibility"]

    final static int NAME = 0
    final static int STATE = 1
    final static int TAGS = 2
    final static int COMMENTS = 3
    final static int REVIEW_COMMENT = 4
    final static int OWNER = 5
    final static int LAST_UPDATER = 6
    final static int CREATION_DATE = 7
    final static int LAST_MODIFICATION_DATE = 8
    final static int ASSIGNED_TO = 9
    final static int VISIBILITY = 10

    def columnValues = [:]
    int orderByColumn = -1
    boolean ascOrder
    ModellingInformationTableTreeBuilder builder
    DefaultMutableTableTreeNode root
    List parameterizationNodes
    ModellingItemNodeFilter filter

    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm")
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy")

    static Log LOG = LogFactory.getLog(ModellingInformationTableTreeModel)

    public ModellingInformationTableTreeModel() {
        builder = new ModellingInformationTableTreeBuilder(this)
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
        return UIUtils.getText(this.class, columnNames[i])
    }

    Object getValueAt(Object node, int i) {

        if (i == 0) {
            String value = "${node.getValueAt(0)}".toString()
            if (node instanceof ItemNode) {
                def item = node.item
                addColumnValue(item, node, 0, value);
            }
            return value
        } else if (node instanceof ItemNode) {
            return getValue(node.item, node, i)
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
        switch (columnIndex) {
            case STATE: value = parameterization?.status?.getDisplayName(); break;
            case TAGS: value = parameterization?.tags?.join(","); break;
            case COMMENTS: value = parameterization.getSize(CommentDAO); break;
            case REVIEW_COMMENT: value = parameterization.getSize(WorkflowCommentDAO); break;
            case OWNER: value = parameterization?.getCreator()?.username; break;
            case LAST_UPDATER: value = parameterization?.getLastUpdater()?.username; break;
            case CREATION_DATE: value = UIUtils.format(format, parameterization.getCreationDate()); break;
            case LAST_MODIFICATION_DATE: value = UIUtils.format(format, parameterization.getModificationDate()); break;
            case ASSIGNED_TO: return "---"
            case VISIBILITY: return "---"
            default: return ""

        }
        return value
    }

    public Object getValue(def item, ItemNode node, int columnIndex) {
        if (item instanceof ModellingItem) {
            switch (columnIndex) {
                case COMMENTS:
                case REVIEW_COMMENT: return 0;
                case OWNER: return item?.getCreator()?.username;
                case LAST_UPDATER: return item?.getLastUpdater()?.username;
                case CREATION_DATE: return UIUtils.format(format, item.getCreationDate());
                case LAST_MODIFICATION_DATE: return UIUtils.format(format, item.getModificationDate());
                case ASSIGNED_TO: return "---"
                case VISIBILITY: return "---"
                default: return ""

            }
        }
        return null
    }

    public List getValues(int columnIndex) {
        Set values = new TreeSet()
        columnValues?.each {Parameterization parameterization, def value ->
            if (value[columnIndex]) {
                switch (columnIndex) {
                    case TAGS:
                        def tags = value[columnIndex]?.split(",");
                        tags?.each { values.add(it.trim())}
                        break;
                    case CREATION_DATE:
                    case LAST_MODIFICATION_DATE: values.add(UIUtils.format(simpleDateFormat, value[columnIndex])); break;
                    default: values.add(value[columnIndex])
                }
            }
        }
        return values as List
    }

    public void putValues(ItemNode node) {
        for (int column = 0; column < columnNames.size() - 2; column++) {
            addColumnValue(node.item, node, column, getValue(node.item, node, column))
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

    }

    public void refresh() {
        builder.refresh()
    }

    public void refresh(ModellingItem item) {
        builder.refresh(item)
    }

    public void addNodeForItem(Simulation item) {
        builder.addNodeForItem item
    }

    public def addNodeForItem(ModellingItem item) {
        builder.addNodeForItem(item)
    }

    public def addNodeForItem(BatchRun batchRun) {
        builder.addNodeForItem batchRun
    }

    ITableTreeNode findNodeForItem(ITableTreeNode node, Object item) {
        return builder.findNodeForItem(node, item)
    }

    public void removeAllGroupNodeChildren(ItemGroupNode groupNode) {
        builder.removeAllGroupNodeChildren groupNode
    }

    public void removeNodeForItem(ModellingItem item) {
        builder.removeNodeForItem item
    }

    public void removeNodeForItem(BatchRun batchRun) {
        builder.removeNodeForItem batchRun
    }

    public void refreshBatchNode() {
        builder.refreshBatchNode()
    }


    private def createAndInsertItemNode(DefaultMutableTableTreeNode node, ModellingItem item) {
        builder.createAndInsertItemNode(node, item)
    }

    private def createAndInsertItemNode(DefaultMutableTableTreeNode node, BatchRun batchRun) {
        builder.createAndInsertItemNode(node, batchRun)
    }

    private Comparator getComparator(int column, boolean ascOrder) {
        return { x, y -> ascOrder ? x.values[column] <=> y.values[column] : y.values[column] <=> x.values[column] } as Comparator
    }


}
