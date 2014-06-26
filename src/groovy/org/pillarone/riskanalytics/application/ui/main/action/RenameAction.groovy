package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RenameAction extends SingleItemAction {

    // Oh my! An instance is Ctor'd for each GUI node!
    //
    public RenameAction(ULCTableTree tree) {
        super("Rename", tree)
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {

        if( quitWithAlertIfCalledWhenDisabled() ){
            return
        }

        /* For opening the cellEditor implement a extension and call startEditingPath on clientSide (remember to convert the TreePath)
        ULCTreeModelAdapter adapter = ULCSession.currentSession().getModelAdapterProvider().getModelAdapter(ITreeModel.class, tree.model)
        tree.invokeUI("startEditingAtPath", [adapter.getDescriptionForPath(tree.getSelectionPath())] as Object[])
        */
        boolean usedInSimulation = false
        ModellingUIItem selectedItem = selectedUIItem
        if (selectedItem.item instanceof Parameterization || selectedItem.item instanceof ResultConfiguration) {
            usedInSimulation = selectedItem.usedInSimulation
            if (!usedInSimulation) {
                usedInSimulation = nameUsedInSimulation(selectedItem.item)
            }
        }
        if (usedInSimulation) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "RenamingLocked")
            alert.show()
        } else {
            NodeNameDialog dialog = new NodeNameDialog(UlcUtilities.getWindowAncestor(tree), selectedItem)
            dialog.title = dialog.getText("renameTitle") + " " + selectedItem.name

            dialog.okAction = { String name -> selectedItem.rename(name) }
            dialog.show()
        }
    }

    protected boolean nameUsedInSimulation(ResultConfiguration item) {
        List runs = SimulationRun.executeQuery("from ${SimulationRun.name} as run where run.resultConfiguration.name = :name ", ["name": item.name])
        return runs != null && runs.size() > 0
    }

    protected boolean nameUsedInSimulation(Parameterization item) {
        List runs = SimulationRun.executeQuery("from ${SimulationRun.name} as run where run.parameterization.name = :name ", ["name": item.name])
        return runs != null && runs.size() > 0
    }

}
