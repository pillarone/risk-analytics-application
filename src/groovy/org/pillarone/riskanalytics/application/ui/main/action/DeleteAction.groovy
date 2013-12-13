package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.main.view.AlertDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.workflow.StatusChangeService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeleteAction extends SelectionTreeAction {

    Closure okAction = { List<AbstractUIItem> selectedItems ->
        removeItem(selectedItems)
    }

    public DeleteAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("Delete", tree, model)
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        List<AbstractUIItem> selectedItems = getSelectedUIItems()
        final Iterator<AbstractUIItem> iterator = selectedItems.iterator()
        while (iterator.hasNext()) {
            if (!iterator.next().isDeletable()) {
                iterator.remove()
            }
        }
        if (!selectedItems) return
        AlertDialog dialog = new AlertDialog(tree, selectedItems, UIUtils.getText(DeleteAction, "warningTitle"), UIUtils.getText(DeleteAction, "warningMessage", [getNames(selectedItems)]), okAction)
        dialog.init()
        dialog.show()
    }

    protected void removeItem(ModellingUIItem selectedItem) {
        boolean usedInSimulation = selectedItem.isUsedInSimulation()
        Model selectedModel = getSelectedModel()
        if (!usedInSimulation) {
            if (!selectedItem.remove()) {
                ULCAlert alert = new ULCAlert(UlcUtilities.getWindowAncestor(tree), "Error", "Error removing selected item. See log files for details.", "Ok")
                alert.messageType = ULCAlert.ERROR_MESSAGE
                alert.show()
            }
        } else {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "DeleteUsedItem")
            alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert.value, alert.firstButtonLabel, selectedModel, selectedItem)}] as IWindowListener)
            alert.show()
        }
    }


    protected void removeItem(def selectedItem) {}

    protected void removeItem(AbstractUIItem selectedItem) {
        selectedItem.remove()
    }

    protected void removeItem(List<AbstractUIItem> selectedItems) {
        selectedItems.each { selectedItem ->
            if (selectedItem instanceof ParameterizationUIItem && selectedItem.item?.status == Status.DATA_ENTRY) {
                StatusChangeService.service.clearAudit(selectedItem.item as Parameterization)
            }
            removeItem(selectedItem)
        }
    }

    private void handleEvent(String value, String firstButtonValue, Model selectedModel, ModellingUIItem item) {
        synchronized (item) {
            if (value.equals(firstButtonValue)) {
                if (item.deleteDependingResults(selectedModel))
                    removeItem(item)
                else
                    new I18NAlert(UlcUtilities.getWindowAncestor(tree), "DeleteAllDependentRunsError").show()
            }
        }
    }

    protected List allowedRoles() {
        return []
    }

    private String getNames(List<AbstractUIItem> items) {
        String separator = ", "
        String names = items*.nameAndVersion.sort().join(separator)
        String lines = UIUtils.addBreakLines(names, 80, separator)
        return lines
    }

}