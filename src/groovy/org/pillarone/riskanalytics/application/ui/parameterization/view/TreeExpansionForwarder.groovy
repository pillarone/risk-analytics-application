package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.event.ITreeExpansionListener
import com.ulcjava.base.application.event.TreeExpansionEvent
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TreeExpansionForwarder implements ITreeExpansionListener {

    ITableTreeModel model

    public TreeExpansionForwarder(ITableTreeModel model) {
        this.@model = model
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        forwardStateChange(event.path, false)
    }

    public void treeExpanded(TreeExpansionEvent event) {
        forwardStateChange(event.path, true)
    }

    private void forwardStateChange(TreePath path, boolean expanded) {
        model.expansionChanged(path, expanded)
    }

}
