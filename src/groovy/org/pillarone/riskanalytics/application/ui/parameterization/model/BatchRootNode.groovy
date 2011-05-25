package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.model.NavigationTreeNode
import org.pillarone.riskanalytics.application.ui.batch.action.NewBatchAction
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchRootNode extends DefaultMutableTableTreeNode implements NavigationTreeNode {
    RiskAnalyticsMainModel mainModel

    public BatchRootNode(String name, RiskAnalyticsMainModel mainModel) {
        super([name] as Object[]);
        this.mainModel = mainModel
    }

    public ULCPopupMenu getPopupMenu(MainSelectionTableTreeCellRenderer renderer, ULCTableTree tree) {
        if (renderer.popupMenus['batchesRootNodePopUpMenu']) return renderer.popupMenus['batchesRootNodePopUpMenu']
        ULCPopupMenu batchesRootNodePopUpMenu = new ULCPopupMenu()
        batchesRootNodePopUpMenu.add(new ULCMenuItem(new NewBatchAction(tree, mainModel)))
        renderer.popupMenus['batchesRootNodePopUpMenu'] = batchesRootNodePopUpMenu
        return batchesRootNodePopUpMenu
    }

    public ULCIcon getIcon() {
        return null
    }

    public Font getFont(String fontName, int fontSize) {
        return new Font(fontName, Font.PLAIN, fontSize)
    }

    public String getToolTip() {
        return ""
    }
}
