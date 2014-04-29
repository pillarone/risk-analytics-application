package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction

// General purpose ULCMenuItem extension allowing dynamic disable/enable
// via underlying SelectionTreeAction.
//
class EnabledCheckingMenuItem extends ULCMenuItem implements ITreeSelectionListener {
    SelectionTreeAction myAction

    EnabledCheckingMenuItem(SelectionTreeAction action) {
        super(action)
        myAction = action
        // Tell the tree to inform this menuitem when selections change.
        // I hope it only tells us when the menu is actually popped up.
        // I would hate to think that all the menus on all the nodes get informed whenever any selection changes.
        myAction.tree.addTreeSelectionListener(this)
    }

    // This must be the code that changes the menu item's enablement dynamically
    // That innocent-looking assignment of a field is probably doing a call to getEnabled()
    // Don't you just hate groovy moving the carpet under your feet ?
    //
    void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        enabled = myAction.enabled
    }

    // For future use when our framework supports a tidier world and can know when to unregister
    // Eg see BatchView as example (there, name destroy() is used).
    //
    void close(){
        myAction.tree.removeTreeSelectionListener(this)
    }

}

