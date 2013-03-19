package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleSelectMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    SelectionTreeAction iAction

    public SingleSelectMenuItem(SelectionTreeAction iAction) {
        super(iAction);
        this.iAction = iAction;
        this.iAction.tree.addTreeSelectionListener this
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        List paths = iAction.tree.selectionPaths
        boolean b = iAction.isEnabled() && paths && paths.size() == 1
        setEnabled(b)
    }

}
