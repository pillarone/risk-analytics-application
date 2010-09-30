package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportItemAction extends ExportAction {

    public ExportItemAction(ULCTree tree, P1RATModel model) {
        super(tree, model, "Export")
    }


    public void doActionPerformed(ActionEvent event) {
        def selectedItem = getSelectedItem()
        if (selectedItem.class)
            doAction(getSelectedObjects(selectedItem.class)?.collect {it.item})
    }


    protected void doAction(List<ModellingItem> items) {
        if (atLeastOneItemChanged(items)) {
            new I18NAlert("UnsavedExport").show()
        } else {
            exportItems(items)
        }
    }

    private boolean atLeastOneItemChanged(List<ModellingItem> items) {
        for (Object item: items) {
            if (item.changed)
                return true
        }
        return false
    }

    protected void doAction(Simulation item) {
        exportSimulations([item])
    }

}
