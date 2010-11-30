package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.output.batch.BatchRunner

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameter.ParameterizationTag
import org.pillarone.riskanalytics.core.parameter.comment.CommentDAO
import org.pillarone.riskanalytics.core.parameter.comment.workflow.IssueStatus
import org.pillarone.riskanalytics.core.parameter.comment.workflow.WorkflowCommentDAO
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.simulation.item.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeModel extends AbstractTableTreeModel {

    List<String> columnNames = ["Name", "State", "Tags", "Comments", "ReviewComment", "Owner", "LastUpdateBy", "Created", "LastModification", "AssignedTo", "Visibility"]
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm")
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

    DefaultMutableTableTreeNode root

    public ModellingInformationTableTreeModel() {
        root = new DefaultMutableTableTreeNode("root")
    }

    public def buildTreeNodes() {
        getAllModelClasses().each {Class modelClass ->
            Model model = modelClass.newInstance()
            model.init()
            ITableTreeNode modelNode = getModelNode(model)
            DefaultMutableTableTreeNode parametrisationsNode = modelNode.getChildAt(0)
            DefaultMutableTableTreeNode resultConfigurationsNode = modelNode.getChildAt(1)
            DefaultMutableTableTreeNode simulationsNode = modelNode.getChildAt(2)

            getItemMap(getItemsForModel(modelClass, Parameterization), false).values().each { List<Parameterization> it ->
                parametrisationsNode.add(createItemNodes(it))
            }
            getItemMap(getItemsForModel(modelClass, Parameterization), true).values().each { List<Parameterization> it ->
                parametrisationsNode.add(createItemNodes(it))
            }

            getItemMap(getItemsForModel(modelClass, ResultConfiguration), false).values().each {
                resultConfigurationsNode.add(createItemNodes(it))
            }

            List simulationsForModel = getItemsForModel(modelClass, Simulation)
            if (simulationsForModel.size() == 0) {
                simulationsNode.leaf = true
            }
            simulationsForModel.each {
                try {
                    simulationsNode.add(createNode(it))
                } catch (Throwable t) {
                    LOG.error "Could not create node for ${it.toString()}", t
                }
            }
            root.add(modelNode)
        }
        root.add(createBatchNode())
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

            return getValue(item, i)
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
        switch (columnIndex) {
            case STATE: return parameterization?.status?.getDisplayName()
            case TAGS:
                String tags = ""
                List pTags = ParameterizationTag.executeQuery("select pTag.tag.name from ${ParameterizationTag.class.name} as pTag where pTag.parameterizationDAO.id = ? ", [parameterization.id])
                pTags.each {
                    tags += "${it}, "
                }
                return tags
            case COMMENTS: return CommentDAO.countByParameterization(parameterization.dao)
            case REVIEW_COMMENT: return WorkflowCommentDAO.countByParameterizationAndStatusNotEqual(parameterization.dao, IssueStatus.CLOSED)
            case OWNER: return parameterization?.getCreator()?.username
            case LAST_UPDATER: return parameterization?.getLastUpdater()?.username
            case CREATER: return format.format(parameterization.getCreationDate())
            case LAST_MODIFICATOR: return format.format(parameterization.getModificationDate())
            case ASSIGNED_TO: return "---"
            case VISIBILITY: return "---"
            default: return ""
        }
    }


    public List getItemsForModel(Class modelClass, Class clazz) {
        switch (clazz) {
            case Parameterization: return ModellingItemFactory.getParameterizationsForModel(modelClass)
            case ResultConfiguration: return ModellingItemFactory.getResultConfigurationsForModel(modelClass)
            case Simulation: return ModellingItemFactory.getActiveSimulationsForModel(modelClass)
            default: return []
        }
    }

    public List getAllModelClasses() {
        return ModelStructure.findAllModelClasses()
    }

    private ITableTreeNode getModelNode(Model model) {
        DefaultMutableTableTreeNode modelNode = null

        for (int i = 0; i < root.childCount && modelNode == null; i++) {
            ItemNode candidate = root.getChildAt(i)
            if (candidate.item.class == model.class) {
                modelNode = candidate
            }
        }

        if (modelNode == null) {
            modelNode = new ModelNode(model)
            DefaultMutableTableTreeNode parameterizationsNode = new ItemGroupNode(getText("Parameterization"), Parameterization)
            DefaultMutableTableTreeNode resultConfigurationsNode = new ItemGroupNode(getText("ResultTemplates"), ResultConfiguration)
            DefaultMutableTableTreeNode simulationsNode = new ItemGroupNode(getText("Results"), Simulation)
            modelNode.add(parameterizationsNode)
            modelNode.add(resultConfigurationsNode)
            modelNode.add(simulationsNode)
        }

        return modelNode
    }

    private Map getItemMap(items, boolean workflow) {
        Map map = [:]
        if (workflow) {
            items = items.findAll { it.versionNumber.toString().startsWith("R")}
        } else {
            items = items.findAll { !it.versionNumber.toString().startsWith("R")}
        }
        items.each {
            def list = map.get(it.name)
            if (!list) {
                list = []
                list.add(it)
                map.put(it.name, list)
            } else {
                list.add(it)
            }
        }
        map
    }

    private def createItemNodes(List items) {
        def tree = []
        tree.addAll(items)
        tree.sort {a, b -> b.versionNumber <=> a.versionNumber }

        def root = createNode(tree.first())
        tree.remove(tree.first())
        root.leaf = tree.empty

        def secondLevelNodes = tree.findAll { it.versionNumber.level == 1}
        secondLevelNodes.each {
            def node = createNode(it)
            createSubNodes(tree, node)
            root.add(node)
        }

        root
    }

    private void createSubNodes(def tree, ItemNode node) {
        def currentLevelNodes = tree.findAll {ModellingItem it ->
            it.versionNumber.isDirectChildVersionOf(node.item.versionNumber)
        }
        node.leaf = currentLevelNodes.size() == 0
        currentLevelNodes.each {
            def newNode = createNode(it)
            node.add(newNode)
            createSubNodes(tree, newNode)
        }
    }


    public void refresh() {
        root.removeAllChildren()
        buildTreeNodes()
        nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
    }

    public void refresh(ModellingItem item) {
        ITableTreeNode node = findNodeForItem(findModelNode(item), item)
        nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
    }

    public void addNodeForItem(Simulation item) {
        DefaultMutableTableTreeNode groupNode = findGroupNode(item, findModelNode(item))
        groupNode.leaf = false
        insertNodeInto(createNode(item), groupNode)
    }

    public def addNodeForItem(ModellingItem item) {
        ITableTreeNode groupNode = findGroupNode(item, findModelNode(item))
        createAndInsertItemNode(groupNode, item)
        nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]))
        return groupNode
    }

    public def addNodeForItem(BatchRun batchRun) {
        ITableTreeNode groupNode = findBatchRootNode()
        createAndInsertItemNode(groupNode, batchRun)
        nodesWereInserted(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]), [groupNode.childCount - 1] as int[])
        return groupNode
    }


    public void removeAllGroupNodeChildren(ItemGroupNode groupNode) {
        groupNode.removeAllChildren()
        nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]))
    }

    public void removeNodeForItem(ModellingItem item) {
        ITableTreeNode groupNode = findGroupNode(item, findModelNode(item))
        def itemNode = findNodeForItem(groupNode, item)
        if (itemNode instanceof SimulationNode) {
            itemNode.removeAllChildren()
        } else {
            if (itemNode.childCount > 0) {
                def parent = itemNode.parent
                def firstChild = itemNode.getChildAt(0)
                parent.add(firstChild)
                def children = []
                for (int i = 0; i < itemNode.childCount; i++) {
                    children << itemNode.getChildAt(i)
                }
                if (children.size() > 0) {
                    firstChild.leaf = false
                }
                children.each {
                    firstChild.add(it)
                }
                itemNode.removeAllChildren()
            }
        }
        removeNodeFromParent(itemNode)
    }

    public void removeNodeForItem(BatchRun batchRun) {
        ITableTreeNode groupNode = findBatchRootNode()
        def itemNode = findNodeForItem(groupNode, batchRun)
        removeNodeFromParent(itemNode)
    }

    public void refreshBatchNode() {
        ITableTreeNode batchNode = findBatchRootNode()

        removeNodeFromParent(batchNode)
        nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
        root.add(createBatchNode())
        nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
    }


    private ModelNode findModelNode(ModellingItem item) {
        ModelNode modelNode = null
        for (int i = 0; i < root.childCount && modelNode == null; i++) {
            def candidate = root.getChildAt(i)
            if (candidate.item.getClass().name.equals(item.modelClass.name)) {
                modelNode = candidate
            }
        }
        return modelNode
    }

    private BatchRootNode findBatchRootNode() {
        for (int i = 0; i < root.childCount; i++) {
            DefaultMutableTableTreeNode candidate = root.getChildAt(i)
            if (candidate instanceof BatchRootNode) {
                return candidate
            }
        }
        return null
    }


    private ItemGroupNode findGroupNode(ModellingItem item, ModelNode modelNode) {
        DefaultMutableTableTreeNode groupNode = null
        for (int i = 0; i < modelNode.childCount && groupNode == null; i++) {
            ITableTreeNode childNode = modelNode.getChildAt(i)
            if (childNode.itemClass == item.class) {
                groupNode = childNode
            }
        }
        groupNode
    }

    private ItemGroupNode findGroupNode(Parameterization item, ModelNode modelNode) {
        ItemGroupNode groupNode = null
        for (int i = 0; i < modelNode.childCount && groupNode == null; i++) {
            ItemGroupNode childNode = modelNode.getChildAt(i)
            if (childNode.itemClass == Parameterization) {
                groupNode = childNode
            }
        }
        return groupNode
    }

    private def createAndInsertItemNode(DefaultMutableTableTreeNode node, ModellingItem item) {
        boolean parameterNameFound = false
        for (int i = 0; i < node.childCount; i++) {
            if (item.name.equals(node.getChildAt(i).item.name)) {
                parameterNameFound = true
                if (item.properties.containsKey("versionNumber") && item.versionNumber.level > 1) {
                    insertSubversionItemNode(node.getChildAt(i), createNode(item))
                } else {
                    DefaultMutableTableTreeNode childNode = node.getChildAt(i)
                    DefaultMutableTableTreeNode newNode = createNode(item)
                    def children = []
                    childNode.childCount.times {
                        children << childNode.getChildAt(it)
                    }
                    children.each {newNode.add(it)}
                    childNode.removeAllChildren()
                    childNode.leaf = true
                    if (childNode.item.properties.containsKey("versionNumber") && childNode.item.versionNumber.level == 1) {
                        newNode.insert(childNode, 0)
                    } else {
                        insertSubversionItemNode(newNode, childNode)
                    }
                    node.add(newNode)
                    return
                }
            }
        }

        if (!parameterNameFound) {
            def newNode = createNode(item)
            newNode.leaf = true
            node.leaf = false
            insertNodeInto(newNode, node)
        }
    }


    private def createAndInsertItemNode(DefaultMutableTableTreeNode node, BatchRun batchRun) {
        DefaultMutableTableTreeNode newNode = createNode(batchRun)
        node.add(newNode)
    }


    private void insertSubversionItemNode(DefaultMutableTableTreeNode node, DefaultMutableTableTreeNode newItemNode) {
        node.childCount.times {
            DefaultMutableTableTreeNode childNode = node.getChildAt(it)
            if (newItemNode.item.properties.containsKey("versionNumber") && newItemNode.item.versionNumber.toString().startsWith(childNode.item.versionNumber.toString())) {
                if (newItemNode.item.versionNumber.isDirectChildVersionOf(childNode.item.versionNumber)) {
                    childNode.leaf = false
                    newItemNode.leaf = true
                    childNode.insert(newItemNode, childNode.childCount)
                } else {
                    insertSubversionItemNode(childNode, newItemNode)
                }
            }
        }
    }

    ITableTreeNode findNodeForItem(ITableTreeNode node, Object item) {
        ITableTreeNode nodeForItem = null
        if (nodeMatches(item, node)) {
            nodeForItem = node
        } else {
            for (int i = 0; i < node.childCount && nodeForItem == null; i++) {
                def childNode = node.getChildAt(i)
                nodeForItem = findNodeForItem(childNode, item)
            }
        }
        return nodeForItem
    }

    private ITableTreeNode createNode(String name) {
        new DefaultMutableTableTreeNode(name)
    }

    private ITableTreeNode createNode(Parameterization item) {
        return item.status == Status.NONE ? new ParameterizationNode(item) : new WorkflowParameterizationNode(item)
    }

    private ITableTreeNode createNode(ResultConfiguration item) {
        return new ResultConfigurationNode(item)
    }

    private ITableTreeNode createNode(BatchRun batchRun) {
        return new BatchRunNode(batchRun)
    }

    private ITableTreeNode createNode(Simulation item) {
        SimulationNode node = new SimulationNode(item)
        if (!item.isLoaded()) {
            item.load()
        }
        def paramsNode = createNode(item.parameterization)
        paramsNode.leaf = true
        def templateNode = createNode(item.template)
        templateNode.leaf = true

        node.add(paramsNode)
        node.add(templateNode)
        return node
    }

    private ITableTreeNode createBatchNode() {
        BatchRootNode batchesNode = new BatchRootNode("Batches")
        List<BatchRun> batchRuns = getAllBatchRuns()
        batchRuns?.each {BatchRun batchRun ->
            batchesNode.add(createNode(batchRun))
        }
        return batchesNode
    }

    protected List<BatchRun> getAllBatchRuns() {
        BatchRunner.getService().getAllBatchRuns()
    }


    private boolean nodeMatches(item, ParameterizationNode node) {
        return node.item == item
    }

    private boolean nodeMatches(item, WorkflowParameterizationNode node) {
        return node.item == item
    }

    private boolean nodeMatches(item, ResultConfigurationNode node) {
        return node.item == item
    }

    private boolean nodeMatches(item, SimulationNode node) {
        return node.item == item
    }

    private boolean nodeMatches(item, BatchRunNode node) {
        return node.item == item
    }


    private boolean nodeMatches(item, ITableTreeNode node) {
        return false
    }

    private void insertNodeInto(DefaultMutableTableTreeNode newNode, DefaultMutableTableTreeNode parent) {
        parent.insert(newNode, parent.childCount)
        nodesWereInserted(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]), [parent.childCount - 1] as int[])
    }

    private void removeNodeFromParent(DefaultMutableTableTreeNode itemNode) {
        DefaultMutableTableTreeNode parent = itemNode.getParent()
        parent.remove(parent.getIndex(itemNode))
        nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
        nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ModellingInformationTreeModel." + key);
    }

}
