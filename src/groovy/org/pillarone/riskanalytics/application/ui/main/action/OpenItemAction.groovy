package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.OpenItemDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationUIItem
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResourceUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class OpenItemAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(OpenItemAction)

    def OpenItemAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("Open", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        AbstractUIItem item = getSelectedUIItem()
        if (item != null) {
            openItem(item)
        }
    }

    private void openItem(ParameterizationUIItem parameterizationUIItem) {
        Model selectedModel = getSelectedModel()
        if (selectedModel != null) {
            parameterizationUIItem.load()
            selectedModel = selectedModel.class.newInstance()
            selectedModel.init()
            ModellingItem item = parameterizationUIItem.item
            item.dao.modelClassName = selectedModel.class.name
            synchronized (parameterizationUIItem) {
                item.daoClass.withTransaction {status ->
                    boolean usedInSimulation = parameterizationUIItem.isUsedInSimulation()
                    if (!usedInSimulation || !parameterizationUIItem.newVersionAllowed()) {
                        this.model.openItem(selectedModel, parameterizationUIItem)
                    } else {
                        showOpenItemDialog(selectedModel, parameterizationUIItem)
                    }
                }
            }
        }
    }

    private void openItem(ResourceUIItem item) {
        boolean usedInSimulation = item.isUsedInSimulation()
        if (!usedInSimulation) {
            item.load()
            this.model.openItem(null, item)
        } else {
            showOpenItemDialog(null, item)
        }
    }

    private void openItem(AbstractUIItem item) {
        Model selectedModel = getSelectedModel()
        if (selectedModel != null) {
            loadIfNotInUse(item)
            boolean usedInSimulation = false
            if (item instanceof ModellingUIItem) {
                usedInSimulation = item.isUsedInSimulation()
            }
            if (!usedInSimulation) {
                this.model.openItem(selectedModel, item)
            } else {
                showOpenItemDialog(selectedModel, item)
            }
        }
    }

    // Do not load item in case it is already open. Otherwise the persistent state of this item gets loaded again. PMO-2383
    private void loadIfNotInUse(AbstractUIItem item) {
        if (!item.mainModel.isItemOpen(item)){
            item.load()
        }
    }

    private void openItem(Model selectedModel, SimulationUIItem item) {
        this.model.openItem(selectedModel, item)
    }

    private void showOpenItemDialog(Model selectedModel, def item) {
        OpenItemDialog openItemDialog = new OpenItemDialog(tree, selectedModel, model, item)
        openItemDialog.init()
        openItemDialog.setVisible(true)
    }

}
