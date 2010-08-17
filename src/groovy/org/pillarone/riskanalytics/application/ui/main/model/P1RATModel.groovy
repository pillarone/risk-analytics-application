package org.pillarone.riskanalytics.application.ui.main.model

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.tree.ITreeNode
import groovy.beans.Bindable
import org.apache.log4j.Logger
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.AbstractPresentationModel
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTreeModel
import org.pillarone.riskanalytics.application.ui.base.view.IModelItemChangeListener
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchSimulationAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchTableListener
import org.pillarone.riskanalytics.application.ui.parameterization.model.CompareParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.model.CalculationConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.*

class P1RATModel extends AbstractPresentationModel implements ISimulationListener {

    private List modelListeners
    private Map viewModelsInUse
    ModellingInformationTreeModel selectionTreeModel
    def rootPaneForAlerts
    ULCPollingTimer pollingBatchSimulationTimer
    PollingBatchSimulationAction pollingBatchSimulationAction

    @Bindable ModellingItem currentItem

    public static boolean deleteActionIsRunning = false
    static final Logger LOG = Logger.getLogger(P1RATModel)

    public P1RATModel() {
        modelListeners = []
        viewModelsInUse = [:]
        selectionTreeModel = new ModellingInformationTreeModel()
        pollingBatchSimulationAction = new PollingBatchSimulationAction()
        startPollingTimer(pollingBatchSimulationAction)
    }



    public ParameterViewModel getParameterViewModel(Parameterization item, Model simulationModel) {
        if (viewModelsInUse.containsKey(item)) {
            return viewModelsInUse[item]
        }
        ParameterViewModel model = new ParameterViewModel(simulationModel, item, ModelStructure.getStructureForModel(simulationModel.class))
        registerModel(item, model)
        return model
    }

    public ResultConfigurationViewModel getResultConfigurationViewModel(ResultConfiguration item, Model simulationModel) {
        if (viewModelsInUse.containsKey(item)) {
            return viewModelsInUse[item]
        }
        ResultConfigurationViewModel model = new ResultConfigurationViewModel(simulationModel, item, ModelStructure.getStructureForModel(simulationModel.class))
        registerModel(item, model)
        return model
    }

    public ResultViewModel getResultViewModel(Simulation item, Model simulationModel) {
        if (viewModelsInUse.containsKey(item)) {
            return viewModelsInUse[item]
        }

        ResultViewModel model = new ResultViewModel(simulationModel, ModelStructure.getStructureForModel(simulationModel.class), item)
        registerModel(item, model)

        return model
    }

    public CompareSimulationsViewModel getCompareSimulationsViewModel(List simulations, Model simulationModel) {
        CompareSimulationsViewModel model = new CompareSimulationsViewModel(simulationModel, ModelStructure.getStructureForModel(simulationModel.class), simulations)
        return model
    }

    public CompareParameterViewModel getCompareParameterViewModel(List<Parameterization> parameterizations, Model simulationModel) {
        CompareParameterViewModel model = new CompareParameterViewModel(simulationModel, parameterizations, ModelStructure.getStructureForModel(simulationModel.class))
        return model
    }


    private def registerModel(ModellingItem item, def model) {
        viewModelsInUse[item] = model
        if (model instanceof IModelChangedListener) {
            addModelChangedListener(model)
        }
    }

    private def unregisterModel(ModellingItem item) {
        def viewModel = viewModelsInUse.remove(item)
        if (viewModel != null) {
            if (viewModel instanceof SimulationConfigurationModel) {
                viewModel.actionsPaneModel.removeSimulationListener(this)
                removeModelChangedListener(viewModel.settingsPaneModel)
            }
            if (viewModel instanceof IModelChangedListener) {
                removeModelChangedListener(viewModel)
            }
        }

    }

    protected void unloadParameterViewModel(ModellingItem item) {
        def viewModel = viewModelsInUse.remove(item)
        if (viewModel instanceof ParameterViewModel)
            viewModel = null
    }



