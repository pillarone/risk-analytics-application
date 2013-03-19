package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.main.action.*

class ItemGroupNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {
    Class itemClass
    RiskAnalyticsMainModel mainModel

    public ItemGroupNode(String name, Class itemClass) {
        super([name] as Object[])
        this.itemClass = itemClass
    }

    public ItemGroupNode(String name, Class itemClass, RiskAnalyticsMainModel mainModel) {
        this(name, itemClass)
        this.mainModel = mainModel
    }

    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        switch (itemClass) {
            case Simulation: return getSimulationGroupNodePopUpMenu(tree)
            case ResultConfiguration: return getGroupNodePopUpMenu(tree)
            case Parameterization: return getParameterGroupNodePopUpMenu(tree)
        }
    }

    private ULCPopupMenu getGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu groupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.isStandAlone())
            groupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', true)))
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, false)))
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, true)))
        groupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, mainModel)))
        groupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultResultConfigurationAction(tree, mainModel)))
        groupNodePopUpMenu.addSeparator()
        groupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, mainModel, "DeleteAllResultTemplates")))
        return groupNodePopUpMenu
    }

    private ULCPopupMenu getSimulationGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu simulationGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.isStandAlone())
            simulationGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', false)))
        simulationGroupNodePopUpMenu.addSeparator()
        simulationGroupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, mainModel, "DeleteAllSimulations")))
        return simulationGroupNodePopUpMenu
    }

    private ULCPopupMenu getParameterGroupNodePopUpMenu(ULCTableTree tree) {
        ULCPopupMenu parameterGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.isStandAlone()) {
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', false)))
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, mainModel, 'ExportAll', true)))
        }
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, false)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, mainModel, true)))
        if (UserContext.isStandAlone())
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAllAction(tree, mainModel, "importAllFromDir")))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, mainModel)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultParameterizationAction(tree, mainModel)))
        parameterGroupNodePopUpMenu.addSeparator()
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, mainModel, "DeleteAllParameters")))
        return parameterGroupNodePopUpMenu
    }

    public ULCIcon getIcon() {
        switch (itemClass) {
            case Simulation: return UIUtils.getIcon("results-active.png")
            case ResultConfiguration: return UIUtils.getIcon("resulttemplate-active.png")
            case Parameterization: return UIUtils.getIcon("parametrization-active.png")
        }
        return null
    }

    public Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }

    public String getToolTip() {
        return ""
    }

    public String getName() {
        return ""
    }


}
