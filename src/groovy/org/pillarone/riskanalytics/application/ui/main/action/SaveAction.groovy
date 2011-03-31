package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

class SaveAction extends ResourceBasedAction {
    P1RATModel model
    def currentItem

    public SaveAction(P1RATModel model) {
        super("Save")
        this.model = model
    }

    public SaveAction(P1RATModel model, def currentItem) {
        super("Save")
        this.model = model
        this.currentItem = currentItem
    }



    public void doActionPerformed(ActionEvent event) {
        save(currentItem ? currentItem : model.currentItem)
    }

    void save(ModellingItem modellingItem) {
        if (!modellingItem.isUsedInSimulation()) {
            saveItem(modellingItem)
        } else {
            I18NAlert alert = new I18NAlert("SaveItemAlreadyUsed")
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
                item.load()
                model.closeItem(itemModel, item)
            } else if (alert.value.equals(alert.secondButtonLabel)) {
                if (model.deleteDependingResults(getItemModel(item), item)) {
                    model.saveItem(item)
                } else {
                    //item used in running simulation
                    new I18NAlert("DeleteAllDependentRunsError").show()
                }
            }
        }
    }


    private Model getItemModel(ModellingItem item) {
        Model itemModel = item.modelClass.newInstance()
        itemModel.init()
        return itemModel
    }


}
