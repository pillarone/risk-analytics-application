package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog

class SaveAction extends ResourceBasedAction {

    private static Log LOG = LogFactory.getLog(SaveAction)

    RiskAnalyticsMainModel model
    AbstractUIItem currentItem
    ULCComponent parent

    public SaveAction(ULCComponent parent, RiskAnalyticsMainModel model) {
        super("Save")
        this.model = model
        this.parent = parent
    }

    public SaveAction(ULCComponent parent, RiskAnalyticsMainModel model, AbstractUIItem currentItem) {
        this(parent, model)
        this.currentItem = currentItem
    }



    public void doActionPerformed(ActionEvent event) {
        save(currentItem ? currentItem : model.currentItem)
    }

    void save(AbstractUIItem abstractUIItem) {
        if (isChangedAndNotUsed(abstractUIItem)) {
            LOG.info("Saving ${abstractUIItem.nameAndVersion}.")
            saveItem(abstractUIItem)
        } else {
            LOG.info("Cannot save ${abstractUIItem.nameAndVersion} because it already used in a simulation.")
            I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(parent), "SaveItemAlreadyUsed")
            alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert, abstractUIItem)}] as IWindowListener)
            alert.show()
        }
    }


    void saveItem(AbstractUIItem abstractUIItem) {
        abstractUIItem.save()
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
                boolean deleted = modellingUIItem.deleteDependingResults(getItemModel(modellingUIItem))
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
        this.model.closeItem(model, item)
    }

    private void handleNewVersion(Model model, ParameterizationUIItem item) {
        Closure okAction = {String commentText ->
            if (!item.isLoaded()) {
                item.load()
            }
            item.createNewVersion(model, commentText, false)
            this.model.closeItem(model, item)
        }

        NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(okAction)
        versionCommentDialog.show()
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
    private boolean isChangedAndNotUsed(ModellingUIItem item) {
        return !item.changed || (item.changed && !item.isUsedInSimulation())
    }

    @Override
    boolean isEnabled() {
        if (model.currentItem) return itemChanged(model.currentItem)
        return false
    }

    boolean itemChanged(ModellingUIItem item) {
        return item.changed
    }

    boolean itemChanged(ParameterizationUIItem parameterizationUIItem) {
        return parameterizationUIItem.changed || parameterizationUIItem.item.commentHasChanged()
    }

    boolean itemChanged(Object item) {
        return false
    }
}
