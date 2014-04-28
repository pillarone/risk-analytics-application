package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RenameAction extends SelectionTreeAction {

    // Oh my! An instance is Ctor'd for each GUI node!
    //
    public RenameAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("Rename", tree, model)
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        /* For opening the cellEditor implement a extension and call startEditingPath on clientSide (remember to convert the TreePath)
        ULCTreeModelAdapter adapter = ULCSession.currentSession().getModelAdapterProvider().getModelAdapter(ITreeModel.class, tree.model)
        tree.invokeUI("startEditingAtPath", [adapter.getDescriptionForPath(tree.getSelectionPath())] as Object[])
        */
        boolean usedInSimulation = false
        ModellingUIItem selectedItem = selectedUIItem
        if (selectedItem.item instanceof Parameterization || selectedItem.item instanceof ResultConfiguration) {
            selectedItem.item.setModelClass(selectedModel.class) //TODO: still necessary?
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

            dialog.okAction = { selectedItem.rename(dialog.nameInput.text) }
            dialog.show()
        }
    }

    // I think this is the 'right' way to disable the menu when multiple items are selected.
    // This code is called when a new ULCMenuItem is ctor'd with a new RenamAction instance
    // Instead I was expecting this is called whenever user right-clicks a node to bring up the context menu.
    // (*That* seems to happen for the Compare menu on P14ns, for instance.)
    // Reason seems to be: ULCMenuItem does not check for enabling/disabling.
    // The reason it works for CompareParameterizationsAction is, that is wrapped in a CompareParameterizationMenuItem
    // which adds dynamic checking to ULCMenuItem.
    //
    // I'm adding a generic alternitive to CompareParameterizationMenuItem that can offer the same ability to
    // any SelectionTreeAction (like RenameAction or RunSimulationAction).
    //
    boolean isEnabled() {
        if (getAllSelectedObjectsSimpler().size() > 1) {
            return false
        }
        return super.isEnabled()//generic checks like user roles
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
