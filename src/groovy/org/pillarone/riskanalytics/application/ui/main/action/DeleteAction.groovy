package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.main.view.AlertDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeleteAction extends SelectionTreeAction {

    Closure okAction = { List<AbstractUIItem> selectedItems, def nextItemToSelect ->
        removeItem(selectedItems)
        tree.addPathSelection(new TreePath(DefaultTableTreeModel.getPathToRoot(nextItemToSelect) as Object[]))
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
        AlertDialog dialog = new AlertDialog(tree, selectedItems, getNextSelectedItem(), UIUtils.getText(this.class, "warningTitle"), UIUtils.getText(this.class, "warningMessage", [getNames(selectedItems)]), okAction)
        dialog.init()
        dialog.show()
    }

    private void removeItem(ModellingUIItem selectedItem) {
        boolean usedInSimulation = false
        if (selectedItem instanceof ParameterizationUIItem || selectedItem instanceof ResultConfigurationUIItem) {
            usedInSimulation = selectedItem.isUsedInSimulation()
        }
        Model selectedModel = getSelectedModel()
        if (!usedInSimulation) {
            selectedItem.remove()
        } else {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "DeleteUsedItem")
            alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert.value, alert.firstButtonLabel, selectedModel, selectedItem)}] as IWindowListener)
            alert.show()
        }
    }


    private void removeItem(def selectedItem) {}

    private void removeItem(List<ModellingUIItem> selectedItems) {
        selectedItems.each {ModellingUIItem selectedItem ->
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
        return items*.nameAndVersion.sort().join(", ")
    }

}