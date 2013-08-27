package org.pillarone.riskanalytics.application.ui.resource.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import grails.util.Holders
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.view.item.ResourceUIItem
import org.pillarone.riskanalytics.core.workflow.Status

class ResourceNode extends VersionedItemNode {

    ResourceNode(ResourceUIItem abstractUIItem) {
        super(abstractUIItem, false)
    }

    public Status getStatus() {
        abstractUIItem.item.status
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu parameterNodePopUpMenu = new ULCPopupMenu()
        parameterNodePopUpMenu.name = "resourceNodePopUpMenu"
        parameterNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new TagsAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, abstractUIItem.mainModel)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, abstractUIItem.mainModel)))
        if (UserContext.hasCurrentUser()) {
            def transactionsEnabled = Holders.grailsApplication.getConfig().getProperty("transactionsEnabled")
            if (transactionsEnabled != null && transactionsEnabled) {
                parameterNodePopUpMenu.addSeparator()
                parameterNodePopUpMenu.add(new ULCMenuItem(new ChooseDealAction(tree, abstractUIItem.mainModel)))
                parameterNodePopUpMenu.add(new ULCMenuItem(new StartWorkflowAction(tree, abstractUIItem.mainModel)))
            }
        }
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, abstractUIItem.mainModel)))
        return parameterNodePopUpMenu
    }


}
