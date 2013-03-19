package org.pillarone.riskanalytics.application.ui.comment.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.comment.view.CommentListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class InsertFunctionCommentAction extends InsertCommentAction {


    public InsertFunctionCommentAction(ULCTableTree tree) {
        super(tree, -1);
    }

    void doActionPerformed(ActionEvent event) {
        commentListeners.each {CommentListener commentListener ->
            commentListener.addNewFunctionCommentView(getSelectedFunction())
        }
    }


    private int[] getSelectedColumns() {
        List list = tree.selectedColumns?.collect { tree.convertColumnIndexToModel(it) } as List
        return list?.sort() as int[]
    }

    private int[] getSelectedRows() {
        return (tree.selectedRows as List)?.sort() as int[]
    }

    List<Map> getSelectedFunction() {
        List list = []
        getSelectedColumns().each {int column ->
            getSelectedRows().each {int row ->
                Map functionsMap = [:]
                String function = tree.model.getFunction(column).name
                int periodIndex = tree.model.getPeriodIndex(column)
                TreePath path = tree.getPathForRow(row)
                String resultPath = path.lastPathComponent.resultPath
                functionsMap['function'] = function
                functionsMap['periodIndex'] = periodIndex
                functionsMap['path'] = resultPath
                list << functionsMap
            }
        }
        return list
    }

}
