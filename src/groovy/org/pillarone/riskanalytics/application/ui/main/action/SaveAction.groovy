package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemUtils
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.workflow.StatusChangeService

class SaveAction extends ResourceBasedAction {

    private static final Log LOG = LogFactory.getLog(SaveAction)

    RiskAnalyticsMainModel model
    ModellingUIItem currentItem
    ULCComponent parent

    public SaveAction(ULCComponent parent, RiskAnalyticsMainModel model) {
        super("Save")
        this.model = model
        this.parent = parent
    }

    public SaveAction(ULCComponent parent, RiskAnalyticsMainModel model, ModellingItem currentItem) {
        this(parent, model)
        this.currentItem = UIItemFactory.createItem(currentItem, currentItem.modelClass?.newInstance() as Model)
    }


    public void doActionPerformed(ActionEvent event) {
        save(currentItem ?: model.currentItem)
    }

    void save(AbstractUIItem abstractUIItem) {
        if (abstractUIItem instanceof ModellingUIItem) {
            final ModellingUIItem modellingUIItem = abstractUIItem
            if (isChangedAndNotUsed(modellingUIItem)) {
                LOG.info("Saving ${abstractUIItem.nameAndVersion}.")
                saveItem(abstractUIItem)
            } else {
                LOG.info("Cannot save ${abstractUIItem.nameAndVersion} because it already used in a simulation.")
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(parent), "SaveItemAlreadyUsed")
                alert.addWindowListener([windowClosing: { WindowEvent e -> handleEvent(alert, modellingUIItem) }] as IWindowListener)
                alert.show()
            }
        }
    }


    void saveItem(ModellingUIItem modellingUIItem) {
        modellingUIItem.save()
    }

    /**
     * save a changed item by creating  a new version or deleting all dependent simulations
     * @param alert
     * @param item
     */
    private void handleEvent(I18NAlert alert, ModellingUIItem modellingUIItem) {

        synchronized (modellingUIItem) {
            if (alert.value.equals(alert.firstButtonLabel)) {
                LOG.info("Saving changes of ${modellingUIItem.nameAndVersion} into a new version.")
                handleNewVersion(getItemModel(modellingUIItem), modellingUIItem) //PMO-2054
            } else if (alert.value.equals(alert.secondButtonLabel)) {
                boolean deleted = modellingUIItem.deleteDependingResults()
                if (deleted) {
                    LOG.info("Deleting depending results of ${modellingUIItem.nameAndVersion} before saving.")
                    saveItem(modellingUIItem)
                } else {
                    String errorKey = UIItemUtils.isUsedInRunningSimulation(modellingUIItem.item) ? "DeleteAllDependentRunningsError" : "DeleteAllDependentRunsError"
                    new I18NAlert(UlcUtilities.getWindowAncestor(parent), errorKey).show()
                }
            } else {
                LOG.info("Save aborted.")
            }
        }
    }

    private void handleNewVersion(Model model, ModellingUIItem item) {
        item.createNewVersion(model, false)
        this.model.notifyCloseDetailView(model, item)
    }

    private void handleNewVersion(Model model, ParameterizationUIItem item) {
        Closure okAction = { String commentText ->
            if (!item.isLoaded()) {
                item.load()
            }
            createNewVersion(item, model, commentText)
            this.model.notifyCloseDetailView(model, item)
        }

        NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(okAction)
        versionCommentDialog.show()
    }

    private void createNewVersion(ParameterizationUIItem item, Model model, String commentText) {
        Parameterization originalParameterization = item.item as Parameterization
        if (originalParameterization.status != Status.NONE) {
            Parameterization parameterization = StatusChangeService.service.changeStatus(originalParameterization, Status.DATA_ENTRY)
            parameterization.save()
        } else {
            item.createNewVersion(model, commentText, false)
        }
    }


    private Model getItemModel(AbstractUIItem modellingUIItem) {
        Model itemModel = modellingUIItem.item.modelClass.newInstance()
        itemModel.init()
        return itemModel
    }

    /**
     * there is a possibility to a comment to used p14n
     * by saving we have to make a difference if the item is changed and not used
     * in a simulation or only its comments have been changed
     * @param item
     * @return
     */
    private boolean isChangedAndNotUsed(ModellingUIItem modellingUIItem) {
        return !modellingUIItem.item.changed || (modellingUIItem.item.changed && !modellingUIItem.usedInSimulation)
    }

    @Override
    boolean isEnabled() {
        if (model.currentItem) {
            return itemChanged(model.currentItem)
        }
        return false
    }

    boolean itemChanged(ModellingUIItem item) {
        return item.item.changed
    }

    boolean itemChanged(ParameterizationUIItem parameterizationUIItem) {
        return parameterizationUIItem.item.changed || parameterizationUIItem.item.commentHasChanged()
    }

    boolean itemChanged(Object item) {
        return false
    }

    @Override
    String toString() {
        "currentItem: $currentItem"
    }
}