    public SimulationConfigurationModel getSimulationConfigurationModel(Simulation item, Model simulationModel) {
        if (viewModelsInUse.containsKey(item) && viewModelsInUse[item] instanceof SimulationConfigurationModel) {
            SimulationConfigurationModel model = viewModelsInUse[item]
            model.settingsPaneModel.selectedParameterization = item.parameterization
            model.settingsPaneModel.selectedResultConfiguration = item.template
            return model
        }
        SimulationConfigurationModel model = new SimulationConfigurationModel(simulationModel.class, this)
        model.settingsPaneModel.selectedParameterization = item.parameterization
        model.settingsPaneModel.selectedResultConfiguration = item.template
        model.actionsPaneModel.addSimulationListener(this)
        addModelChangedListener(model.settingsPaneModel)
        registerModel(item, model)

        return model
    }

    public CalculationConfigurationModel getSimulationConfigurationModel(Simulation item, DeterministicModel simulationModel) {
        if (viewModelsInUse.containsKey(item) && viewModelsInUse[item] instanceof CalculationConfigurationModel) {
            return viewModelsInUse[item]
        }
        CalculationConfigurationModel model = new CalculationConfigurationModel(this, simulationModel.class, item?.parameterization, item?.template)
        model.addSimulationListener(this)
        registerModel(item, model)

        return model
    }

    void addModelListener(IP1RATModelListener listener) {
        modelListeners << listener
    }

    void notifyOpenDetailView(Model model, Object item) {
        modelListeners.each {IP1RATModelListener listener ->
            listener.openDetailView(model, item)
        }
    }

    void notifyOpenDetailView(Model model, List items) {
        modelListeners.each {IP1RATModelListener listener ->
            listener.openDetailView(model, items)
        }
    }

    void notifyCloseDetailView(Model model, Object item) {
        modelListeners.each {IP1RATModelListener listener ->
            listener.closeDetailView(model, item)
        }
    }

    void refresh() {
        ExceptionSafe.protect {
            selectionTreeModel.refresh()
        }
    }

    void refresh(ModellingItem item) {
        ExceptionSafe.protect {
            selectionTreeModel.refresh(item)
        }
    }

    void refreshBatchNode() {
        ExceptionSafe.protect {
            selectionTreeModel.refreshBatchNode()
        }
    }


    void save() {
        saveItem(currentItem)
        updateViewModelsMap()
        fireModelItemChanged()
        if (currentItem instanceof Parameterization)
            refresh(currentItem)
    }

    void saveAllOpenItems() {
        viewModelsInUse.keySet().each {
            saveItem(it)
        }
        updateViewModelsMap()
        fireModelItemChanged()
    }

    protected void saveItem(ModellingItem item) {
        ExceptionSafe.protect {
            item.save()
        }
        updateViewModelsMap()
        fireModelChanged()
        fireModelItemChanged()
    }

    private void updateViewModelsMap() {
        if (viewModelsInUse[currentItem] instanceof ParameterViewModel || viewModelsInUse[currentItem] instanceof ResultConfigurationViewModel) {
            currentItem.updateChangeUserAndDate()
            viewModelsInUse[currentItem]?.propertiesViewModel?.setItem(currentItem)
        }
    }

    public void renameItem(Simulation item, String name) {
        ITreeNode itemNode = selectionTreeModel.findNodeForItem(selectionTreeModel.root, item)
        closeItem(item.modelClass.newInstance(), item)
        itemNode.userObject = name
        selectionTreeModel.nodesChanged(itemNode.parent, itemNode.parent.getIndex(itemNode))
    }

    public void renameItem(ModellingItem item, String name) {
        item.daoClass.withTransaction {status ->
            item.load()
            ITreeNode itemNode = selectionTreeModel.findNodeForItem(selectionTreeModel.root, item)
            closeItem(item.modelClass.newInstance(), item)

            itemNode.userObject = name
            selectionTreeModel.nodeChanged(itemNode)

            renameAllChildren(itemNode, name)
            Class modelClass = item.modelClass != null ? item.modelClass : Model
            fireModelChanged()
        }
    }

    private void renameAllChildren(ITreeNode itemNode, String name) {
        itemNode.childCount.times {
            def childNode = itemNode.getChildAt(it)
            closeItem(itemNode.item.modelClass.newInstance(), childNode.item)

            childNode.userObject = name
            selectionTreeModel.nodeChanged(childNode)

            renameAllChildren(childNode, name)
        }
    }

