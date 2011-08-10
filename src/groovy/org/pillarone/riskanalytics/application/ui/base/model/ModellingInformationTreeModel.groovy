package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.output.batch.BatchRunner

import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.workflow.Status

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry

//todo fja delete a class
class ModellingInformationTreeModel extends DefaultTableTreeModel {

    private static Log LOG = LogFactory.getLog(ModellingInformationTreeModel)

    public ModellingInformationTreeModel() {
        super(new DefaultMutableTreeNode("root"))
    }

    public def buildTreeNodes() {
        getAllModelClasses().each {Class modelClass ->
            Model model = modelClass.newInstance()
            model.init()
            ITableTreeNode modelNode = getModelNode(model)
            DefaultMutableTreeNode parametrisationsNode = modelNode.getChildAt(0)
            ItemGroupNode normalNode = parametrisationsNode.getChildAt(0)
            ItemGroupNode workflowNode = parametrisationsNode.getChildAt(1)
            DefaultMutableTreeNode resultConfigurationsNode = modelNode.getChildAt(1)
            DefaultMutableTreeNode simulationsNode = modelNode.getChildAt(2)

            getItemMap(getItemsForModel(modelClass, Parameterization), false).values().each { List<Parameterization> it ->
                normalNode.add(createItemNodes(it))
            }
            getItemMap(getItemsForModel(modelClass, Parameterization), true).values().each { List<Parameterization> it ->
                workflowNode.add(createItemNodes(it))
            }

            getItemMap(getItemsForModel(modelClass, ResultConfiguration), false).values().each {
                resultConfigurationsNode.add(createItemNodes(it))
            }

            List simulationsForModel = getItemsForModel(modelClass, Simulation)//ModellingItemFactory.getActiveSimulationsForModel(modelClass)
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
        return ModelRegistry.instance.allModelClasses.toList()
    }

    private ITableTreeNode getModelNode(Model model) {
        ITableTreeNode modelNode = null

        for (int i = 0; i < root.childCount && modelNode == null; i++) {
            ItemNode candidate = root.getChildAt(i)
            if (candidate.item.class == model.class) {
                modelNode = candidate
            }
        }

        if (modelNode == null) {
            modelNode = new ModelNode(model)
            DefaultMutableTreeNode parameterizationsNode = new ItemGroupNode(getText("Parameterization"), Parameterization)
            parameterizationsNode.add(new ItemGroupNode(getText("Normal"), Parameterization))
            parameterizationsNode.add(new ItemGroupNode(getText("Workflow"), Parameterization))
            DefaultMutableTreeNode resultConfigurationsNode = new ItemGroupNode(getText("ResultTemplates"), ResultConfiguration)
            DefaultMutableTreeNode simulationsNode = new ItemGroupNode(getText("Results"), Simulation)
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
        DefaultMutableTreeNode root = getRoot()
        root.removeAllChildren()
        buildTreeNodes()
        nodeStructureChanged(root)
    }

    public void refresh(ModellingItem item) {
        ITableTreeNode node = findNodeForItem(findModelNode(item), item)
        nodeChanged node
    }

    public def addNodeForItem(Simulation item) {
        ITableTreeNode groupNode = findGroupNode(item, findModelNode(item))
        groupNode.leaf = false
        insertNodeInto(createNode(item), groupNode, groupNode.childCount)
        return groupNode
    }

    public def addNodeForItem(ModellingItem item) {
        ITableTreeNode groupNode = findGroupNode(item, findModelNode(item))
        createAndInsertItemNode(groupNode, item)
//        nodeStructureChanged(groupNode)
        return groupNode
    }

    public def addNodeForItem(BatchRun batchRun) {
        ITableTreeNode groupNode = findBatchRootNode()
        createAndInsertItemNode(groupNode, batchRun)
        return groupNode
    }


    public void removeAllGroupNodeChildren(ItemGroupNode groupNode) {
        groupNode.removeAllChildren()
        nodeStructureChanged(groupNode)
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
        nodeStructureChanged(groupNode)
    }

    public void removeNodeForItem(BatchRun batchRun) {
        ITableTreeNode groupNode = findBatchRootNode()
        def itemNode = findNodeForItem(groupNode, batchRun)
        removeNodeFromParent(itemNode)
        nodeStructureChanged(groupNode)
    }

    public void refreshBatchNode() {
        ITableTreeNode batchNode = findBatchRootNode()

        removeNodeFromParent(batchNode)
        nodeStructureChanged(root)
        root.add(createBatchNode())
        nodeStructureChanged(root)
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
            DefaultMutableTreeNode candidate = root.getChildAt(i)
            if (candidate instanceof BatchRootNode) {
                return candidate
            }
        }
        return null
    }


    private ItemGroupNode findGroupNode(ModellingItem item, ModelNode modelNode) {
        ITableTreeNode groupNode = null
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
        return item.status == Status.NONE ? groupNode.getChildAt(0) : groupNode.getChildAt(1)
    }

    private def createAndInsertItemNode(DefaultMutableTreeNode node, ModellingItem item) {
        boolean parameterNameFound = false
        for (int i = 0; i < node.childCount; i++) {
            if (item.name.equals(node.getChildAt(i).item.name)) {
                parameterNameFound = true
                if (GroovyUtils.getProperties(item).containsKey("versionNumber") && item.versionNumber.level > 1) {
                    insertSubversionItemNode(node.getChildAt(i), createNode(item))
                } else {
                    DefaultMutableTreeNode childNode = node.getChildAt(i)
                    DefaultMutableTreeNode newNode = createNode(item)
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
            insertNodeInto(newNode, node, node.childCount)
        }
    }

    private def createAndInsertItemNode(DefaultMutableTreeNode node, BatchRun batchRun) {
        DefaultMutableTreeNode newNode = createNode(batchRun)
        node.add(newNode)
    }

    private void insertSubversionItemNode(DefaultMutableTreeNode node, DefaultMutableTreeNode newItemNode) {
        node.childCount.times {
            def childNode = node.getChildAt(it)
            if (GroovyUtils.getProperties(newItemNode.item).containsKey("versionNumber") && newItemNode.item.versionNumber.toString().startsWith(childNode.item.versionNumber.toString())) {
                if (newItemNode.item.versionNumber.isDirectChildVersionOf(childNode.item.versionNumber)) {
                    childNode.leaf = false
                    newItemNode.leaf = true
                    childNode.add(newItemNode)
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
        return node.item == item || node.item.id == item.id
    }

    private boolean nodeMatches(item, BatchRunNode node) {
        return node.item == item
    }


    private boolean nodeMatches(item, ITableTreeNode node) {
        return false
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




