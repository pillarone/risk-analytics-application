package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RenameAction extends SelectionTreeAction {

    public RenameAction(ULCTableTree tree, P1RATModel model) {
        super("Rename", tree, model)
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        /* For opening the cellEditor implement a extension and call startEditingPath on clientSide (remember to convert the TreePath)
        ULCTreeModelAdapter adapter = ULCSession.currentSession().getModelAdapterProvider().getModelAdapter(ITreeModel.class, tree.model)
        tree.invokeUI("startEditingAtPath", [adapter.getDescriptionForPath(tree.getSelectionPath())] as Object[])
        */
        def selectedItem = getSelectedItem()
        if (!(selectedItem instanceof ModellingItem)) return
        boolean usedInSimulation = isUsedInSimulation(selectedItem)
        if (!usedInSimulation) {
            selectedItem.setModelClass(getSelectedModel().class)
            NodeNameDialog dialog = new NodeNameDialog(UlcUtilities.getWindowAncestor(tree), selectedItem)
            dialog.title = dialog.getText("renameTitle") + " " + selectedItem.name

            dialog.okAction = { model.renameItem(selectedItem, dialog.nameInput.text) }
            dialog.show()
        } else {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "RenamingLocked")
            alert.show()
        }
    }

    /**
     * check if this item or another version of them is used in simulation
     * @param parameterization
     * @return
     */
    protected boolean isUsedInSimulation(Parameterization parameterization) {
        List runs = SimulationRun.executeQuery(" from ${SimulationRun.class.name} as run where run.parameterization.name = :name ", ["name": parameterization.name], [max: 1])
        return runs != null && runs.size() > 0
    }

    /**
     * check if this item or another version of them is used in simulation
     * @param resultConfiguration
     * @return
     */
    protected boolean isUsedInSimulation(ResultConfiguration resultConfiguration) {
        List runs = SimulationRun.executeQuery(" from ${SimulationRun.class.name} as run where run.resultConfiguration.name = :name ", ["name": resultConfiguration.name], [max: 1])
        return runs != null && runs.size() > 0
    }

    protected boolean isUsedInSimulation(def item) {
        return false
    }

}
