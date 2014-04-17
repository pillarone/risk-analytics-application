package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

@CompileStatic
class ItemGroupNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {
    Class itemClass
    RiskAnalyticsMainModel mainModel

    final String toolTip = ''
    final String name = ''

    ItemGroupNode(String name, Class itemClass) {
        super([name] as Object[])
        this.itemClass = itemClass
    }

    ItemGroupNode(String name, Class itemClass, RiskAnalyticsMainModel mainModel) {
        this(name, itemClass)
        this.mainModel = mainModel
    }

    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        switch (itemClass) {
            case Simulation: return getSimulationGroupNodePopUpMenu(tree)
            case ResultConfiguration: return getGroupNodePopUpMenu(tree)
            case Parameterization: return getParameterGroupNodePopUpMenu(tree)
        }
    }

    private ULCPopupMenu getGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu groupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.standAlone) {
            groupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', true)))
        }
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, false)))
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, true)))
        groupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, mainModel)))
        groupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultResultConfigurationAction(tree, mainModel)))
        return groupNodePopUpMenu
    }

    private ULCPopupMenu getSimulationGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu simulationGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.standAlone) {
            simulationGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', false)))
        }
        return simulationGroupNodePopUpMenu
    }

    private ULCPopupMenu getParameterGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu parameterGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.standAlone) {
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', false)))
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', true)))
        }
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, false)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, true)))
        if (UserContext.standAlone) {
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAllAction(tree, mainModel, "importAllFromDir")))
        }
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, mainModel)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultParameterizationAction(tree, mainModel)))
        parameterGroupNodePopUpMenu.addSeparator()
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportParameterizationExcelAction(tree, mainModel, 'ImportFromExcel')))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportParameterizationExcelAction(tree, mainModel, 'ExportToExcel')))
        return parameterGroupNodePopUpMenu
    }

    ULCIcon getIcon() {
        switch (itemClass) {
            case Simulation: return UIUtils.getIcon("results-active.png")
            case ResultConfiguration: return UIUtils.getIcon("resulttemplate-active.png")
            case Parameterization: return UIUtils.getIcon("parametrization-active.png")
        }
        return null
    }

    Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }
}
