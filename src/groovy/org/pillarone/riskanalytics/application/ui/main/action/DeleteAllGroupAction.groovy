package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeleteAllGroupAction extends DeleteAllAction {
    String alertId

    public DeleteAllGroupAction(ULCTree tree, P1RATModel model, String alertId) {
        super(tree, model)
        this.alertId = alertId
    }

    public void doActionPerformed(ActionEvent actionEvent) {
        ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), alertId)
        alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert.value, alert.firstButtonLabel)}] as IWindowListener)
        alert.show()

    }

    private void handleEvent(String value, String firstButtonValue) {
        if (value.equals(firstButtonValue)) {
            Class itemClass = getSelectedItemGroupClass()
            def modelClass = getSelectedModel().class
            switch (itemClass) {
                case Parameterization:
                    deleteParameterizations(ModellingItemFactory.getNewestParameterizationsForModel(modelClass))
                    break;
                case ResultConfiguration:
                    deleteResultConfigurations(ModellingItemFactory.getNewestResultConfigurationsForModel(modelClass))
                    break;
                case Simulation:
                    deleteResults(ModellingItemFactory.getActiveSimulationsForModel(modelClass))
                    break;
            }
        }
    }

}
