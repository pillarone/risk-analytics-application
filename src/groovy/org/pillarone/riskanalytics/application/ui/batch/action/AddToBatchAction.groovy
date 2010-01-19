package org.pillarone.riskanalytics.application.ui.batch.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.event.ActionEvent
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.ui.simulation.action.MaxIterationsAlert
import org.pillarone.riskanalytics.application.ui.simulation.action.RunSimulationAction
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.output.batch.OutputStrategyFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad jaada
 */

public class AddToBatchAction extends RunSimulationAction {
    P1RATModel p1RATModel

    public AddToBatchAction(AbstractConfigurationModel model, P1RATModel p1RATModel) {
        super("AddToBatch", model);
        this.p1RATModel = p1RATModel
    }

    public void doActionPerformed(ActionEvent event) {
        boolean isValid = validate(event)
        if (isValid)
            addToBatch()
    }

    private void addToBatch() {
        int maxIterations = ApplicationHolder.application.config.maxIterations
        if (model.iterationCount > maxIterations) {
            ULCAlert alert = new MaxIterationsAlert(maxIterations)
            alert.show()
            model.iterationCount = maxIterations
        } else {
            try {
                BatchRun batchRun = model.itemsComboBoxModel.selectedObject
                if (!batchRun) {
                    Object newBatchRunName = model.itemsComboBoxModel.getSelectedItem()
                    batchRun = new BatchRun(name: newBatchRunName, executionTime: new Date())
                    batchRun.save()
                    updateModels(batchRun)
                }
                model.addToBatch(batchRun, OutputStrategyFactory.getEnum( model.outputStrategyComboBoxModel.getStrategy().class))
                model.batchAdded(UIUtils.getText(this.class,"succes" ), false  )
            } catch (Exception  ex) {
                model.batchAdded(UIUtils.getText(this.class,"error" ), true  )
            }

        }
    }

    private void updateModels(BatchRun batchRun) {
        p1RATModel.addItem(batchRun)
        model.itemsComboBoxModel.addItem(batchRun)
    }

}