    public void addItem(ModellingItem item, String name) {
        item.daoClass.withTransaction {status ->
            item.load()
            ModellingItem newItem = ModellingItemFactory.copyItem(item, name)
            newItem.id = null
            fireModelChanged()
            selectionTreeModel.addNodeForItem(newItem)
        }
    }

    public void importItem(ModellingItem item, Class modelClass) {
        fireModelChanged()
        selectionTreeModel.addNodeForItem(item)
        item.unload()
    }


    public void removeItem(Model model, ModellingItem item) {
        if (ModellingItemFactory.delete(item)) {
            closeItem(model, item)
            selectionTreeModel.removeNodeForItem(item)
            ModellingItemFactory.remove(item)
            fireModelChanged()
            if (item instanceof Simulation) fireRowDeleted(item)
        }
    }

    public void removeItem(BatchRun batchRun) {
        if (batchRun.batchRunService.deleteBatchRun(batchRun)) {
            selectionTreeModel.removeNodeForItem(batchRun)
            fireModelChanged()
        }
    }


    public void removeItems(Model selectedModel, ItemGroupNode itemGroupNode, List modellingItems) {
        closeItems(selectedModel, modellingItems)
        selectionTreeModel.removeAllGroupNodeChildren(itemGroupNode)
        try {
            for (ModellingItem modellingItem: modellingItems) {
                ModellingItemFactory.remove(modellingItem)
                modellingItem.delete()
            }//for
        } catch (Exception ex) {
            LOG.error "Deleting Item Failed: ${ex}"
        }
        fireModelChanged()
    }



    public void createNewVersion(Model model, ModellingItem item, boolean openNewVersion = true) {
        item.daoClass.withTransaction {status ->
            item.load()
            ModellingItem modellingItem = ModellingItemFactory.incrementVersion(item)
            modellingItem.id = null
            fireModelChanged()
            selectionTreeModel.addNodeForItem(modellingItem)
            if (openNewVersion) {
                notifyOpenDetailView(model, modellingItem)
            }
        }
    }

