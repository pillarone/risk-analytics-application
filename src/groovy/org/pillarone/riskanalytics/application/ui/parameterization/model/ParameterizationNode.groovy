package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.util.Font
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.view.CompareParameterizationMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.application.ui.main.action.*

class ParameterizationNode extends VersionedItemNode {


    public ParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem, false)
    }

    @Override
    public ULCPopupMenu getPopupMenu(MainSelectionTableTreeCellRenderer renderer, ULCTableTree tree) {
        if (renderer.popupMenus['parameterNodePopUpMenu']) return renderer.popupMenus['parameterNodePopUpMenu']
        ULCPopupMenu parameterNodePopUpMenu = new ULCPopupMenu()
        parameterNodePopUpMenu.name = "parameterNodePopUpMenu"
        parameterNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.addSeparator()
        ULCMenuItem compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, abstractUIItem.mainModel))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        parameterNodePopUpMenu.add(compareParameterizationMenuItem)
        parameterNodePopUpMenu.add(new ULCMenuItem(new TagsAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        if (!UserContext.isStandAlone()) {
            def transactionsEnabled = ApplicationHolder.getApplication().getConfig().getProperty("transactionsEnabled")
            if (transactionsEnabled != null && transactionsEnabled == true) {
                parameterNodePopUpMenu.addSeparator()
                parameterNodePopUpMenu.add(new ULCMenuItem(new ChooseDealAction(tree, abstractUIItem.mainModel)))
                parameterNodePopUpMenu.add(new ULCMenuItem(new StartWorkflowAction(tree, abstractUIItem.mainModel)))
            }
        }
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, abstractUIItem.mainModel)))
        renderer.popupMenus['parameterNodePopUpMenu'] = parameterNodePopUpMenu
        return parameterNodePopUpMenu
    }

    boolean isValid() {
        return abstractUIItem.item.valid
    }

    @Override
    public Font getFont(String fontName, int fontSize) {
        return new Font(fontName, !isValid() ? Font.ITALIC : Font.PLAIN, fontSize)
    }

    @Override
    public String getToolTip() {
        return abstractUIItem.item.status == Status.NONE ? String.valueOf("") : abstractUIItem.item.status.displayName
    }


}
