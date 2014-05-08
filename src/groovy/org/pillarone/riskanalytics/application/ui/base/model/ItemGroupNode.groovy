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
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

@CompileStatic
class ItemGroupNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {
    Class itemClass

    final String toolTip = ''
    final String name = ''

    ItemGroupNode(String name, Class itemClass) {
        super([name] as Object[])
        this.itemClass = itemClass
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
            groupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, 'ExportAll', true)))
        }
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, false)))
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, true)))
        groupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree)))
        groupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultResultConfigurationAction(tree)))
        return groupNodePopUpMenu
    }

    private ULCPopupMenu getSimulationGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu simulationGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.standAlone) {
            simulationGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, 'ExportAll', false)))
        }
        return simulationGroupNodePopUpMenu
    }

    private ULCPopupMenu getParameterGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu parameterGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.standAlone) {
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, 'ExportAll', false)))
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, 'ExportAll', true)))
        }
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, false)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, true)))
        if (UserContext.standAlone) {
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAllAction(tree, "importAllFromDir")))
        }
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultParameterizationAction(tree)))
        parameterGroupNodePopUpMenu.addSeparator()
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportParameterizationExcelAction(tree, 'ImportFromExcel')))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportParameterizationExcelAction(tree, 'ExportToExcel')))
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
