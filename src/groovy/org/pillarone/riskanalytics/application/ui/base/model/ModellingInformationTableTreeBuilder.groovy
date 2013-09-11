package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.*
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.components.IResource
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.output.batch.BatchRunner
import org.pillarone.riskanalytics.core.simulation.item.*
import org.pillarone.riskanalytics.core.util.ClassPathScanner
import org.pillarone.riskanalytics.core.workflow.Status
import org.springframework.core.type.filter.AssignableTypeFilter

import static org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils.*

class ModellingInformationTableTreeBuilder {
    static final int PARAMETERIZATION_NODE_INDEX = 0
    static final int RESULT_CONFIGURATION_NODE_INDEX = 1
    static final int SIMULATION_NODE_INDEX = 2
    static Log LOG = LogFactory.getLog(ModellingInformationTableTreeBuilder)

    /**
     * Initialize the resources only once, as the classpath will not change during runtime.
     */
    @Lazy
    private static List<Class> resources = {
        ClassPathScanner provider = new ClassPathScanner()
        provider.addIncludeFilter(new AssignableTypeFilter(IResource))
        List<String> acceptedResources = ConfigurationHolder.config.includedResources

        List<Class> classes = provider.findCandidateComponents("")*.beanClassName.collect { getClass().getClassLoader().loadClass(it) }
        return classes.findAll { acceptedResources.contains(it.simpleName) }
    }()

    final DefaultMutableTableTreeNode root
    private final AbstractTableTreeModel model
    private final RiskAnalyticsMainModel mainModel
    private boolean resourceNodeVisible

    public ModellingInformationTableTreeBuilder(AbstractTableTreeModel model, RiskAnalyticsMainModel mainModel) {
        this.model = model;
        this.mainModel = mainModel
        root = new DefaultMutableTableTreeNode("root")
    }

    public buildTreeNodes(List<ModellingItem> modellingItems) {
        buildResourcesNodes(modellingItems)
        buildBatchNodes()
        buildModelNodes(modellingItems)

    }

    public List<ModellingItem> getModellingItems() {
        List<ModellingItem> result = []
        internalGetModellingItems(root, result)
        return result
    }

    protected void internalGetModellingItems(ITableTreeNode currentNode, List<ModellingItem> list) {
        if (currentNode instanceof ItemNode) {
            Object item = currentNode.abstractUIItem.item
            if ((item instanceof ParametrizedItem) || (item instanceof ResultConfiguration)) {
                list << item
            }
            if (item instanceof Simulation) return
        }

        for (int i = 0; i < currentNode.childCount; i++) {
            internalGetModellingItems(currentNode.getChildAt(i), list)
        }
    }

    private buildModelNodes(List<ModellingItem> items) {
        getAllModelClasses().each { Class<Model> modelClass ->
            List<ModellingItem> itemsForModel = items.findAll { it.modelClass == modelClass }
            Map groupedItems = itemsForModel.groupBy { it.class.name }
            Model model = modelClass.newInstance()
            model.init()
            DefaultMutableTableTreeNode modelNode = getModelNode(model)
            DefaultMutableTableTreeNode parametrisationsNode = modelNode.getChildAt(PARAMETERIZATION_NODE_INDEX) as DefaultMutableTableTreeNode
            DefaultMutableTableTreeNode resultConfigurationsNode = modelNode.getChildAt(RESULT_CONFIGURATION_NODE_INDEX) as DefaultMutableTableTreeNode
            DefaultMutableTableTreeNode simulationsNode = modelNode.getChildAt(SIMULATION_NODE_INDEX) as DefaultMutableTableTreeNode
            addToNode(parametrisationsNode, groupedItems[Parameterization.name])
            addToNode(resultConfigurationsNode, groupedItems[ResultConfiguration.name])
            addSimulationsToNode(simulationsNode, groupedItems[Simulation.name])
            root.insert(modelNode, root.childCount - (resourceNodeVisible ? 2 : 1))
        }

    }

