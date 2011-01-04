package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.output.batch.BatchRunner

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.text.SimpleDateFormat
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.parameter.ParameterizationTag
import org.pillarone.riskanalytics.core.parameter.comment.CommentDAO
import org.pillarone.riskanalytics.core.parameter.comment.workflow.IssueStatus
import org.pillarone.riskanalytics.core.parameter.comment.workflow.WorkflowCommentDAO
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeModel extends AbstractTableTreeModel {

    List<String> columnNames = ["Name", "State", "Tags", "Comments", "ReviewComment", "Owner", "LastUpdateBy", "Created", "LastModification", "AssignedTo", "Visibility"]

    final static int STATE = 1
    final static int TAGS = 2
    final static int COMMENTS = 3
    final static int REVIEW_COMMENT = 4
    final static int OWNER = 5
    final static int LAST_UPDATER = 6
    final static int CREATER = 7
    final static int LAST_MODIFICATOR = 8
    final static int ASSIGNED_TO = 9
    final static int VISIBILITY = 10

    def columnValues = [:]
    ModellingInformationTableTreeBuilder builder
    DefaultMutableTableTreeNode root
    List parameterizationNodes
    ParameterizationNodeFilter filter

    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm")

    static Log LOG = LogFactory.getLog(ModellingInformationTableTreeModel)

    public ModellingInformationTableTreeModel() {
        builder = new ModellingInformationTableTreeBuilder(this)
    }

    public def buildTreeNodes() {
        builder.buildTreeNodes()
        root = builder.root
        extractNodeNames()
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
            return "${node.getValueAt(0)}".toString()
        } else if (node instanceof ParameterizationNode) {
            Parameterization item = node.item
            if (!item.isLoaded())
                item.load(false)

            String value = getValue(item, i)
            node.values[i] = value
            return value
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


    public Object getValue(Parameterization parameterization, int columnIndex) {
        def value = null
        switch (columnIndex) {
            case STATE:
                value = parameterization?.status?.getDisplayName(); break;
            case TAGS:
                String tags = ""
                List pTags = ParameterizationTag.executeQuery("select pTag.tag.name from ${ParameterizationTag.class.name} as pTag where pTag.parameterizationDAO.id = ? ", [parameterization.id])
                pTags.each {
                    tags += "${it}, "
                }
                value = tags; break;
            case COMMENTS: value = CommentDAO.countByParameterization(parameterization.dao); break;
            case REVIEW_COMMENT: value = WorkflowCommentDAO.countByParameterizationAndStatusNotEqual(parameterization.dao, IssueStatus.CLOSED); break;
            case OWNER: value = parameterization?.getCreator()?.username; break;
            case LAST_UPDATER: value = parameterization?.getLastUpdater()?.username; break;
            case CREATER: value = format.format(parameterization.getCreationDate()); break;
            case LAST_MODIFICATOR: value = format.format(parameterization.getModificationDate()); break;
            case ASSIGNED_TO: return "---"
            case VISIBILITY: return "---"
            default: return ""

        }
        addColumnValue(parameterization, columnIndex, value)
        return value
    }

    public List getValues(int columnIndex) {
        Set values = new TreeSet()
        columnValues.each {Parameterization parameterization, def value ->
            if (value[columnIndex])
                values.add(value[columnIndex])
        }
        return values as List
    }

    public void addColumnValue(Parameterization parameterization, int column, Object value) {
        if (columnValues[parameterization] == null)
            columnValues[parameterization] = new Object[columnNames.size() - 1]
        if (columnValues[parameterization][column] == null) {
            columnValues[parameterization][column] = value
        }
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


}
