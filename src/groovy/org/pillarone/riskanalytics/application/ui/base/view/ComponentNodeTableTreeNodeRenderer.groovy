package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import org.pillarone.riskanalytics.application.ui.base.action.OpenComponentHelp
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeDuplicator
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeRename
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.comment.action.InsertCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.main.action.AddDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.action.RemoveDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationUtilities
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent

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
        ShowCommentsAction showCommentsAction = new ShowCommentsAction(tree.rowHeaderTableTree, -1, false)
        showCommentsAction.addCommentListener commentAndErrorView

        OpenComponentHelp help = new OpenComponentHelp(tree.rowHeaderTableTree)

        addDynamicNodeMenu = new ULCPopupMenu()
        addDynamicNodeMenu.add(new ULCMenuItem(new AddDynamicSubComponent(tree.rowHeaderTableTree, model)))
        addDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        addDynamicNodeMenu.addSeparator()
        addDynamicNodeMenu.add(new ULCMenuItem(insertComment))
        addDynamicNodeMenu.add(new ULCMenuItem(showCommentsAction))
        addDynamicNodeMenu.addSeparator()
        addDynamicNodeMenu.add(new ULCMenuItem(help))
        addDynamicNodeMenu.name = "popup.expand"

        removeDynamicNodeMenu = new ULCPopupMenu()
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeNodeDuplicator(tree.rowHeaderTableTree, model)))
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeNodeRename(tree.rowHeaderTableTree, model)))
        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(new RemoveDynamicSubComponent(tree.rowHeaderTableTree, model)))
        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(insertComment))
        removeDynamicNodeMenu.add(new ULCMenuItem(showCommentsAction))
        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(help))

        expandTreeMenu = new ULCPopupMenu()
        expandTreeMenu.name = "popup.expand"
        expandTreeMenu.add(new ULCMenuItem(new TreeExpander(tree)))

        expandTreeMenuWithHelp = new ULCPopupMenu()
        expandTreeMenuWithHelp.name = "popup.expand"
        expandTreeMenuWithHelp.add(new ULCMenuItem(new TreeExpander(tree)))
        //expandTreeMenuWithHelp.addSeparator()

        expandTreeMenuWithHelp.addSeparator()
        expandTreeMenuWithHelp.add(new ULCMenuItem(insertComment))
        expandTreeMenuWithHelp.add(new ULCMenuItem(showCommentsAction))
        expandTreeMenuWithHelp.addSeparator()
        expandTreeMenuWithHelp.add(new ULCMenuItem(help))

        commentMenu = new ULCPopupMenu()
        commentMenu.name = "popup.comment"
        commentMenu.add(new ULCMenuItem(insertComment))
        commentMenu.add(new ULCMenuItem(showCommentsAction))

    }


    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        setPopupMenu(component, node)
        customizeNode(component, node)
        return component

    }

    void setPopupMenu(IRendererComponent rendererComponent, def node) {
        rendererComponent.setComponentPopupMenu(node.leaf ? commentMenu : expandTreeMenu)
    }

    void customizeNode(IRendererComponent rendererComponent, def node) {
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
        if (node.errorMessage == null) {
            setForeground(Color.black)
            setToolTipText(null)
            setFont(font.deriveFont(Font.PLAIN))
        } else {
            setForeground(Color.red)
            setToolTipText(node.errorMessage)
            setFont(font.deriveFont(Font.BOLD))
        }
        if (node.comments && node.comments.size() > 0) {
            setForeground(Color.black)
            setFont(font.deriveFont(Font.BOLD))
            setToolTipText(HTMLUtilities.convertToHtml(node.commentMessage))
        } else {
            setForeground(Color.black)
            setFont(font.deriveFont(Font.PLAIN))
            setToolTipText("")
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


}




class CompareComponentNodeTableTreeNodeRenderer extends ComponentNodeTableTreeNodeRenderer {

    public CompareComponentNodeTableTreeNodeRenderer(tree, model) {
        super(tree, model)
    }

    protected ULCMenuItem addContextMenu(Object tree, Object model) {
        OpenComponentHelp help = new OpenComponentHelp(tree.rowHeaderTableTree)

        addDynamicNodeMenu = new ULCPopupMenu()
        addDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))

        addDynamicNodeMenu.addSeparator()
        addDynamicNodeMenu.add(new ULCMenuItem(help))
        addDynamicNodeMenu.name = "popup.expand"

        removeDynamicNodeMenu = new ULCPopupMenu()
        removeDynamicNodeMenu.add(new ULCMenuItem(new TreeExpander(tree)))

        removeDynamicNodeMenu.addSeparator()
        removeDynamicNodeMenu.add(new ULCMenuItem(help))

        expandTreeMenu = new ULCPopupMenu()
        expandTreeMenu.name = "popup.expand"
        expandTreeMenu.add(new ULCMenuItem(new TreeExpander(tree)))

        expandTreeMenuWithHelp = new ULCPopupMenu()
        expandTreeMenuWithHelp.name = "popup.expand"
        expandTreeMenuWithHelp.add(new ULCMenuItem(new TreeExpander(tree)))

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

    public CompareParameterizationRenderer() {
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setBackground(tableTree, node)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        return component
    }

    private void setBackground(ULCTableTree tableTree, Object node) {
        (tableTree.model.isDifferent(node)) ? setBackground(ParameterizationUtilities.ERROR_BG) : setBackground(Color.white)
    }


}