    private addSimulationsToNode(DefaultMutableTableTreeNode simulationsNode, List<ModellingItem> simulations) {
        simulationsNode.leaf = simulations?.size() == 0
        simulations?.each {
            try {
                simulationsNode.add(createNode(it))
            } catch (Throwable t) {
                LOG.error "Could not create node for ${it.toString()}", t
            }
        }
    }

    private addToNode(DefaultMutableTableTreeNode node, List<ModellingItem> items) {
        if (items) {
            sortByName(items)
            getItemMap(items, false).values().each {
                node.add(createItemNodes(it))
            }
            getItemMap(items, true).values().each {
                node.add(createItemNodes(it))
            }
        }
    }

    private void sortByName(List items) {
        items.sort { a, b -> a.name.compareToIgnoreCase(b.name) }
    }

    private buildBatchNodes() {
        root.add(createBatchNode())
    }

    private buildResourcesNodes(List<ModellingItem> items) {
        def resourceClasses = getAllResourceClasses()
        if (!resourceClasses.isEmpty()) {
            resourceNodeVisible = true
            ResourceGroupNode resourceGroupNode = new ResourceGroupNode("Resources")
            resourceClasses.each { Class resourceClass ->

                ResourceClassNode resourceNode = new ResourceClassNode(UIUtils.getText(ModellingInformationTableTreeModel.class, resourceClass.simpleName), resourceClass, mainModel)
                List<ModellingItem> resourceItems = items.findAll { ModellingItem item -> item instanceof Resource && item.modelClass == resourceClass }
                getItemMap(resourceItems, false).values().each {
                    resourceNode.add(createItemNodes(it))
                }
                resourceGroupNode.add(resourceNode)
            }
            root.add(resourceGroupNode)
        }
    }

    //legacy  - only used for model node insertions.
    private createModelNode(Model model) {
        Class modelClass = model.class

        model.init()
        ITableTreeNode modelNode = getModelNode(model)
        DefaultMutableTableTreeNode parametrisationsNode = modelNode.getChildAt(PARAMETERIZATION_NODE_INDEX) as DefaultMutableTableTreeNode
        DefaultMutableTableTreeNode resultConfigurationsNode = modelNode.getChildAt(RESULT_CONFIGURATION_NODE_INDEX) as DefaultMutableTableTreeNode
        DefaultMutableTableTreeNode simulationsNode = modelNode.getChildAt(SIMULATION_NODE_INDEX) as DefaultMutableTableTreeNode

        getItemMap(getItemsForModel(modelClass, Parameterization), false).values().each { List<Parameterization> it ->
            parametrisationsNode.add(createItemNodes(it))
        }
        getItemMap(getItemsForModel(modelClass, Parameterization), true).values().each { List<Parameterization> it ->
            parametrisationsNode.add(createItemNodes(it))
        }

        getItemMap(getItemsForModel(modelClass, ResultConfiguration), false).values().each {
            resultConfigurationsNode.add(createItemNodes(it))
        }

        addSimulationsToNode(simulationsNode, getItemsForModel(modelClass, Simulation))
        root.insert(modelNode, root.childCount - (resourceNodeVisible ? 2 : 1))
    }

    //legacy  - only used for mode node insertions.
    private <T> List<T> getItemsForModel(Class modelClass, Class<T> clazz) {
        switch (clazz) {
            case Resource: return ModellingItemFactory.getResources(modelClass)
            case Parameterization: return ModellingItemFactory.getParameterizationsForModel(modelClass)
            case ResultConfiguration: return ModellingItemFactory.getResultConfigurationsForModel(modelClass)
            case Simulation: return ModellingItemFactory.getActiveSimulationsForModel(modelClass)
            default: return []
        }
    }

    private List getAllModelClasses() {
        return ModelRegistry.instance.allModelClasses.toList()
    }

    private List<Class> getAllResourceClasses() {
        if (!(ConfigurationHolder.config?.includedResources instanceof List)) {
            LOG.info("Please note that there are no resource classes defined in the config.groovy file")
            return []
        }
        return resources
    }

