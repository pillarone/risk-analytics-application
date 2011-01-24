package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.OpenItemDialog
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class OpenItemAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(OpenItemAction)

    def OpenItemAction(ULCTree tree, P1RATModel model) {
        super("Open", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        Model selectedModel = getSelectedModel()
        def item = getSelectedItem()
        if (selectedModel != null && item != null) {
            if (!item.isLoaded()) {
                item.load()
            }
            if (item instanceof Simulation) {
                LOG.trace "Reading end time from simulation: ${System.identityHashCode(item)}: ${item.end?.time}"
            }
            openItem(selectedModel, item)
        }
    }

    private void openItem(Model selectedModel, Parameterization item) {
        selectedModel = selectedModel.class.newInstance()
        selectedModel.init()
        item.dao.modelClassName = selectedModel.class.name
        synchronized (item) {
            item.daoClass.withTransaction {status ->
                boolean usedInSimulation = item.isUsedInSimulation()
                if (!usedInSimulation) {
                    this.model.openItem(selectedModel, item)
                } else {
                    showOpenItemDialog(selectedModel, item)
                }
            }
        }
    }

    private void openItem(Model selectedModel, ModellingItem item) {
        boolean usedInSimulation = false
        if (item instanceof ResultConfiguration) {
            usedInSimulation = item.isUsedInSimulation()
        }
        if (!usedInSimulation) {
            this.model.openItem(selectedModel, item)
        } else {
            showOpenItemDialog(selectedModel, item)
        }
    }

    private void openItem(Model selectedModel, Simulation item) {
        this.model.openItem(selectedModel, item)
    }

    private void showOpenItemDialog(Model selectedModel, def item) {
        OpenItemDialog openItemDialog = new OpenItemDialog(tree, selectedModel, model, item)
        openItemDialog.init()
        openItemDialog.setVisible(true)
    }

}

