package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import com.ulcjava.base.application.tree.DefaultTreeModel
import com.ulcjava.base.application.tree.ITreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.batch.BatchRunner
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.simulation.item.*

class ModellingInformationTreeModel extends DefaultTreeModel {

    private static Log LOG = LogFactory.getLog(ModellingInformationTreeModel)

    public ModellingInformationTreeModel() {
        super(new DefaultMutableTreeNode("root"))
        buildTreeNodes()
    }

    private def buildTreeNodes() {
        ModelStructure.findAllModelClasses().each {Class modelClass ->
            Model model = modelClass.newInstance()
            model.init()
            ITreeNode modelNode = getModelNode(model)
            DefaultMutableTreeNode parametrisationsNode = modelNode.getChildAt(0)
            DefaultMutableTreeNode resultConfigurationsNode = modelNode.getChildAt(1)
            DefaultMutableTreeNode simulationsNode = modelNode.getChildAt(2)

            getItemMap(ModellingItemFactory.getParameterizationsForModel(modelClass)).values().each {
                parametrisationsNode.add(createItemNodes(it))
            }

            getItemMap(ModellingItemFactory.getResultConfigurationsForModel(modelClass)).values().each {
                resultConfigurationsNode.add(createItemNodes(it))
            }

            List simulationsForModel = ModellingItemFactory.getActiveSimulationsForModel(modelClass)
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

    private ITreeNode getModelNode(Model model) {
        ITreeNode modelNode = null

        for (int i = 0; i < root.childCount && modelNode == null; i++) {
            ItemNode candidate = root.getChildAt(i)
            if (candidate.item.class == model.class) {
                modelNode = candidate
            }
        }

        if (modelNode == null) {
            modelNode = new ModelNode(model)
            DefaultMutableTreeNode parameterizationsNode = new ItemGroupNode(getText("Parameterization"), Parameterization)
            DefaultMutableTreeNode resultConfigurationsNode = new ItemGroupNode(getText("ResultTemplates"), ResultConfiguration)
            DefaultMutableTreeNode simulationsNode = new ItemGroupNode(getText("Results"), Simulation)
            modelNode.add(parameterizationsNode)
            modelNode.add(resultConfigurationsNode)
            modelNode.add(simulationsNode)
        }

        return modelNode
    }

    private Map getItemMap(items) {
        Map map = [:]
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
        ITreeNode node = findNodeForItem(findModelNode(item), item)
        nodeChanged node
    }

    public void addNodeForItem(Simulation item) {
        ModelNode modelNode = findModelNode(item)
        if (!modelNode) return
        ITreeNode groupNode = findGroupNode(item, modelNode)
        groupNode.leaf = false
        insertNodeInto(createNode(item), groupNode, groupNode.childCount)
    }

    public void addNodeForItem(ModellingItem item) {
        ITreeNode groupNode = findGroupNode(item, findModelNode(item))
        createAndInsertItemNode(groupNode, item)
        nodeStructureChanged(groupNode)
    }

    public void addNodeForItem(BatchRun batchRun) {
        ITreeNode groupNode = findBatchRootNode()
        createAndInsertItemNode(groupNode, batchRun)
        nodeStructureChanged(groupNode)
    }


    public void removeAllGroupNodeChildren(ItemGroupNode groupNode) {
        groupNode.removeAllChildren()
        nodeStructureChanged(groupNode)
    }

    public void removeNodeForItem(ModellingItem item) {
        ITreeNode groupNode = findGroupNode(item, findModelNode(item))
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
        ITreeNode groupNode = findBatchRootNode()
        def itemNode = findNodeForItem(groupNode, batchRun)
        removeNodeFromParent(itemNode)
        nodeStructureChanged(groupNode)
    }

    public void refreshBatchNode() {
        ITreeNode batchNode = findBatchRootNode()

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
        ITreeNode groupNode = null
        for (int i = 0; i < modelNode.childCount && groupNode == null; i++) {
            ITreeNode childNode = modelNode.getChildAt(i)
            if (childNode.itemClass == item.class) {
                groupNode = childNode
            }
        }
        groupNode
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


    ITreeNode findNodeForItem(ITreeNode node, Object item) {
        ITreeNode nodeForItem = null
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

    private ITreeNode createNode(String name) {
        new DefaultMutableTreeNode(name)
    }

    private ITreeNode createNode(Parameterization item) {
        return new ParameterizationNode(item)
    }

    private ITreeNode createNode(ResultConfiguration item) {
        return new ResultConfigurationNode(item)
    }

    private ITreeNode createNode(BatchRun batchRun) {
        return new BatchRunNode(batchRun)
    }

    private ITreeNode createNode(Simulation item) {
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

    private ITreeNode createBatchNode() {
        BatchRootNode batchesNode = new BatchRootNode("Batches")
        List<BatchRun> batchRuns = BatchRunner.getService().getAllBatchRuns()
        batchRuns?.each {BatchRun batchRun ->
            batchesNode.add(createNode(batchRun))
        }
        return batchesNode
    }


    private boolean nodeMatches(item, ParameterizationNode node) {
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


    private boolean nodeMatches(item, ITreeNode node) {
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