    private DefaultMutableTableTreeNode getModelNode(Model model) {
        DefaultMutableTableTreeNode modelNode = null

        for (int i = 0; i < root.childCount && modelNode == null; i++) {
            ITableTreeNode candidate = root.getChildAt(i)
            if (candidate instanceof ItemNode && candidate.getItemClass() == model.class) {
                modelNode = candidate
            }
        }

        if (modelNode == null) {
            modelNode = new ModelNode(new ModelUIItem(mainModel, model))
            DefaultMutableTableTreeNode parameterizationsNode = new ItemGroupNode(UIUtils.getText(ModellingInformationTableTreeModel.class, "Parameterization"), Parameterization, mainModel)
            DefaultMutableTableTreeNode resultConfigurationsNode = new ItemGroupNode(UIUtils.getText(ModellingInformationTableTreeModel.class, "ResultTemplates"), ResultConfiguration, mainModel)
            DefaultMutableTableTreeNode simulationsNode = new ItemGroupNode(UIUtils.getText(ModellingInformationTableTreeModel.class, "Results"), Simulation, mainModel)
            modelNode.add(parameterizationsNode)
            modelNode.add(resultConfigurationsNode)
            modelNode.add(simulationsNode)
        }

        return modelNode
    }

    private Map getItemMap(items, boolean workflow) {
        Map<String, List> map = [:]
        items = items.findAll { workflow ? it.versionNumber.toString().startsWith("R") : !it.versionNumber.toString().startsWith("R") }
        items.each {
            List list = map.get(it.name)
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

    private ITableTreeNode createItemNodes(List items) {
        List tree = []
        tree.addAll(items)
        tree.sort { a, b -> b.versionNumber <=> a.versionNumber }

        IMutableTableTreeNode root = createNode(tree.first())
        tree.remove(tree.first())
        root.leaf = tree.empty

        List secondLevelNodes = tree.findAll { it.versionNumber.level == 1 }
        secondLevelNodes.each {
            IMutableTableTreeNode node = createNode(it)
            createSubNodes(tree, node)
            root.add(node)
        }

        root
    }

    private void createSubNodes(List tree, ItemNode node) {
        def currentLevelNodes = tree.findAll { ModellingItem it ->
            it.versionNumber.isDirectChildVersionOf(node.versionNumber)
        }
        node.leaf = currentLevelNodes.size() == 0
        currentLevelNodes.each {
            ItemNode newNode = createNode(it)
            node.add(newNode)
            createSubNodes(tree, newNode)
        }
    }

    public void order(Comparator comparator) {
        root.childCount.times { childIndex ->
            def modelNode = root.getChildAt(childIndex)
            if (modelNode instanceof ModelNode) {
                DefaultMutableTableTreeNode parameterizationGroupNode = modelNode.getChildAt(PARAMETERIZATION_NODE_INDEX) as DefaultMutableTableTreeNode
                List<ParameterizationNode> nodes = []
                parameterizationGroupNode.childCount.times { int nodeIndex ->
                    ParameterizationNode node = parameterizationGroupNode.getChildAt(nodeIndex) as ParameterizationNode
                    nodes << node
                }
                parameterizationGroupNode.removeAllChildren()
                model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parameterizationGroupNode) as Object[]))
                nodes.sort(comparator)
                nodes.each {
                    parameterizationGroupNode.add(it)
                }
                model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parameterizationGroupNode) as Object[]))
            }
        }
    }

    public DefaultMutableTableTreeNode addNodeForItem(Model model, boolean notifyStructureChanged = true) {
        createModelNode(model)
        if (notifyStructureChanged) {
            //TODO (db) check with Michi about the node structure changed'. Maybe we could use NodesWhereInserted, but somehow the index screws up.
            this.model.nodeStructureChanged(new TreePath(root))
        }
        return root
    }

    public DefaultMutableTableTreeNode addNodeForItem(Simulation item, boolean notifyStructureChanged = true) {
        if (item.end) {
            ModelNode modelNode = findModelNode(root, item)
            LOG.debug("Adding simulation to modelNode: $modelNode")
            if (modelNode) {
                DefaultMutableTableTreeNode groupNode = findGroupNode(item, modelNode)
                LOG.debug("Group node : ${groupNode.name}")
                groupNode.leaf = false
                insertNodeInto(createNode(item), groupNode, notifyStructureChanged)
                return groupNode
            }
        }
        return null
    }

    public DefaultMutableTableTreeNode addNodeForItem(ModellingItem modellingItem, boolean notifyStructureChanged = true) {
        ModellingUIItem modellingUIItem = UIItemFactory.createItem(modellingItem, null, mainModel)
        addNodeForUIItem(modellingUIItem, notifyStructureChanged)
    }

    public DefaultMutableTableTreeNode addNodeForItem(BatchUIItem batchRun, boolean notifyStructureChanged = true) {
        ITableTreeNode groupNode = findBatchRootNode(root)
        insertNodeInto(createNode(batchRun), groupNode, notifyStructureChanged)
        return groupNode
    }

    private DefaultMutableTableTreeNode addNodeForUIItem(ModellingUIItem modellingUIItem, boolean notifyStructureChanged) {
        ModelNode modelNode = findModelNode(root, modellingUIItem)
        LOG.debug("Model node : $modelNode")
        if (modelNode != null) { //item in db, but not enabled in Config
            ITableTreeNode groupNode = findGroupNode(modellingUIItem, modelNode)
            LOG.debug("Group node: $groupNode")
            createAndInsertItemNode(groupNode, modellingUIItem, notifyStructureChanged)
            return groupNode
        }
        return null
    }

    private DefaultMutableTableTreeNode addNodeForUIItem(ResourceUIItem modellingUIItem, boolean notifyStructureChanged) {
        ResourceGroupNode resourceGroup = findResourceGroupNode(root)
        if (resourceGroup) {
            ITableTreeNode itemGroupNode = findResourceItemGroupNode(resourceGroup, modellingUIItem.item.modelClass)
            createAndInsertItemNode(itemGroupNode, modellingUIItem, notifyStructureChanged)
            return itemGroupNode
        }
        return null
    }

    public void itemChanged(ModellingItem item) {
        ModelNode modelNode = findModelNode(root, item)
        LOG.debug("Modify item $item on modelNode: $modelNode")
        if (modelNode) {
            ITableTreeNode itemGroupNode = findGroupNode(item, modelNode)
            LOG.debug("GroupNode $itemGroupNode")
            itemNodeChanged(itemGroupNode, item)
        }
    }

    public void itemChanged(Parameterization item) {
        ModelNode modelNode = findModelNode(root, item)
        if (modelNode) {
            ITableTreeNode itemGroupNode = findGroupNode(item, modelNode)
            itemNodeChanged(itemGroupNode, item)
            ITableTreeNode simulationGroupNode = modelNode.getChildAt(SIMULATION_NODE_INDEX)
            findAllNodesForItem(simulationGroupNode, item).each {
                updateValues(item, it)
            }
        }
    }

    public void itemChanged(ResultConfiguration item) {
        ModelNode modelNode = findModelNode(root, item)
        if (modelNode) {
            ITableTreeNode itemGroupNode = findGroupNode(item, modelNode)
            itemNodeChanged(itemGroupNode, item)
            ITableTreeNode simulationGroupNode = modelNode.getChildAt(RESULT_CONFIGURATION_NODE_INDEX)
            itemNodeChanged(simulationGroupNode, item)
        }
    }

    private void itemNodeChanged(ITableTreeNode itemGroupNode, ModellingItem item) {
        ItemNode itemNode = findNodeForItem(itemGroupNode, item)
        LOG.debug("Node for item. ${itemNode?.name}")
        if (itemNode) {
            updateValues(item, itemNode)
        }
    }

    private void updateValues(ModellingItem item, ItemNode itemNode) {
        itemNode.abstractUIItem.update(item)
        model.putValues(itemNode)
        model?.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(itemNode) as Object[]))
    }

    private void itemNodeChanged(ITableTreeNode itemGroupNode, Simulation item) {
        ItemNode itemNode = findNodeForItem(itemGroupNode, item)
        if (!itemNode) {
            addNodeForItem(item, true)
        } else {
            model?.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(itemNode) as Object[]))
        }
    }

    public void itemChanged(Resource item) {
        ITableTreeNode itemGroupNode = findResourceItemGroupNode(findResourceGroupNode(root), item.modelClass)
        itemNodeChanged(itemGroupNode, item)
    }

    public void removeAllGroupNodeChildren(ItemGroupNode groupNode) {
        groupNode.removeAllChildren()
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]))
    }

    public void removeNodeForItem(ModellingUIItem modellingUIItem) {
        ModelNode modelNode = findModelNode(root, modellingUIItem)
        LOG.debug("Removing node from modelNode: $modelNode")
        if (modelNode) {
            ITableTreeNode groupNode = findGroupNode(modellingUIItem, modelNode)
            def itemNode = findNodeForItem(groupNode, modellingUIItem.item)
            if (!itemNode) {
                LOG.warn("Unable to remove item $modellingUIItem.item from tree as it could not be found.")
            } else {
                removeItemNode(itemNode, true)
                modellingUIItem.closeItem()
            }

        }
    }

    public void removeNodeForItem(ModellingItem modellingItem) {
        removeNodeForItem(UIItemFactory.createItem(modellingItem, null, mainModel))
    }

    public void addNodesForItems(List<ModellingItem> items) {
        Set<DefaultMutableTableTreeNode> parentNodes = new HashSet<DefaultMutableTableTreeNode>()
        items.each {
            DefaultMutableTableTreeNode parent = addNodeForItem(it, false)
            if (parent) {
                parentNodes.add(parent)
            }
        }
        parentNodes.each {
            model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(it) as Object[]))
        }

    }

    public void removeNodesForItems(List<ModellingItem> items) {
        Set<DefaultMutableTableTreeNode> parentNodes = new HashSet<DefaultMutableTableTreeNode>()
        items.each {
            ITableTreeNode node = null
            ITableTreeNode groupNode = internalFindGroupNode(it)
            if (groupNode) {
                node = findNodeForItem(groupNode, it)
            }
            if (!node) {
                LOG.debug("No node found for modelling item $it")
            }
            parentNodes.add(removeItemNode(node, false))

        }
        parentNodes.each {
            model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(it) as Object[]))
        }
    }

    private ITableTreeNode internalFindGroupNode(ModellingItem item) {
        ModelNode modelNode = findModelNode(root, item)
        return modelNode ? findGroupNode(item, modelNode) : null
    }

    private ITableTreeNode internalFindGroupNode(Resource item) {
        return findResourceItemGroupNode(findResourceGroupNode(root), item.modelClass)
    }

    public void removeNodeForItem(ResourceUIItem modellingUIItem) {
        ITableTreeNode itemGroupNode = findResourceItemGroupNode(findResourceGroupNode(root), modellingUIItem.item.modelClass)
        ITableTreeNode itemNode = findNodeForItem(itemGroupNode, modellingUIItem.item)
        if (!itemNode) return

        removeItemNode(itemNode, true)
    }

    private DefaultMutableTableTreeNode removeItemNode(DefaultMutableTableTreeNode itemNode, boolean notifyStructureChanged) {
        if (itemNode instanceof SimulationNode) {
            itemNode.removeAllChildren()
        } else {
            if (itemNode.childCount > 0) {
                DefaultMutableTableTreeNode parent = itemNode.parent
                int childIndex = parent.getIndex(itemNode)
                IMutableTableTreeNode firstChild = itemNode.getChildAt(0)
                parent.insert(firstChild, childIndex)
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
        return removeNodeFromParent(itemNode, notifyStructureChanged)
    }

    private DefaultMutableTableTreeNode removeNodeFromParent(DefaultMutableTableTreeNode itemNode, boolean notifyStructureChanged) {
        DefaultMutableTableTreeNode parent = itemNode.getParent() as DefaultMutableTableTreeNode
        int childIndex = parent.getIndex(itemNode)
        parent.remove(childIndex)
        if (notifyStructureChanged) {
            model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
        }
        return parent
    }


    public void removeNodeForItem(BatchUIItem batchUIItem) {
        ITableTreeNode groupNode = findBatchRootNode(root)
        ITableTreeNode itemNode = findNodeForItem(groupNode, batchUIItem)
        removeNodeFromParent(itemNode, true)
    }

    private createAndInsertItemNode(DefaultMutableTableTreeNode node, ModellingUIItem modellingUIItem, boolean notifyStructureChanged) {
        boolean parameterNameFound = false
        for (int i = 0; i < node.childCount; i++) {
            if (isMatchingParent(node.getChildAt(i).abstractUIItem, modellingUIItem)) {
                parameterNameFound = true
                DefaultMutableTableTreeNode newNode = createNode(modellingUIItem)
                DefaultMutableTableTreeNode childNode = node.getChildAt(i) as DefaultMutableTableTreeNode
                LOG.debug("Item with previous version already in tree. ${childNode}")
                if (modellingUIItem.isVersionable() && modellingUIItem.item.versionNumber.level > 1) {
                    insertSubversionItemNode(childNode, newNode, notifyStructureChanged)
                } else {
                    def children = []
                    childNode.childCount.times {
                        children << childNode.getChildAt(it)
                    }
                    children.each { newNode.add(it) }
                    childNode.removeAllChildren()
                    childNode.leaf = true
                    node.remove(i)
                    if (childNode.abstractUIItem.isVersionable() && childNode.abstractUIItem.item.versionNumber.level == 1) {
                        newNode.insert(childNode, 0)
                    } else {
                        insertSubversionItemNode(newNode, childNode, notifyStructureChanged)
                    }
                    node.insert(newNode, i)
                    if (notifyStructureChanged) {
                        //TODO (db) check with Michi about the node structure changed'. Maybe we could use NodesWhereInserted, but somehow the index screws up.
                        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
                    }
                    return
                }
            }
        }

        if (!parameterNameFound) {
            LOG.debug("Add new item ${modellingUIItem}")
            def newNode = createNode(modellingUIItem)
            newNode.leaf = true
            node.leaf = false
            insertNodeInto(newNode, node, notifyStructureChanged)
        }
    }

    private createAndInsertItemNode(DefaultMutableTableTreeNode node, BatchUIItem batchUIItem, boolean notifyStructureChanged) {
        DefaultMutableTableTreeNode newNode = createNode(batchUIItem)
        node.add(newNode)
    }

    private boolean isMatchingParent(IUIItem currentItem, IUIItem itemToAdd) {
        return currentItem.name == itemToAdd.name
    }

    private boolean isMatchingParent(ParameterizationUIItem currentItem, ParameterizationUIItem itemToAdd) {
        if (currentItem.item.versionNumber.isWorkflow()) {
            if (!(itemToAdd.item.versionNumber.isWorkflow())) {
                return false
            }
        } else {
            if (itemToAdd.item.versionNumber.isWorkflow()) {
                return false
            }
        }
        return currentItem.name == itemToAdd.name
    }

    private void insertSubversionItemNode(DefaultMutableTableTreeNode node, DefaultMutableTableTreeNode newItemNode, boolean notifyStructureChanged) {
        node.childCount.times {
            DefaultMutableTableTreeNode childNode = node.getChildAt(it)
            if (newItemNode.abstractUIItem.isVersionable() && newItemNode.abstractUIItem.item.versionNumber.toString().startsWith(childNode.abstractUIItem.item.versionNumber.toString())) {
                if (newItemNode.abstractUIItem.item.versionNumber.isDirectChildVersionOf(childNode.abstractUIItem.item.versionNumber)) {
                    childNode.leaf = false
                    newItemNode.leaf = true
                    childNode.add(newItemNode)
                    if (notifyStructureChanged) {
                        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(childNode) as Object[]))
                    }
                } else {
                    insertSubversionItemNode(childNode, newItemNode, notifyStructureChanged)
                }
            }
        }
    }

    private DefaultMutableTableTreeNode createNode(String name) {
        new DefaultMutableTableTreeNode(name)
    }

    private DefaultMutableTableTreeNode createNode(Parameterization item) {
        return createNode(new ParameterizationUIItem(mainModel, item.modelClass?.newInstance(), item))
    }

    private DefaultMutableTableTreeNode createNode(ParameterizationUIItem parameterizationUIItem) {
        ParameterizationNode node = parameterizationUIItem.item.status == Status.NONE ? new ParameterizationNode(parameterizationUIItem) : new WorkflowParameterizationNode(parameterizationUIItem)
        model.putValues(node)
        return node
    }

    private DefaultMutableTableTreeNode createNode(ResultConfiguration item) {
        return createNode(new ResultConfigurationUIItem(mainModel, item.modelClass?.newInstance(), item))
    }

    private DefaultMutableTableTreeNode createNode(ResultConfigurationUIItem resultConfigurationUIItem) {
        ResultConfigurationNode node = new ResultConfigurationNode(resultConfigurationUIItem)
        model.putValues(node)
        return node
    }

    private DefaultMutableTableTreeNode createNode(Resource item) {
        return createNode(new ResourceUIItem(mainModel, null, item))
    }

    private DefaultMutableTableTreeNode createNode(ResourceUIItem item) {
        ResourceNode node = new ResourceNode(item)
        model.putValues(node)
        return node
    }

    private DefaultMutableTableTreeNode createNode(BatchUIItem batchUIItem) {
        return new BatchRunNode(batchUIItem)
    }

    private DefaultMutableTableTreeNode createNode(BatchRun batchRun) {
        return new BatchRunNode(new BatchUIItem(mainModel, batchRun))
    }

    private DefaultMutableTableTreeNode createNode(Simulation item) {
        SimulationNode node = null
        Model selectedModelInstance = item.modelClass?.newInstance()
        try {
            node = new SimulationNode(UIItemFactory.createItem(item, selectedModelInstance, mainModel))
            DefaultMutableTableTreeNode paramsNode = createNode(item.parameterization)
            paramsNode.leaf = true
            DefaultMutableTableTreeNode templateNode = createNode(item.template)
            templateNode.leaf = true

            node.add(paramsNode)
            node.add(templateNode)
            model.putValues(node)
        } catch (Exception ex) {
            LOG.error "create simulation exception", ex
        }
        return node
    }

    private DefaultMutableTableTreeNode createBatchNode() {
        BatchRootNode batchesNode = new BatchRootNode("Batches", mainModel)
        List<BatchRun> batchRuns = getAllBatchRuns()
        batchRuns?.each { BatchRun batchRun ->
            batchesNode.add(createNode(batchRun))
        }
        return batchesNode
    }

    private List<BatchRun> getAllBatchRuns() {
        BatchRunner.getService().getAllBatchRuns()
    }

    private void insertNodeInto(DefaultMutableTableTreeNode newNode, DefaultMutableTableTreeNode parent, boolean notifyStructureChanged) {
        List<ModellingUIItem> children = []
        parent.childCount.times { i ->
            children << parent.getChildAt(i)
        }
        children << newNode
        sortByName(children)
        int newIndex = children.indexOf(newNode)

        parent.insert(newNode, newIndex)
        LOG.debug("Node ${newNode} added at index $newIndex to parent: $parent")
        if (notifyStructureChanged) {
            if (parent.childCount == 1) {
                model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
            } else {
                model.nodesWereInserted(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]), [newIndex] as int[])
            }
        }
    }

    void removeAll() {
        root.removeAllChildren()
    }
}
