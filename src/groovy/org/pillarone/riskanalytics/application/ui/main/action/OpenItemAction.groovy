package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.OpenItemDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.*
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class OpenItemAction extends SelectionTreeAction {

    private static final Log LOG = LogFactory.getLog(OpenItemAction)

    OpenItemAction(ULCTableTree tree) {
        super("Open", tree)
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
            selectedModel = selectedModel.class.newInstance()
            selectedModel.init()
            ModellingItem item = parameterizationUIItem.item
            item.dao.modelClassName = selectedModel.class.name
            synchronized (parameterizationUIItem) {
                item.daoClass.withTransaction { status ->
                    boolean usedInSimulation = parameterizationUIItem.isUsedInSimulation()
                    if (!usedInSimulation || !parameterizationUIItem.newVersionAllowed()) {
                        LOG.info("Opening parameterization ${parameterizationUIItem.nameAndVersion}")
                        riskAnalyticsEventBus.post(new OpenDetailViewEvent(parameterizationUIItem))
                    } else {
                        LOG.info("Parameterization ${parameterizationUIItem.nameAndVersion} cannot be edited.")
                        showOpenItemDialog(selectedModel, parameterizationUIItem)
                    }
                }
            }
        }
    }

    private void openItem(ResourceUIItem item) {
        boolean usedInSimulation = item.usedInSimulation
        if (usedInSimulation) {
            showOpenItemDialog(null, item)
        } else {
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(item))
        }
    }

    private void openItem(BatchUIItem item) {
        riskAnalyticsEventBus.post(new OpenDetailViewEvent(item))
    }

    private void openItem(AbstractUIItem item) {
        Model selectedModel = getSelectedModel()
        if (selectedModel != null) {
            boolean usedInSimulation = false
            if (item instanceof ModellingUIItem) {
                usedInSimulation = item.isUsedInSimulation()
            }
            if (usedInSimulation) {
                showOpenItemDialog(selectedModel, item)
            } else {
                riskAnalyticsEventBus.post(new OpenDetailViewEvent(item))
            }
        }
    }

    private void showOpenItemDialog(Model selectedModel, ModellingUIItem item) {
        OpenItemDialog openItemDialog = new OpenItemDialog(tree, selectedModel, riskAnalyticsEventBus, item)
        openItemDialog.init()
        openItemDialog.visible = true
    }

}
