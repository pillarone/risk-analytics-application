package org.pillarone.riskanalytics.application.ui.resource.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.view.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResourceUIItem
import org.pillarone.riskanalytics.core.workflow.Status

@CompileStatic
class ResourceNode extends ItemNode {


    static final String RESOURCE_NODE_POP_UP_MENU = 'resourceNodePopUpMenu'

    ResourceNode(ResourceUIItem abstractUIItem) {
        super(abstractUIItem, false)
    }

    @Override
    ResourceUIItem getItemNodeUIItem() {
        return super.itemNodeUIItem as ResourceUIItem
    }

    Status getStatus() {
        itemNodeUIItem.item.status
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu parameterNodePopUpMenu = new ULCPopupMenu()
        parameterNodePopUpMenu.name = RESOURCE_NODE_POP_UP_MENU
        parameterNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree)))
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new TagsAction(tree)))
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new EnabledCheckingMenuItem(new RenameAction(tree)))
        parameterNodePopUpMenu.add(new EnabledCheckingMenuItem(new SaveAsAction(tree)))
        parameterNodePopUpMenu.add(new EnabledCheckingMenuItem(new CreateNewMajorVersion(tree)))
        if (UserContext.hasCurrentUser()) {
            def transactionsEnabled = Holders.grailsApplication.config.getProperty("transactionsEnabled")
            if (transactionsEnabled != null && transactionsEnabled) {
                parameterNodePopUpMenu.addSeparator()
                parameterNodePopUpMenu.add(new ULCMenuItem(new ChooseDealAction(tree)))
                parameterNodePopUpMenu.add(new ULCMenuItem(new StartWorkflowAction(tree)))
            }
        }
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree)))
        return parameterNodePopUpMenu
    }
}
