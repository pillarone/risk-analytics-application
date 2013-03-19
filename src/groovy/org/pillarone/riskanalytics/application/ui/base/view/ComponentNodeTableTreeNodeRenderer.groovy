package org.pillarone.riskanalytics.application.ui.base.view

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import org.pillarone.riskanalytics.application.ui.base.action.OpenComponentHelp
import org.pillarone.riskanalytics.application.ui.base.action.TreeCollapser
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeCopier
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeDuplicator
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeRename
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.comment.action.InsertCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.InsertIssueAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowValidationAndCommentsAction
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.main.action.AddDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.action.RemoveDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.view.LockSensitiveMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.SubComponentMenuItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationUtilities
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory

class ComponentNodeTableTreeNodeRenderer extends DefaultTableTreeCellRenderer {

    protected ULCPopupMenu addDynamicNodeMenu
    protected ULCPopupMenu removeDynamicNodeMenu
    protected ULCPopupMenu expandTreeMenu
    protected ULCPopupMenu expandTreeMenuWithHelp
    protected ULCPopupMenu commentMenu
    protected CommentAndErrorView commentAndErrorView


    public ComponentNodeTableTreeNodeRenderer(tree, model) {
        addContextMenu(tree, model)

    }

    public ComponentNodeTableTreeNodeRenderer(tree, model, commentAndErrorView) {
        this.commentAndErrorView = commentAndErrorView
        addContextMenu(tree, model)
    }

