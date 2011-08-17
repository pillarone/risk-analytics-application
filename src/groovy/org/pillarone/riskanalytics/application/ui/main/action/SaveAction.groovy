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

class SaveAction extends ResourceBasedAction {
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
            saveItem(abstractUIItem)
        } else {
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
                Model itemModel = getItemModel(modellingUIItem)
                modellingUIItem.createNewVersion(itemModel, false)
                model.closeItem(itemModel, modellingUIItem)
            } else if (alert.value.equals(alert.secondButtonLabel)) {
                boolean deleted = modellingUIItem.deleteDependingResults(getItemModel(modellingUIItem))
                if (deleted) {
                    saveItem(modellingUIItem)
                } else {
                    String errorKey = UIItemUtils.isUsedInRunningSimulation(modellingUIItem.item) ? "DeleteAllDependentRunningsError" : "DeleteAllDependentRunsError"
                    new I18NAlert(UlcUtilities.getWindowAncestor(parent), errorKey).show()
                }
            }
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
