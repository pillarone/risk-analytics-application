package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.action.CompareSimulationsAction

@CompileStatic
class CompareSimulationMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    CompareSimulationsAction compareSimulationsAction

    CompareSimulationMenuItem(CompareSimulationsAction compareSimulationsAction) {
        super(compareSimulationsAction);
        this.compareSimulationsAction = compareSimulationsAction
    }

    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        enabled = compareSimulationsAction.enabled
    }
}