    protected ULCMenuItem addContextMenu(tree, model) {

        InsertCommentAction insertComment = new InsertCommentAction(tree.rowHeaderTableTree, -1)
        insertComment.addCommentListener commentAndErrorView
        InsertIssueAction insertIssue = new InsertIssueAction(tree.rowHeaderTableTree, -1)
        insertIssue.addCommentListener commentAndErrorView
        ShowCommentsAction showCommentsAction = new ShowCommentsAction(tree.rowHeaderTableTree, -1, false)
        showCommentsAction.addCommentListener commentAndErrorView
        ShowValidationAndCommentsAction validationAndComments = new ShowValidationAndCommentsAction(tree.rowHeaderTableTree)
        validationAndComments.addCommentListener commentAndErrorView

        OpenComponentHelp help = new OpenComponentHelp(tree.rowHeaderTableTree)

        internalAddContextMenu(tree, model, insertComment, insertIssue, showCommentsAction, validationAndComments, help)

        expandTreeMenu = new ULCPopupMenu()
        expandTreeMenu.name = "popup.expand"
        expandTreeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        expandTreeMenu.add(new ULCMenuItem(new TreeCollapser(tree)))
        expandTreeMenu.addSeparator()
        expandTreeMenu.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.rowHeaderTableTree, viewPortTree: tree.viewPortTableTree, model: model.treeModel)))
        expandTreeMenu.addSeparator()
        expandTreeMenu.add(new ULCMenuItem(insertComment))

        ULCMenuItem expandTreeShowCommentsMenuItem = new ShowCommentsMenuItem(showCommentsAction, model)
        tree.addTreeSelectionListener(expandTreeShowCommentsMenuItem)
        expandTreeMenu.add(expandTreeShowCommentsMenuItem)
        expandTreeMenu.add(new ULCMenuItem(validationAndComments))

        expandTreeMenuWithHelp = new ULCPopupMenu()
        expandTreeMenuWithHelp.name = "popup.expand"
        expandTreeMenuWithHelp.add(new ULCMenuItem(new TreeExpander(tree)))
        expandTreeMenuWithHelp.add(new ULCMenuItem(new TreeCollapser(tree)))

        expandTreeMenuWithHelp.addSeparator()
        expandTreeMenuWithHelp.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.rowHeaderTableTree, viewPortTree: tree.viewPortTableTree, model: model.treeModel)))
        expandTreeMenuWithHelp.addSeparator()
        expandTreeMenuWithHelp.add(new ULCMenuItem(insertComment))
        expandTreeMenuWithHelp.add(new ULCMenuItem(insertIssue))

        ULCMenuItem expandTreeMenuWithHelpShowCommentsMenuItem = new ShowCommentsMenuItem(showCommentsAction, model)
        tree.addTreeSelectionListener(expandTreeMenuWithHelpShowCommentsMenuItem)
        expandTreeMenuWithHelp.add(expandTreeMenuWithHelpShowCommentsMenuItem)
        expandTreeMenuWithHelp.add(new ULCMenuItem(validationAndComments))
        expandTreeMenuWithHelp.addSeparator()
        expandTreeMenuWithHelp.add(new ULCMenuItem(help))

        commentMenu = new ULCPopupMenu()
        commentMenu.name = "popup.comment"
        commentMenu.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.rowHeaderTableTree, viewPortTree: tree.viewPortTableTree, model: model.treeModel)))
        commentMenu.addSeparator()
        commentMenu.add(new ULCMenuItem(insertComment))
        commentMenu.add(new ULCMenuItem(insertIssue))

        ULCMenuItem commentsMenuItem = new ShowCommentsMenuItem(showCommentsAction, model)
        tree.addTreeSelectionListener(commentsMenuItem)
        commentMenu.add(commentsMenuItem)
        commentMenu.add(new ULCMenuItem(validationAndComments))

    }

    protected internalAddContextMenu(def tree, def model, InsertCommentAction insertComment, InsertIssueAction insertIssue, ShowCommentsAction showCommentsAction, ShowValidationAndCommentsAction validationAndComments, OpenComponentHelp help) {

    }

    protected internalAddContextMenu(ULCFixedColumnTableTree tree, ParameterViewModel model, InsertCommentAction insertComment, InsertIssueAction insertIssue, ShowCommentsAction showCommentsAction, ShowValidationAndCommentsAction validationAndComments, OpenComponentHelp help) {
        addDynamicNodeMenu = new ULCPopupMenu()
        ULCMenuItem subComponentMenuItem = new SubComponentMenuItem(new AddDynamicSubComponent(tree.rowHeaderTableTree, model))
        tree.addTreeSelectionListener(subComponentMenuItem)
        addDynamicNodeMenu.add(subComponentMenuItem)
        addDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        addDynamicNodeMenu.add(new ULCMenuItem(new TreeCollapser(tree)))
        addDynamicNodeMenu.addSeparator()
        addDynamicNodeMenu.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.rowHeaderTableTree, viewPortTree: tree.viewPortTableTree, model: model.treeModel)))
        addDynamicNodeMenu.addSeparator()
        addDynamicNodeMenu.add(new ULCMenuItem(insertComment))
        addDynamicNodeMenu.add(new ULCMenuItem(insertIssue))
        ULCMenuItem dynamicNodeShowCommentsMenuItem = new ShowCommentsMenuItem(showCommentsAction, model)
        tree.addTreeSelectionListener(dynamicNodeShowCommentsMenuItem)
        addDynamicNodeMenu.add(dynamicNodeShowCommentsMenuItem)
        addDynamicNodeMenu.add(new ULCMenuItem(validationAndComments))
        addDynamicNodeMenu.addSeparator()
        addDynamicNodeMenu.add(new ULCMenuItem(help))
        addDynamicNodeMenu.name = "popup.expand"

        removeDynamicNodeMenu = new ULCPopupMenu()
        removeDynamicNodeMenu.name = "popup.remove"
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeCollapser(tree)))
        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeNodeCopier(rowHeaderTree: tree.rowHeaderTableTree, viewPortTree: tree.viewPortTableTree, model: model.treeModel)))

        LockSensitiveMenuItem treeNodeDuplicatorMenuItem = new LockSensitiveMenuItem(new TreeNodeDuplicator(tree.rowHeaderTableTree, model))
        tree.addTreeSelectionListener treeNodeDuplicatorMenuItem
        removeDynamicNodeMenu.add(treeNodeDuplicatorMenuItem)

        LockSensitiveMenuItem treeNodeRenameMenuItem = new LockSensitiveMenuItem(new TreeNodeRename(tree.rowHeaderTableTree, model))
        tree.addTreeSelectionListener treeNodeRenameMenuItem
        removeDynamicNodeMenu.add(treeNodeRenameMenuItem)

        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(insertComment))
        removeDynamicNodeMenu.add(new ULCMenuItem(insertIssue))
        ULCMenuItem removeDynamicNodeShowCommentsMenuItem = new ShowCommentsMenuItem(showCommentsAction, model)
        tree.addTreeSelectionListener(removeDynamicNodeShowCommentsMenuItem)
        removeDynamicNodeMenu.add(removeDynamicNodeShowCommentsMenuItem)
        removeDynamicNodeMenu.add(new ULCMenuItem(validationAndComments))
        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(help))
        removeDynamicNodeMenu.addSeparator()

        LockSensitiveMenuItem removeDynamicSubComponentMenuItem = new LockSensitiveMenuItem(new RemoveDynamicSubComponent(tree.rowHeaderTableTree, model))
        tree.addTreeSelectionListener removeDynamicSubComponentMenuItem
        removeDynamicNodeMenu.add(removeDynamicSubComponentMenuItem)
    }


    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        setPopupMenu(component, node)
        customizeNode(component, node)
        if (selected)
            setForeground(Color.white)
        setToolTipText getNodeToolTip(node.getToolTip())
        return component

    }

    void setPopupMenu(IRendererComponent rendererComponent, def node) {
        rendererComponent.setComponentPopupMenu(node.leaf ? commentMenu : expandTreeMenu)
    }

    void customizeNode(IRendererComponent rendererComponent, def node) {
        setForeground(Color.black)
        if (node.comments && node.comments.size() > 0) {
            setFont(getFont().deriveFont(Font.BOLD))
            setToolTipText(HTMLUtilities.convertToHtml(node.commentMessage))
        } else {
            setFont(getFont().deriveFont(Font.PLAIN))
            setToolTipText("")
        }
    }

    void customizeNode(IRendererComponent rendererComponent, ParameterizationTableTreeNode node) {
        Font font = getFont()
        if (node.errors != null) {
            setForeground(node.getErrorColor())
            setToolTipText(node.errorMessage)
            setFont(font.deriveFont(Font.BOLD))
        } else if (node.comments && node.comments.size() > 0) {
            setForeground(Color.black)
            setFont(font.deriveFont(Font.BOLD))
            setToolTipText(HTMLUtilities.convertToHtml(node.commentMessage))
        } else {
            setForeground(Color.black)
            setToolTipText(null)
            setFont(font.deriveFont(Font.PLAIN))
        }

    }

    void setPopupMenu(IRendererComponent rendererComponent, ComponentTableTreeNode node) {
        if (node.component instanceof DynamicComposedComponent) {
            rendererComponent.setComponentPopupMenu(addDynamicNodeMenu)
        } else if (ComponentUtils.isDynamicComposedSubComponentNode(node)) {
            rendererComponent.setComponentPopupMenu(removeDynamicNodeMenu)
        } else {
            rendererComponent.setComponentPopupMenu(expandTreeMenuWithHelp)
        }
    }

    private String getNodeToolTip(String newToolTip) {
        String oldToolTip = getToolTipText()
        return oldToolTip ? oldToolTip + "<br>" + newToolTip : newToolTip
    }


}




