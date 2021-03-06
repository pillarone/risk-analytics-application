package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.base.action.TableTreeCopier
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.comment.action.InsertFunctionCommentAction
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory

class StochasticValueTableTreeCellRenderer extends NumberFormatRenderer {
    int index
    ULCPopupMenu copyMenu
    CommentAndErrorView commentAndErrorView
    static final Color backgroundColor = new Color(240, 240, 190)

    public StochasticValueTableTreeCellRenderer(int index, def tableTree, CommentAndErrorView commentAndErrorView) {
        this.index = index
        this.commentAndErrorView = commentAndErrorView
        copyMenu = new ULCPopupMenu()
        copyMenu.add(new ULCMenuItem(new TableTreeCopier(table: tableTree)))
        InsertFunctionCommentAction action = new InsertFunctionCommentAction(tableTree)
        action.addCommentListener commentAndErrorView
        copyMenu.add(new ULCMenuItem(action))
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        updateFractionDigits(tableTree)
        setFormat(value)
        setBackgroundForNode(node, tableTree, value)
        setHorizontalAlignment(RIGHT)
        def renderer = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        renderer.setComponentPopupMenu(copyMenu)

        return renderer
    }

    private boolean isStochasticValue(Object node, ULCTableTree tableTree, def value) {
        tableTree.model.isStochasticValue(node, index)
    }

    private boolean isStochasticValue(Object node, ULCTableTree tableTree, String value) {
        true
    }


    private def setBackgroundForNode(ResultTableTreeNode node, ULCTableTree tableTree, Number value) {
        if (!isStochasticValue(node, tableTree, value)) {
            setBackground backgroundColor
        } else {
            setBackground Color.white
        }
    }

    private def setBackgroundForNode(SimpleTableTreeNode node, ULCTableTree tableTree, def value) {
        setBackground Color.white
    }


}


class NumberFormatRenderer extends DefaultTableTreeCellRenderer {
    ULCNumberDataType numberDataType

    public setFormat(def value) {
        setDataType null
    }

    public setFormat(Number value) {
        setDataType(getNumberDataType())
    }


    public ULCNumberDataType getNumberDataType() {
        if (numberDataType == null) {
            numberDataType = DataTypeFactory.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }

    public void updateFractionDigits(tableTree) {
        if (tableTree?.model?.numberDataType) {
            getNumberDataType().setMinFractionDigits tableTree.model.numberDataType.minFractionDigits
            getNumberDataType().setMaxFractionDigits tableTree.model.numberDataType.maxFractionDigits
        }
    }
}


