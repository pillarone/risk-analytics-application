package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    def compareSimulationsAction

    def CompareSimulationMenuItem(compareSimulationsAction) {
        super(compareSimulationsAction);
        this.compareSimulationsAction = compareSimulationsAction;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(compareSimulationsAction.isEnabled())
    }

}