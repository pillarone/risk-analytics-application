package org.pillarone.riskanalytics.application.ui.parameterization.model.popup.impl

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import grails.util.Holders
import org.apache.commons.lang.BooleanUtils
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.view.CompareParameterizationMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.OpenExternalMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode

/**
 * Allianz Risk Transfer  ATOM
 * User: bzetterstrom
 *
 * frahman 2014-01-02: Looks like this class builds (in its ctor) the context menu on a parameterization node.
 */
abstract class AbstractParameterNodePopupMenu extends ULCPopupMenu {
    public AbstractParameterNodePopupMenu(ULCTableTree tree, ParameterizationNode node) {
        super();
        final RiskAnalyticsMainModel mainModel = node.getAbstractUIItem().mainModel
        setName("parameterNodePopUpMenu");
        add(new ULCMenuItem(new OpenItemAction(tree, mainModel)));
        add(new ULCMenuItem(new SimulationAction(tree, mainModel)));

        //External link
        OpenExternalMenuItem openTx = new OpenExternalMenuItem(new OpenTransactionLinkAction(tree, mainModel))
        tree.addTreeSelectionListener(openTx)
        add(openTx)

        addSeparator();

        CompareParameterizationMenuItem compPns = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, mainModel));
        tree.addTreeSelectionListener(compPns);
        add(compPns);

        add(new ULCMenuItem(new TagsAction(tree, mainModel)));

        if( Boolean.valueOf(Holders.grailsApplication?.getConfig()?.getProperty("useSetFilterToSelectionPopupMenu")) ) {
            add(new ULCMenuItem(new SetFilterToSelection(tree, mainModel)));
        }
        addSeparator();
        if (hasRenameAction()) add(new ULCMenuItem(new RenameAction(tree, mainModel)));
        add(new ULCMenuItem(new SaveAsAction(tree, mainModel)));
        if (hasCreateNewMajorVersionAction()) add(new ULCMenuItem(new CreateNewMajorVersion(tree, mainModel)));
        add(new ULCMenuItem(new ExportItemAction(tree, mainModel)));
        add(new ULCMenuItem(new ImportParameterizationExcelAction(tree, mainModel,'ImportFromExcelAdditional')));
        addSeparator();

        //Concrete subclasses add menus appropriate to current state.
        //Eg the InReviewPopupMenu adds menu items for send to production and reject workflow.
        //(Like a 'statemachine' encoded in class diagram, fixed at compile time)
        boolean separatorNeeded = addMenuItemsForWorkflowState(tree, node);

        node.addReportMenus(this, tree, separatorNeeded);
        if (hasDeleteAction()) {
            addSeparator();
            add(new ULCMenuItem(new DeleteAction(tree, mainModel)));
        }
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
