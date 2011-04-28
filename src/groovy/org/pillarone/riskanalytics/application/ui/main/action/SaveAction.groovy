package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class SaveAction extends ResourceBasedAction {
    P1RATModel model
    def currentItem
    ULCComponent parent

    public SaveAction(ULCComponent parent, P1RATModel model) {
        super("Save")
        this.model = model
        this.parent = parent
    }

    public SaveAction(ULCComponent parent, P1RATModel model, def currentItem) {
        this(parent, model)
        this.currentItem = currentItem
    }



    public void doActionPerformed(ActionEvent event) {
        save(currentItem ? currentItem : model.currentItem)
    }

    void save(ModellingItem modellingItem) {
        if (isChangedAndNotUsed(modellingItem)) {
            saveItem(modellingItem)
        } else {
            I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(parent), "SaveItemAlreadyUsed")
            alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert, modellingItem)}] as IWindowListener)
            alert.show()
        }
    }



    void save(def item) {
        model.saveItem(item)
    }

    void saveItem(ModellingItem modellingItem) {
        model.saveItem(modellingItem)
    }

    /**
     * save a changed item by creating  a new version or deleting all dependent simulations
     * @param alert
     * @param item
     */
    private void handleEvent(I18NAlert alert, ModellingItem item) {

        synchronized (item) {
            if (alert.value.equals(alert.firstButtonLabel)) {
                Model itemModel = getItemModel(item)
                model.createNewVersion(itemModel, item)
                model.closeItem(itemModel, item)
            } else if (alert.value.equals(alert.secondButtonLabel)) {
                if (model.deleteDependingResults(getItemModel(item), item)) {
                    model.saveItem(item)
                } else {
                    //item used in running simulation
                    new I18NAlert(UlcUtilities.getWindowAncestor(parent), "DeleteAllDependentRunsError").show()
                }
            }
        }
    }


    private Model getItemModel(ModellingItem item) {
        Model itemModel = item.modelClass.newInstance()
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
    private boolean isChangedAndNotUsed(ModellingItem item) {
        return !item.changed || (item.changed && !item.isUsedInSimulation())
    }

    @Override
    boolean isEnabled() {
        if (model.currentItem) return itemChanged(model.currentItem)
        return false
    }

    boolean itemChanged(ModellingItem item) {
        return item.changed
    }

    boolean itemChanged(Parameterization item) {
        return item.changed || item.commentHasChanged()
    }
}
