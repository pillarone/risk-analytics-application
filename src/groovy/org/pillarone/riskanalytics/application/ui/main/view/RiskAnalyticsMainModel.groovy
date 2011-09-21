package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel

import groovy.beans.Bindable
import org.apache.log4j.Logger
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.AbstractPresentationModel
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.view.IModelItemChangeListener
import org.pillarone.riskanalytics.application.ui.batch.action.PollingBatchSimulationAction
import org.pillarone.riskanalytics.application.ui.batch.model.BatchTableListener

import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationConfigurationModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.application.ui.main.view.item.*
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.base.model.MultiFilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.simulation.model.INewSimulationListener
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class RiskAnalyticsMainModel extends AbstractPresentationModel implements ISimulationListener {

    Map<AbstractUIItem, Object> viewModelsInUse
    AbstractTableTreeModel navigationTableTreeModel
    def switchActions = []
    private List<IRiskAnalyticsModelListener> modelListeners = []
    private List<BatchTableListener> batchTableListeners = []
    private List<IModelItemChangeListener> modelItemlisteners = []
    private List<INewSimulationListener> newSimulationListeners = []
    //selectedItem needs to be updated when tabs are changed etc.
    @Bindable AbstractUIItem currentItem

    public static boolean deleteActionIsRunning = false
    static final Logger LOG = Logger.getLogger(RiskAnalyticsMainModel)

    public RiskAnalyticsMainModel() {
        viewModelsInUse = [:]
        ModellingInformationTableTreeModel modellingInformationTableTreeModel = ModellingInformationTableTreeModel.getInstance(this)
        modellingInformationTableTreeModel.buildTreeNodes()
        navigationTableTreeModel = new MultiFilteringTableTreeModel(modellingInformationTableTreeModel)
    }

    public RiskAnalyticsMainModel(AbstractTableTreeModel navigationTableTreeModel) {
        viewModelsInUse = [:]
        this.navigationTableTreeModel = navigationTableTreeModel
    }

    void saveAllOpenItems() {
        viewModelsInUse.keySet().each {AbstractUIItem item ->
            item.save()
        }
    }

    public void renameItem(AbstractUIItem item, String name) {
        item.rename(name)
    }


    public void removeItems(Model selectedModel, ItemGroupNode itemGroupNode, List<AbstractUIItem> modellingItems) {
        closeItems(selectedModel, modellingItems)
//        selectionTreeModel.removeAllGroupNodeChildren(itemGroupNode)
        try {
            for (AbstractUIItem item: modellingItems) {
                item.remove()
                item.delete()
            }//for
        } catch (Exception ex) {
            LOG.error "Deleting Item Failed: ${ex}"
        }
        fireModelChanged()
    }

    public AbstractModellingModel getViewModel(AbstractUIItem item) {
        if (viewModelsInUse.containsKey(item)) {
            return viewModelsInUse[item]
        }
        return item.getViewModel()
    }


    public void openItem(Model model, AbstractUIItem item) {
        if (!item.isLoaded())
            item.load()
        notifyOpenDetailView(model, item)
    }

    public void closeItem(Model model, AbstractUIItem abstractUIItem) {
        notifyCloseDetailView(model, abstractUIItem)
        viewModelsInUse.remove(abstractUIItem)
        unregisterModel(abstractUIItem)
        abstractUIItem.removeAllModellingItemChangeListener()
    }



    private void closeItems(Model selectedModel, List<AbstractUIItem> items) {
        for (AbstractUIItem item: items) {
            closeItem(selectedModel, item)
        }
    }


    public void addModelItemChangedListener(IModelItemChangeListener listener) {
        modelItemlisteners << listener
    }

    public void removeModelItemChangedListener(IModelItemChangeListener listener) {
        modelItemlisteners.remove(listener)
    }

    public void fireModelItemChanged() {
        modelItemlisteners.each {IModelItemChangeListener listener -> listener.modelItemChanged()}
    }

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


    public void registerModel(AbstractUIItem item, def model) {
        viewModelsInUse[item] = model
        if (model instanceof IModelChangedListener) {
            addModelChangedListener(model)
        }
    }

    private def unregisterModel(AbstractUIItem item) {
        def viewModel = viewModelsInUse.remove(item)
        if (viewModel != null) {
            if (viewModel instanceof SimulationConfigurationModel) {
                viewModel.actionsPaneModel.removeSimulationListener(this)
                removeModelChangedListener(viewModel.settingsPaneModel)
                removeNewSimulationListener(viewModel)
            }
            if (viewModel instanceof IModelChangedListener) {
                removeModelChangedListener(viewModel)
            }
        }

    }

    void addModelListener(IRiskAnalyticsModelListener listener) {
        if (!modelListeners.contains(listener))
            modelListeners << listener
    }

    void notifyOpenDetailView(Model model, Object item) {
        modelListeners.each {IRiskAnalyticsModelListener listener ->
            listener.openDetailView(model, item)
        }
    }


    void notifyCloseDetailView(Model model, AbstractUIItem item) {
        modelListeners.each {IRiskAnalyticsModelListener listener ->
            listener.closeDetailView(model, item)
        }
    }

    void notifyChangedDetailView(Model model, AbstractUIItem item) {
        setCurrentItem(item)
        modelListeners.each {IRiskAnalyticsModelListener listener ->
            listener.changedDetailView(model, item)
        }
    }

    void notifyChangedWindowTitle(AbstractUIItem abstractUIItem) {

        modelListeners.each {IRiskAnalyticsModelListener listener ->
            listener.setWindowTitle(abstractUIItem)
        }
    }

    public void addNewSimulationListener(INewSimulationListener newSimulationListener) {
        newSimulationListeners << newSimulationListener
    }

    public void removeNewSimulationListener(INewSimulationListener newSimulationListener) {
        newSimulationListeners.remove(newSimulationListener)
    }

    public void fireNewSimulation(Simulation simulation) {
        newSimulationListeners.each {INewSimulationListener newSimulationListener ->
            newSimulationListener.newSimulation(simulation)
        }
    }

    public void setCurrentItem(def currentItem) {
        this.currentItem = (currentItem instanceof BatchUIItem) ? null : currentItem
        switchActions.each {
            boolean b = (this.currentItem instanceof ParameterizationUIItem) || (this.currentItem instanceof ResultUIItem)
            it.setEnabled(b)
            it.selected = b
        }
        notifyChangedWindowTitle(currentItem)
    }

    ModellingUIItem getAbstractUIItem(ModellingItem modellingItem) {
        ModellingUIItem item = null
        viewModelsInUse.keySet().findAll {it instanceof ModellingUIItem}.each {ModellingUIItem openedUIItem ->
            if (modellingItem.class == openedUIItem.item.class && modellingItem.id == openedUIItem.item.id) {
                item = openedUIItem
            }
        }
        return item
    }

    public void startPollingTimer(PollingBatchSimulationAction pollingBatchSimulationAction) {
        if (pollingBatchSimulationAction == null)
            pollingBatchSimulationAction = new PollingBatchSimulationAction()
        try {
            pollingBatchSimulationAction.addSimulationListener this
            ULCPollingTimer pollingBatchSimulationTimer = new ULCPollingTimer(2000, pollingBatchSimulationAction)
            pollingBatchSimulationTimer.repeats = true
            pollingBatchSimulationTimer.syncClientState = false
            pollingBatchSimulationTimer.start()
        } catch (NullPointerException ex) {}
    }


    public void simulationStart(Simulation simulation) {
    }

    public void simulationEnd(Simulation simulation, Model model) {
        if (simulation.simulationRun?.endTime != null) {
            navigationTableTreeModel.addNodeForItem(simulation)
            Parameterization parameterization = simulation.parameterization
            //after simulation running, lock the used the used p14n
            parameterization.addRemoveLockTag()
            navigationTableTreeModel.itemChanged(parameterization)
        }
    }

}
