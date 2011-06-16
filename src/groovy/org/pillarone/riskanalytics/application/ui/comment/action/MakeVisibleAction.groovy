package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MakeVisibleAction extends AbstractCommentAction {

    AbstractCommentableItemModel model
    String path

    public MakeVisibleAction(AbstractCommentableItemModel model, String path) {
        super("MakeVisibleAction")
        this.model = model
        this.path = path
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        TreePath treePath = model.getTreePath(path)
        if (treePath) {
            getViewPortTableTree().makeVisible treePath
            getRowHeaderTableTree().makeVisible treePath
            getViewPortTableTree().scrollCellToVisible treePath, 0
            getViewPortTableTree().selectionModel.setSelectionPath treePath
            model.selectTab(0)
        }
    }

    @Override
    void executeAction(String path, int periodIndex, String displayPath) {}

    ULCTableTree getViewPortTableTree() {
        return commentListeners[0].tableTree.viewPortTableTree
    }

    ULCTableTree getRowHeaderTableTree() {
        return commentListeners[0].tableTree.rowHeaderTableTree
    }


}
