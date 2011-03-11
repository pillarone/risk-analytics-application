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
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.AlertDialog
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DeleteAction extends SelectionTreeAction {

    Closure okAction = { def selectedItem, def nextItemToSelect ->
        removeItem(selectedItem)
        tree.addPathSelection(new TreePath(DefaultTableTreeModel.getPathToRoot(nextItemToSelect) as Object[]))
    }

    public DeleteAction(ULCTableTree tree, P1RATModel model) {
        super("Delete", tree, model)
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        def selectedItem = getSelectedItem()
        if (!selectedItem) return
        AlertDialog dialog = new AlertDialog(tree, selectedItem, getNextSelectedItem(), UIUtils.getText(this.class, "warningTitle"), UIUtils.getText(this.class, "warningMessage", [tree?.selectedPath?.lastPathComponent?.toString()]), okAction)
        dialog.init()
        dialog.show()
    }

    private void removeItem(ModellingItem selectedItem) {
        boolean usedInSimulation = false
        if (selectedItem instanceof Parameterization || selectedItem instanceof ResultConfiguration) {
            usedInSimulation = selectedItem.isUsedInSimulation()
        }
        Model selectedModel = getSelectedModel()
        if (!usedInSimulation) {
            model.removeItem(selectedModel, selectedItem)
        } else {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "DeleteUsedItem")
            alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert.value, alert.firstButtonLabel, model, selectedModel, selectedItem)}] as IWindowListener)
            alert.show()
        }
    }

    private void removeItem(BatchRun selectedItem) {
        model.removeItem(selectedItem)
    }

    private void removeItem(def selectedItem) {}


    private void handleEvent(String value, String firstButtonValue, P1RATModel model, Model selectedModel, ModellingItem item) {
        synchronized (item) {
            if (value.equals(firstButtonValue)) {
                model.deleteDependingResults(selectedModel, item)
                model.removeItem(selectedModel, item)
            }
        }
    }

    protected List allowedRoles() {
        return []
    }

}