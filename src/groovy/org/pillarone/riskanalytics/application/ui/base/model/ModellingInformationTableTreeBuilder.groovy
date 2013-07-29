package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
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

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingInformationTableTreeBuilder {

    DefaultMutableTableTreeNode root
    AbstractTableTreeModel model
    RiskAnalyticsMainModel mainModel
    static Log LOG = LogFactory.getLog(ModellingInformationTableTreeBuilder)
    @Lazy
    private static List<Class> resources = {
        ClassPathScanner provider = new ClassPathScanner()
        provider.addIncludeFilter(new AssignableTypeFilter(IResource))
        List<String> acceptedResources = ConfigurationHolder.config.includedResources

        List<Class> classes = provider.findCandidateComponents("")*.beanClassName.collect { getClass().getClassLoader().loadClass(it) }
        return classes.findAll { acceptedResources.contains(it.simpleName) }
    }()

    static final int PARAMETERIZATION_NODE_INDEX = 0
    static final int RESULT_CONFIGURATION_NODE_INDEX = 1
    static final int SIMULATION_NODE_INDEX = 2

    private boolean resourceNodeVisible = false

    public ModellingInformationTableTreeBuilder(AbstractTableTreeModel model) {
        this(model, null)
    }

    public ModellingInformationTableTreeBuilder(AbstractTableTreeModel model, RiskAnalyticsMainModel mainModel) {
        this.model = model;
        this.mainModel = mainModel
        root = new DefaultMutableTableTreeNode("root")
    }

    public def buildTreeNodes(List<ModellingItem> modellingItems) {
        buildResourcesNodes()
        buildBatchNodes()
        buildModelNodes(modellingItems)

    }

    public List<ModellingItem> getModellingItems() {
        List<ModellingItem> result = []
        internalGetModellingItems(root, result)
        return result
    }

    protected void internalGetModellingItems(ITableTreeNode currentNode, List<ModellingItem> list) {
        if(currentNode instanceof ItemNode) {
            Object item = currentNode.abstractUIItem.item
            if ((item instanceof ParametrizedItem) || (item instanceof ResultConfiguration)) {
                list << item
            }
        }

        for(int i = 0; i < currentNode.childCount; i++) {
            internalGetModellingItems(currentNode.getChildAt(i), list)
        }
    }

    void buildModelNodes(List<ModellingItem> items) {
        getAllModelClasses().each { Class<Model> modelClass ->
            List<ModellingItem> itemsForModel = items.findAll { it.modelClass == modelClass }
            Map groupedItems = itemsForModel.groupBy { it.class.name }
            Model model = modelClass.newInstance()
            model.init()
            ITableTreeNode modelNode = getModelNode(model)
            DefaultMutableTableTreeNode parametrisationsNode = modelNode.getChildAt(PARAMETERIZATION_NODE_INDEX)
            DefaultMutableTableTreeNode resultConfigurationsNode = modelNode.getChildAt(RESULT_CONFIGURATION_NODE_INDEX)
            DefaultMutableTableTreeNode simulationsNode = modelNode.getChildAt(SIMULATION_NODE_INDEX)
            addToNode(parametrisationsNode, groupedItems[Parameterization.name])
            addToNode(resultConfigurationsNode, groupedItems[ResultConfiguration.name])
            addSimulationsToNode(modelClass, simulationsNode)
            root.insert(modelNode, root.childCount - (resourceNodeVisible ? 2 : 1))
        }

    }

    private void addSimulationsToNode(Class<Model> modelClass, DefaultMutableTableTreeNode simulationsNode) {
        List simulationsForModel = getItemsForModel(modelClass, Simulation)
        if (simulationsForModel.size() == 0) {
            simulationsNode.leaf = true
        }
        simulationsForModel.each {
            try {
                simulationsNode.add(createNode(it))
            } catch (Throwable t) {
                LOG.error "Could not create node for ${toString()}", t
            }
        }
    }

    private addToNode(DefaultMutableTableTreeNode node, List items) {
        if (items) {
            getItemMap(items, false).values().each {
                node.add(createItemNodes(it))
            }
            getItemMap(items, true).values().each {
                node.add(createItemNodes(it))
            }
        }
    }

    public void buildTreeNodes() {
        buildResourcesNodes()
        buildBatchNodes()
        getAllModelClasses().each { Class modelClass ->
            createModelNode(modelClass.newInstance())
        }
    }

    private void buildBatchNodes() {
        root.add(createBatchNode())
    }

    private void buildResourcesNodes() {
        def resourceClasses = getAllResourceClasses()
        if (!resourceClasses.isEmpty()) {
            ResourceGroupNode resourcesNode = new ResourceGroupNode("Resources")
            resourceClasses.each { Class resourceClass ->
                ResourceClassNode resourceNode = new ResourceClassNode(resourceClass.simpleName, resourceClass, mainModel)
                getItemMap(getItemsForModel(resourceClass, Resource), false).values().each {
                    resourceNode.add(createItemNodes(it))
                }
                resourcesNode.add(resourceNode)
            }
            root.add(resourcesNode)
            resourceNodeVisible = true
        }
    }

    private void createModelNode(Model model) {
        Class modelClass = model.class

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

        addSimulationsToNode(modelClass, simulationsNode)
        root.insert(modelNode, root.childCount - (resourceNodeVisible ? 2 : 1))
    }

    public List getItemsForModel(Class modelClass, Class clazz) {
        switch (clazz) {
            case Resource: return ModellingItemFactory.getResources(modelClass)
            case Parameterization: return ModellingItemFactory.getParameterizationsForModel(modelClass)
            case ResultConfiguration: return ModellingItemFactory.getResultConfigurationsForModel(modelClass)
            case Simulation: return ModellingItemFactory.getActiveSimulationsForModel(modelClass)
            default: return []
        }
    }

    public List getAllModelClasses() {
        return ModelRegistry.instance.allModelClasses.toList()
    }

    public List<Class> getAllResourceClasses() {

        if (!(ConfigurationHolder.config?.includedResources instanceof List)) {
            LOG.info("Please note that there are no resource classes defined in the config.groovy file")
            return []
        }
        return resources
    }

    private ITableTreeNode getModelNode(Model model) {
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
        Map map = [:]
        items = items.findAll { workflow ? it.versionNumber.toString().startsWith("R") : !it.versionNumber.toString().startsWith("R") }
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
        tree.sort { a, b -> b.versionNumber <=> a.versionNumber }

        def root = createNode(tree.first())
        tree.remove(tree.first())
        root.leaf = tree.empty

        def secondLevelNodes = tree.findAll { it.versionNumber.level == 1 }
        secondLevelNodes.each {
            def node = createNode(it)
            createSubNodes(tree, node)
            root.add(node)
        }

        root
    }

    private void createSubNodes(def tree, ItemNode node) {
        def currentLevelNodes = tree.findAll { ModellingItem it ->
            it.versionNumber.isDirectChildVersionOf(node.versionNumber)
        }
        node.leaf = currentLevelNodes.size() == 0
        currentLevelNodes.each {
            def newNode = createNode(it)
            node.add(newNode)
            createSubNodes(tree, newNode)
        }
    }

    public void order(def comparator) {
        root.childCount.times { childIndex ->
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

    public ITableTreeNode refresh(AbstractUIItem item) {
        def node = findNodeForItem(findModelNode(root, item), item)
        model?.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        return node
    }

    public void addNodeForItem(Model model) {
        createModelNode(model)
        this.model.nodesWereInserted(new TreePath(root), [root.childCount - 2] as int[])
    }

    public void addNodeForItem(Simulation item) {
        if (item.end) {
            DefaultMutableTableTreeNode groupNode = findGroupNode(item, findModelNode(root, item))
            groupNode.leaf = false
            insertNodeInto(createNode(item), groupNode)
        }
    }

    public def addNodeForItem(ModellingUIItem modellingUIItem) {
        ITableTreeNode groupNode = findGroupNode(modellingUIItem, findModelNode(root, modellingUIItem))
        createAndInsertItemNode(groupNode, modellingUIItem)
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(groupNode) as Object[]))
        return groupNode
    }

    public def addNodeForItem(ResourceUIItem modellingUIItem) {
        ITableTreeNode itemGroupNode = findResourceItemGroupNode(findResourceGroupNode(root), modellingUIItem.item.modelClass)

        createAndInsertItemNode(itemGroupNode, modellingUIItem)
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(itemGroupNode) as Object[]))
        return itemGroupNode
    }

    public def addNodeForItem(ModellingItem modellingItem) {
        ModellingUIItem modellingUIItem = UIItemFactory.createItem(modellingItem, null, mainModel)
        return addNodeForItem(modellingUIItem)
    }

    public void addNodeForItem(BatchUIItem batchRun) {
        ITableTreeNode groupNode = findBatchRootNode(root)
        insertNodeInto(createNode(batchRun), groupNode)
    }

    public void itemChanged(ModellingItem item) {
        ITableTreeNode itemGroupNode = findGroupNode(item, findModelNode(root, item))
        itemNodeChanged(itemGroupNode, item)
    }

    private void itemNodeChanged(ITableTreeNode itemGroupNode, ModellingItem item) {
        ItemNode itemNode = findNodeForItem(itemGroupNode, item)
        itemNode.abstractUIItem.item = item
        model.putValues(itemNode)
        model?.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(itemNode) as Object[]))
    }

    private void itemNodeChanged(ITableTreeNode itemGroupNode, Simulation item) {
        ItemNode itemNode = findNodeForItem(itemGroupNode, item)
        if (!itemNode) {
            addNodeForItem(item)
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
        ITableTreeNode groupNode = findGroupNode(modellingUIItem, findModelNode(root, modellingUIItem))
        def itemNode = findNodeForItem(groupNode, modellingUIItem)
        if (!itemNode) return

        removeItemNode(itemNode)
    }

    public void removeNodeForItem(ModellingItem modellingItem) {
        removeNodeForItem(UIItemFactory.createItem(modellingItem, null, mainModel))
    }


    public void removeNodeForItem(ResourceUIItem modellingUIItem) {
        ITableTreeNode itemGroupNode = findResourceItemGroupNode(findResourceGroupNode(root), modellingUIItem.item.modelClass)
        ITableTreeNode itemNode = findNodeForItem(itemGroupNode, modellingUIItem)
        if (!itemNode) return

        removeItemNode(itemNode)
    }

    private void removeItemNode(DefaultMutableTableTreeNode itemNode) {
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

    public void removeNodeForItem(BatchUIItem batchUIItem) {
        ITableTreeNode groupNode = findBatchRootNode(root)
        ITableTreeNode itemNode = findNodeForItem(groupNode, batchUIItem)
        removeNodeFromParent(itemNode)
    }

    public void refreshBatchNode() {
        ITableTreeNode batchNode = findBatchRootNode(root)

        removeNodeFromParent(batchNode)
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
        root.add(createBatchNode())
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(root) as Object[]))
    }

    protected void createAndInsertItemNode(DefaultMutableTableTreeNode node, ModellingUIItem modellingUIItem) {
        boolean parameterNameFound = false
        for (int i = 0; i < node.childCount; i++) {
            if (isMatchingParent(node.getChildAt(i).abstractUIItem, modellingUIItem)) {
                parameterNameFound = true
                if (modellingUIItem.isVersionable() && modellingUIItem.item.versionNumber.level > 1) {
                    insertSubversionItemNode(node.getChildAt(i), createNode(modellingUIItem))
                } else {
                    DefaultMutableTableTreeNode childNode = node.getChildAt(i)
                    DefaultMutableTableTreeNode newNode = createNode(modellingUIItem)
                    def children = []
                    childNode.childCount.times {
                        children << childNode.getChildAt(it)
                    }
                    children.each { newNode.add(it) }
                    childNode.removeAllChildren()
                    childNode.leaf = true
                    if (childNode.abstractUIItem.isVersionable() && childNode.abstractUIItem.item.versionNumber.level == 1) {
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
            def newNode = createNode(modellingUIItem)
            newNode.leaf = true
            node.leaf = false
            insertNodeInto(newNode, node)
        }
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

    protected void createAndInsertItemNode(DefaultMutableTableTreeNode node, BatchUIItem batchUIItem) {
        DefaultMutableTableTreeNode newNode = createNode(batchUIItem)
        node.add(newNode)
    }


    private void insertSubversionItemNode(DefaultMutableTableTreeNode node, DefaultMutableTableTreeNode newItemNode) {
        node.childCount.times {
            DefaultMutableTableTreeNode childNode = node.getChildAt(it)
            if (newItemNode.abstractUIItem.isVersionable() && newItemNode.abstractUIItem.item.versionNumber.toString().startsWith(childNode.abstractUIItem.item.versionNumber.toString())) {
                if (newItemNode.abstractUIItem.item.versionNumber.isDirectChildVersionOf(childNode.abstractUIItem.item.versionNumber)) {
                    childNode.leaf = false
                    newItemNode.leaf = true
                    childNode.insert(newItemNode, childNode.childCount)
                } else {
                    insertSubversionItemNode(childNode, newItemNode)
                }
            }
        }
    }

    private DefaultMutableTableTreeNode createNode(String name) {
        new DefaultMutableTableTreeNode(name)
    }

    private DefaultMutableTableTreeNode createNode(Parameterization item) {
        Model selectedModelInstance = getModelInstance(item)
        return createNode(new ParameterizationUIItem(mainModel, selectedModelInstance, item))
    }

    private DefaultMutableTableTreeNode createNode(ParameterizationUIItem parameterizationUIItem) {
        ParameterizationNode node = parameterizationUIItem.item.status == Status.NONE ? new ParameterizationNode(parameterizationUIItem) : new WorkflowParameterizationNode(parameterizationUIItem)
        model.putValues(node)
        return node
    }

    private DefaultMutableTableTreeNode createNode(ResultConfiguration item) {
        Model selectedModelInstance = getModelInstance(item)
        return createNode(new ResultConfigurationUIItem(mainModel, selectedModelInstance, item))
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
        Model selectedModelInstance = getModelInstance(item)
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
            println "create simulation exception : ${ex}"
        }
        return node
    }

    protected DefaultMutableTableTreeNode createBatchNode() {
        BatchRootNode batchesNode = new BatchRootNode("Batches", mainModel)
        List<BatchRun> batchRuns = getAllBatchRuns()
        batchRuns?.each { BatchRun batchRun ->
            batchesNode.add(createNode(batchRun))
        }
        return batchesNode
    }

    protected List<BatchRun> getAllBatchRuns() {
        BatchRunner.getService().getAllBatchRuns()
    }

    private void insertNodeInto(DefaultMutableTableTreeNode newNode, DefaultMutableTableTreeNode parent) {
        parent.insert(newNode, parent.childCount)
        if (parent.childCount == 1) {
            model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
            model.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
        } else {
            model.nodesWereInserted(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]), [parent.childCount - 1] as int[])
        }
    }

    protected void removeNodeFromParent(DefaultMutableTableTreeNode itemNode) {
        DefaultMutableTableTreeNode parent = itemNode.getParent()
        parent.remove(parent.getIndex(itemNode))
        model.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
        model.nodeStructureChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(parent) as Object[]))
    }

    Model getModelInstance(ModellingItem item) {
        return null
    }

    void removeAll() {
        root.removeAllChildren()
    }
}
