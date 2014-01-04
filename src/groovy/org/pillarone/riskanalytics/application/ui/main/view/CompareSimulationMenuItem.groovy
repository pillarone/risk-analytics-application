package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.main.action.CompareSimulationsAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    CompareSimulationsAction compareSimulationsAction

    def CompareSimulationMenuItem(CompareSimulationsAction compareSimulationsAction) {
        super(compareSimulationsAction);
        this.compareSimulationsAction = compareSimulationsAction;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(compareSimulationsAction.isEnabled())
    }

}