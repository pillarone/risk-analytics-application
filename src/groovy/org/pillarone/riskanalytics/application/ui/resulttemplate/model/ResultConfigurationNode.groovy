package org.pillarone.riskanalytics.application.ui.resulttemplate.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.view.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem

@CompileStatic
class ResultConfigurationNode extends ItemNode {

    static final String RESULT_CONFIGURATION_NODE_POP_UP_MENU = "resultConfigurationNodePopUpMenu"

    ResultConfigurationNode(ResultConfigurationUIItem resultConfigurationUIItem) {
        super(resultConfigurationUIItem, false)
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu resultConfigurationNodePopUpMenu = new ULCPopupMenu()
        resultConfigurationNodePopUpMenu.name = RESULT_CONFIGURATION_NODE_POP_UP_MENU
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, riskAnalyticsMainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, riskAnalyticsMainModel)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new EnabledCheckingMenuItem(new RenameAction(tree, riskAnalyticsMainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, riskAnalyticsMainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, riskAnalyticsMainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, riskAnalyticsMainModel)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, riskAnalyticsMainModel)))
        return resultConfigurationNodePopUpMenu
    }
}
