package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.core.model.Model
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
        Model model = getSelectedModel()
        def item = getSelectedItem()
        if (model != null && item != null) {
            if (item instanceof Simulation) {
                LOG.trace "Reading end time from simulation: ${System.identityHashCode(item)}: ${item.end?.time}"
            }
            this.model.openItem(model, item)
        }
    }

}
