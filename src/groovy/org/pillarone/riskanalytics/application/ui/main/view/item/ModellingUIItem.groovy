package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class ModellingUIItem extends AbstractUIItem {
    ModellingItem item
    AbstractTableTreeModel tableTreeModel

    public ModellingUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, ModellingItem item) {
        super(mainModel, simulationModel)
        this.item = item
    }

    public ModellingUIItem(RiskAnalyticsMainModel mainModel, AbstractTableTreeModel tableTreeModel,
                           Model simulationModel, ModellingItem item) {
        this(mainModel, simulationModel, item)
        this.tableTreeModel = tableTreeModel

    }

    @Override
    public boolean isLoaded() {
        return ((ModellingItem) getItem()).isLoaded()
    }

    @Override
    public void load(boolean completeLoad) {
        ((ModellingItem) getItem()).load(completeLoad)
    }

    public boolean isUsedInSimulation() {
        return ((ModellingItem) getItem()).isUsedInSimulation()
    }

    @Override
    String createTitle() {
        return "$item.name v${item.versionNumber.toString()}".toString()
    }

    public boolean deleteDependingResults(Model model) {
        List<SimulationRun> simulationRuns = getSimulations();
        //check if at least one simulation is running
        for (SimulationRun simulationRun: simulationRuns) {
            if (!simulationRun.endTime) return false
        }
        for (SimulationRun simulationRun: simulationRuns) {
            Simulation simulation = ModellingItemFactory.getSimulation(simulationRun.name, item.modelClass)
            SimulationUIItem simulationUIItem = new SimulationUIItem(mainModel, tableTreeModel, model, simulation)
            simulationUIItem.remove()
        }
        return true
    }

    public ModellingItem createNewVersion(Model selectedModel, boolean openNewVersion = true) {
        ModellingItem modellingItem = null
        item.daoClass.withTransaction {status ->
            if (!item.isLoaded())
                item.load()
            modellingItem = ModellingItemFactory.incrementVersion(item)
        }
        mainModel.fireModelChanged()
        AbstractUIItem modellingUIItem = UIItemFactory.createItem(modellingItem, selectedModel, mainModel, tableTreeModel)
        tableTreeModel.addNodeForItem(modellingUIItem)
        if (openNewVersion)
            mainModel.openItem(selectedModel, modellingUIItem)
        return modellingItem
    }

    @Override
    public boolean remove() {
        if (ModellingItemFactory.delete(item)) {
            ModellingUIItem openedItem = mainModel.getAbstractUIItem(item)
            if (openedItem)
                mainModel.closeItem(model, openedItem)
            tableTreeModel.removeNodeForItem(this)
            ModellingItemFactory.remove(item)
            mainModel.fireModelChanged()
            //todo fja refactoring fireRowDeleted
            if (item instanceof Simulation) mainModel.fireRowDeleted(item)
            return true
        }
        return false
    }

    @Override
    void rename(String newName) {
        item.daoClass.withTransaction {status ->
            if (!item.isLoaded())
                item.load()
            ITableTreeNode itemNode = tableTreeModel.findNodeForItem(tableTreeModel.root, this)
            //todo fja
            //            closeItem(item.modelClass.newInstance(), item)

            itemNode.userObject = newName

            tableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(itemNode) as Object[]))
            //todo fja
            //            renameAllChildren(itemNode, name)
            Class modelClass = item.modelClass != null ? item.modelClass : Model
            mainModel.fireModelChanged()
        }
    }

    @Override
    void save() {
        ExceptionSafe.protect {
            item.save()
        }
        //todo fja
//        mainModel.updateViewModelsMap()
        mainModel.fireModelChanged()
        mainModel.fireModelItemChanged()
    }



    public void addItem(ModellingUIItem modellingUIItem, String name) {
        modellingUIItem.item.daoClass.withTransaction {status ->
            if (!modellingUIItem.isLoaded())
                modellingUIItem.load()
            ModellingItem newItem = ModellingItemFactory.copyItem(modellingUIItem.item, name)
            newItem.id = null
            mainModel.fireModelChanged()
            tableTreeModel.addNodeForItem(UIItemFactory.createItem(newItem, modellingUIItem.model, mainModel, tableTreeModel))
        }
    }



    public void importItem() {
        mainModel.fireModelChanged()
        tableTreeModel.addNodeForItem(this)
        item.unload()
    }

    @Override
    void removeAllModellingItemChangeListener() {
        item.removeAllModellingItemChangeListener()
    }

    @Override
    def addModellingItemChangeListener(IModellingItemChangeListener listener) {
        return item.addModellingItemChangeListener(listener)
    }

    @Override
    boolean isChanged() {
        return item.changed
    }

    String getName() {
        return item.name
    }


}
