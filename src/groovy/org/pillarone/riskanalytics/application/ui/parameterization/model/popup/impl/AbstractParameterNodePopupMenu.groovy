package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.impl

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.view.CompareParameterizationMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.OpenExternalMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 */
abstract class AbstractParameterNodePopupMenu extends ULCPopupMenu {
    public AbstractParameterNodePopupMenu(ULCTableTree tree, ParameterizationNode node) {
        super();
        final AbstractUIItem uIItem = node.getAbstractUIItem()
        setName("parameterNodePopUpMenu");
        add(new ULCMenuItem(new OpenItemAction(tree, uIItem.mainModel)));
        add(new ULCMenuItem(new SimulationAction(tree, uIItem.mainModel)));
        //Add external link
        OpenExternalMenuItem openTransactionMenuItem = new OpenExternalMenuItem(new OpenTransactionLinkAction(tree, uIItem.mainModel))
        tree.addTreeSelectionListener(openTransactionMenuItem)
        add(openTransactionMenuItem)
        addSeparator();
        CompareParameterizationMenuItem compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, node.getAbstractUIItem().mainModel));
        tree.addTreeSelectionListener(compareParameterizationMenuItem);
        add(compareParameterizationMenuItem);
        add(new ULCMenuItem(new TagsAction(tree, uIItem.mainModel)));
        Boolean useSetFilterToSelectionPopupMenu = (Boolean) Holders.grailsApplication?.getConfig()?.getProperty("useSetFilterToSelectionPopupMenu");
        if (useSetFilterToSelectionPopupMenu != null && useSetFilterToSelectionPopupMenu) {
            add(new ULCMenuItem(new SetFilterToSelection(tree, uIItem.mainModel)));
        }
        addSeparator();
        if (hasRenameAction()) add(new ULCMenuItem(new RenameAction(tree, uIItem.mainModel)));
        add(new ULCMenuItem(new SaveAsAction(tree, uIItem.mainModel)));
        if (hasCreateNewMajorVersionAction()) add(new ULCMenuItem(new CreateNewMajorVersion(tree, uIItem.mainModel)));
        add(new ULCMenuItem(new ExportItemAction(tree, uIItem.mainModel)));
        add(new ULCMenuItem(new ImportParameterizationExcelAction(tree, uIItem.mainModel,'ImportFromExcelAdditional')));

        addSeparator();
        boolean separatorNeeded = addMenuItemsForWorkflowState(tree, node);

        node.addReportMenus(this, tree, separatorNeeded);
        if (hasDeleteAction()) {
            addSeparator();
            add(new ULCMenuItem(getDeleteAction(tree, uIItem)));
        }
    }

    protected DeleteAction getDeleteAction(ULCTableTree tree, AbstractUIItem uIItem) {
        new DeleteAction(tree, uIItem.mainModel)
    }

    protected abstract boolean hasRenameAction();
    protected abstract boolean hasCreateNewMajorVersionAction();
    protected abstract boolean hasDeleteAction();

    /**
     * Add menu items specific for this state
     * @param tree
     * @return true if menu items were added (and a trailing menu separator is required)
     */
    protected abstract boolean addMenuItemsForWorkflowState(ULCTableTree tree, ParameterizationNode node);
}
