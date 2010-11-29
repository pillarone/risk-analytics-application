package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterizationMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    def compareParameterizationsAction

    public CompareParameterizationMenuItem(compareParameterizationsAction) {
        super(compareParameterizationsAction)
        this.compareParameterizationsAction = compareParameterizationsAction
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        setEnabled(compareParameterizationsAction.isEnabled())
    }

}