    public void openItem(Model model, Parameterization item) {
        model = model.class.newInstance()
        model.init()
        item.dao.modelClassName = model.class.name
        synchronized (item) {
            item.daoClass.withTransaction {status ->
                boolean usedInSimulation = item.isUsedInSimulation()
                if (!usedInSimulation) {
                    item.load()
                    notifyOpenDetailView(model, item)
                } else {
                    ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(rootPaneForAlerts), "ItemAlreadyUsed")
                    alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert.value, alert.firstButtonLabel, alert.secondButtonLabel, model, item)}] as IWindowListener)
                    alert.show()
                }
            }
        }
    }

    public void openItem(Model model, ModellingItem item) {
        boolean usedInSimulation = false
        if (item instanceof ResultConfiguration) {
            usedInSimulation = item.isUsedInSimulation()
        }
        if (!usedInSimulation) {
            if (!item.isLoaded()) {
                item.load()
            }
            notifyOpenDetailView(model, item)
        } else {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(rootPaneForAlerts), "ItemAlreadyUsed")
            alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert.value, alert.firstButtonLabel, alert.secondButtonLabel, model, item)}] as IWindowListener)
            alert.show()
        }
    }

    /**
     * @param model
     * @param item can be used for a simulation page or a result view
     */
    public void openItem(Model model, Simulation item) {
        if (!item.isLoaded()) {
            item.load()
        }
        // update parameter, result template and their version selection according correctly, if the item is not a result
        if (item.end == null) {
            viewModelsInUse?.each {k, v ->
                if (v instanceof SimulationConfigurationModel) {
                    if (item.parameterization && k.parameterization.modelClass.name == item.parameterization.modelClass.name) {
                        v.settingsPaneModel.parameterizationNames.selectedItem = item.parameterization.name
                        v.settingsPaneModel.parameterizationVersions.selectedItem = "v" + item.parameterization.versionNumber.toString()
                    }
                    if (item.template && k.parameterization.modelClass.name == item.template.modelClass.name) {
                        v.settingsPaneModel.resultConfigurationNames.selectedItem = item.template.name
                        v.settingsPaneModel.resultConfigurationVersions.selectedItem = "v" + item.template.versionNumber.toString()
                    }
                }
            }
        }
        notifyOpenDetailView(model, item)
    }

    public void addItem(BatchRun batchRun) {
        selectionTreeModel.addNodeForItem(batchRun)
        viewModelsInUse.each {k, v ->
            if (v instanceof AbstractConfigurationModel) {
                ((AbstractConfigurationModel) v).itemsComboBoxModel.addItem batchRun
            }
        }
    }

    public void openItem(Model model, BatchRun batchRun) {
        notifyOpenDetailView(model, batchRun)
    }

    public void compareItems(Model model, List simulations) {
        simulations.each {
            if (!it.item.isLoaded()) {
                it.item.load()
            }
        }

        notifyOpenDetailView(model, simulations)
    }

    public void compareParameterizations(Model model, List parameterizations) {
        parameterizations.each {
            if (!it.item.isLoaded()) {
                it.item.load()
            }
        }

        notifyOpenDetailView(model, parameterizations)
    }

    private void handleEvent(String value, String firstButtonValue, String secondButtonValue, Model model, ModellingItem item) {
        //in case hibernate collections are accessed by the simulation at the same time
        synchronized (item) {
            if (value.equals(firstButtonValue)) {
                ModellingItem modellingItem
                item.daoClass.withTransaction {status ->
                    item.load()
                    modellingItem = ModellingItemFactory.incrementVersion(item)
                    modellingItem.id = null
                    fireModelChanged()
                    selectionTreeModel.addNodeForItem(modellingItem)
                }
                openItem(model, modellingItem)
            } else if (value.equals(secondButtonValue)) {
                item.daoClass.withTransaction {status ->
                    item.load()
                    notifyOpenDetailView(model, item)
                }
            }
        }
    }

    public void closeItem(Model model, ModellingItem item) {
        if (item != null) {
            notifyCloseDetailView(model, item)
            unregisterModel(item)
            item.removeAllModellingItemChangeListener()
        }
        //no auto-saved for item
        /*if (item.changed) {
            saveItem(item)
        }*/
    }

    public void closeItem(def model, BatchRun batchRun) {
    }


    private void closeItems(Model selectedModel, List modellingItems) {
        for (ModellingItem modellingItem: modellingItems) {
            closeItem(selectedModel, modellingItem)
        }
    }


    public void simulationStart(Simulation simulation) {
        closeItem(simulation.modelClass.newInstance(), simulation.parameterization)
        closeItem(simulation.modelClass.newInstance(), simulation.template)
    }

    public void simulationEnd(Simulation simulation, Model model) {
        if (simulation.simulationRun?.endTime != null) {
            selectionTreeModel.addNodeForItem(simulation)
            ParameterViewModel viewModel = viewModelsInUse[simulation.parameterization]
            ResultConfigurationViewModel templateViewModel = viewModelsInUse[simulation.template]
            viewModel?.readOnly = true
            templateViewModel?.readOnly = true
        }
    }

    private void startPollingTimer(PollingBatchSimulationAction pollingBatchSimulationAction) {
        try {
            pollingBatchSimulationAction.addSimulationListener this
            pollingBatchSimulationTimer = new ULCPollingTimer(2000, pollingBatchSimulationAction)
            pollingBatchSimulationTimer.repeats = true
            pollingBatchSimulationTimer.start()
        } catch (NullPointerException ex) {}
    }

    List modelItemlisteners = []

    public void addModelItemChangedListener(IModelItemChangeListener listener) {
        modelItemlisteners << listener
    }

    public void removeModelItemChangedListener(IModelItemChangeListener listener) {
        modelItemlisteners.remove(listener)
    }

    public void fireModelItemChanged() {
        modelItemlisteners.each {IModelItemChangeListener listener -> listener.modelItemChanged()}
    }

    List batchTableListeners = []

    public void addBatchTableListener(BatchTableListener batchTableListener) {
        batchTableListeners << batchTableListener
    }

    public void fireRowAdded() {
        batchTableListeners.each {BatchTableListener batchTableListener -> batchTableListener.fireRowAdded()}
    }

    public void fireRowDeleted(Object item) {
        batchTableListeners.each {BatchTableListener batchTableListener ->
            batchTableListener.fireRowDeleted(item.getSimulationRun())
        }
    }

}




interface IP1RATModelListener {

    void openDetailView(Model model, Object item)

    void closeDetailView(Model model, Object item)

}



