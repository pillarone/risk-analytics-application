package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.batch.BatchRunner
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.simulation.item.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeBuilder {

    DefaultMutableTableTreeNode root
    AbstractTableTreeModel model
    static Log LOG = LogFactory.getLog(ModellingInformationTableTreeBuilder)

    static final int PARAMETERIZATION_NODE_INDEX = 0
    static final int RESULT_CONFIGURATION_NODE_INDEX = 1
    static final int SIMULATION_NODE_INDEX = 2

    public ModellingInformationTableTreeBuilder(model) {
        this.model = model;
        root = new DefaultMutableTableTreeNode("root")
    }

    public def buildTreeNodes() {
        getAllModelClasses().each {Class modelClass ->
            Model model = modelClass.newInstance()
            model.init()
            ITableTreeNode modelNode = getModelNode(model)
            DefaultMutableTableTreeNode parametrisationsNode = modelNode.getChildAt(PARAMETERIZATION_NODE_INDEX)
            DefaultMutableTableTreeNode resultConfigurationsNode = modelNode.getChildAt(RESULT_CONFIGURATION_NODE_INDEX)
            DefaultMutableTableTreeNode simulationsNode = modelNode.getChildAt(SIMULATION_NODE_INDEX)

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
            DefaultMutableTableTreeNode parameterizationsNode = new ItemGroupNode(UIUtils.getText(this.class, "Parameterization"), Parameterization)
            DefaultMutableTableTreeNode resultConfigurationsNode = new ItemGroupNode(UIUtils.getText(this.class, "ResultTemplates"), ResultConfiguration)
            DefaultMutableTableTreeNode simulationsNode = new ItemGroupNode(UIUtils.getText(this.class, "Results"), Simulation)
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

    public void order(def comparator) {
        root.childCount.times {childIndex ->
            def modelNode = root.getChildAt(childIndex)
            if (modelNode instanceof ModelNode) {
                DefaultMutableTableTreeNode parameterizationNode = modelNode.getChildAt(PARAMETERIZATION_NODE_INDEX)
                List nodes = []
                parameterizationNode.childCount.times { parameterizationnodeIndex ->
                    ParameterizationNode node = parameterizationNode.getChildAt(parameterizationnodeIndex)
                    nodes << node
                }

                parameterizationNode.removeAllChildren()
                model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parameterizationNode) as Object[]))

                nodes.sort(comparator)
                nodes.each {
                    parameterizationNode.add(it)
                }
                model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parameterizationNode) as Object[]))
            }
        }
    }

    public void refresh() {
        root.removeAllChildren()
        buildTreeNodes()
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
    }

    public ITableTreeNode refresh(ModellingItem item) {
        def node = findNodeForItem(findModelNode(item), item)
        model?.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        return node
    }

    public void addNodeForItem(Simulation item) {
        DefaultMutableTableTreeNode groupNode = findGroupNode(item, findModelNode(item))
        groupNode.leaf = false
        insertNodeInto(createNode(item), groupNode)
    }

    public def addNodeForItem(ModellingItem item) {
        ITableTreeNode groupNode = findGroupNode(item, findModelNode(item))
        createAndInsertItemNode(groupNode, item)
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]))
        return groupNode
    }

    public def addNodeForItem(BatchRun batchRun) {
        ITableTreeNode groupNode = findBatchRootNode()
        createAndInsertItemNode(groupNode, batchRun)
        model.nodesWereInserted(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]), [groupNode.childCount - 1] as int[])
        return groupNode
    }


    public void removeAllGroupNodeChildren(ItemGroupNode groupNode) {
        groupNode.removeAllChildren()
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]))
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
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
        root.add(createBatchNode())
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
    }

    protected ModelNode findModelNode(ModellingItem item) {
        ModelNode modelNode = null
        for (int i = 0; i < root.childCount && modelNode == null; i++) {
            def candidate = root.getChildAt(i)
            if (candidate.item.getClass().name.equals(item.modelClass.name)) {
                modelNode = candidate
            }
        }
        return modelNode
    }

    protected BatchRootNode findBatchRootNode() {
        for (int i = 0; i < root.childCount; i++) {
            DefaultMutableTableTreeNode candidate = root.getChildAt(i)
            if (candidate instanceof BatchRootNode) {
                return candidate
            }
        }
        return null
    }


    protected ItemGroupNode findGroupNode(ModellingItem item, ModelNode modelNode) {
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

    protected def createAndInsertItemNode(DefaultMutableTableTreeNode node, ModellingItem item) {
        boolean parameterNameFound = false
        for (int i = 0; i < node.childCount; i++) {
            if (item.name.equals(node.getChildAt(i).item.name)) {
                parameterNameFound = true
                if (GroovyUtils.getProperties(item).containsKey("versionNumber") && item.versionNumber.level > 1) {
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
                    if (GroovyUtils.getProperties(childNode.item).containsKey("versionNumber") && childNode.item.versionNumber.level == 1) {
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
            if (GroovyUtils.getProperties(newItemNode.item).containsKey("versionNumber") && newItemNode.item.versionNumber.toString().startsWith(childNode.item.versionNumber.toString())) {
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
        ParameterizationNode node = item.status == Status.NONE ? new ParameterizationNode(item) : new WorkflowParameterizationNode(item)
        ((ModellingInformationTableTreeModel) model).putValues(node)
        return node
    }

    private ITableTreeNode createNode(ResultConfiguration item) {
        ResultConfigurationNode node = new ResultConfigurationNode(item)
        ((ModellingInformationTableTreeModel) model).putValues(node)
        return node
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

    protected ITableTreeNode createBatchNode() {
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
        model.nodesWereInserted(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]), [parent.childCount - 1] as int[])
    }

    protected void removeNodeFromParent(DefaultMutableTableTreeNode itemNode) {
        DefaultMutableTableTreeNode parent = itemNode.getParent()
        parent.remove(parent.getIndex(itemNode))
        model.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
    }

}