class CompareComponentNodeTableTreeNodeRenderer extends ComponentNodeTableTreeNodeRenderer {

    public CompareComponentNodeTableTreeNodeRenderer(tree, model) {
        super(tree, model)
    }

    protected ULCMenuItem addContextMenu(Object tree, Object model) {
        OpenComponentHelp help = new OpenComponentHelp(tree.rowHeaderTableTree)

        addDynamicNodeMenu = new ULCPopupMenu()
        addDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        addDynamicNodeMenu.add(new ULCMenuItem(new TreeCollapser(tree)))

        addDynamicNodeMenu.addSeparator()
        addDynamicNodeMenu.add(new ULCMenuItem(help))
        addDynamicNodeMenu.name = "popup.expand"

        removeDynamicNodeMenu = new ULCPopupMenu()
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeCollapser(tree)))

        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(help))

        expandTreeMenu = new ULCPopupMenu()
        expandTreeMenu.name = "popup.expand"
        expandTreeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        expandTreeMenu.add(new ULCMenuItem(new TreeCollapser(tree)))

        expandTreeMenuWithHelp = new ULCPopupMenu()
        expandTreeMenuWithHelp.name = "popup.expand"
        expandTreeMenuWithHelp.add(new ULCMenuItem(new TreeExpander(tree)))
        expandTreeMenuWithHelp.add(new ULCMenuItem(new TreeCollapser(tree)))

        expandTreeMenuWithHelp.add(new ULCMenuItem(help))
    }



    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setBackground(tableTree, node)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        setPopupMenu(component, node)
        return component

    }

    private void setBackground(ULCTableTree tableTree, Object node) {
        (tableTree.model.isDifferent(node)) ? setBackground(ParameterizationUtilities.ERROR_BG) : setBackground(Color.white)
    }
}

class CompareParameterizationRenderer extends DefaultTableTreeCellRenderer {

    IDataType doubleDataType = DataTypeFactory.getDoubleDataTypeForNonEdit()
    IDataType integerDataType = DataTypeFactory.getIntegerDataTypeForNonEdit()
    IDataType dateDataType = DataTypeFactory.getDateDataType()

    public CompareParameterizationRenderer() {
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setBackground(tableTree, node)
        setDataType(value)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)

        return component
    }

    private void setBackground(ULCTableTree tableTree, Object node) {
        (tableTree.model.isDifferent(node)) ? setBackground(ParameterizationUtilities.ERROR_BG) : setBackground(Color.white)
    }

    private void setDataType(Double value) {
        super.setDataType(doubleDataType)
    }

    private void setDataType(Integer value) {
        super.setDataType(integerDataType)
    }

    private void setDataType(Date value) {
        super.setDataType(dateDataType)
    }

    private void setDataType(def value) {
        super.setDataType(null)
    }


}




