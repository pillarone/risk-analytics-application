package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportItemAction extends ExportAction {

    public ExportItemAction(ULCTableTree tree) {
        this(tree, "Export", 'xlsx')
    }

    public ExportItemAction(ULCTableTree tree, String actionName, String fileExtension) {
        super(tree, actionName, fileExtension)
    }


    public void doActionPerformed(ActionEvent event) {
        List selectedItems = getAllSelectedObjects()?.collect { ItemNode itemNode ->
            itemNode.itemNodeUIItem.item
        }
        doAction(selectedItems)
    }


    protected void doAction(List<ModellingItem> items) {
        if (atLeastOneItemChanged(items)) {
            new I18NAlert("UnsavedExport").show()
        } else {
            exportAll(items)
        }
    }

    private boolean atLeastOneItemChanged(List<ModellingItem> items) {
        for (ModellingItem item : items) {
            if (item.changed)
                return true
        }
        return false
    }

    protected void doAction(Simulation item) {
        exportSimulations([item])
    }

}
