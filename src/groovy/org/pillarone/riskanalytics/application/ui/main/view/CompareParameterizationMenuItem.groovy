package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.main.action.CompareParameterizationsAction

// TODO - DELETE THIS CLASS ONCE WE KNOW THE EnableCheckingMenuItem does the job.
@Deprecated
class CompareParameterizationMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    CompareParameterizationsAction compareParameterizationsAction

    CompareParameterizationMenuItem(CompareParameterizationsAction compareParameterizationsAction) {
        super(compareParameterizationsAction)
        this.compareParameterizationsAction = compareParameterizationsAction
    }

    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        enabled = compareParameterizationsAction.enabled
    }

}

