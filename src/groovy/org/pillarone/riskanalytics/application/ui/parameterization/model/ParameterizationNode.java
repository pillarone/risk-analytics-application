package org.pillarone.riskanalytics.application.ui.parameterization.model;

import com.ulcjava.base.application.ULCMenuItem;
import com.ulcjava.base.application.ULCPopupMenu;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.util.Font;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import org.pillarone.riskanalytics.application.UserContext;
import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode;
import org.pillarone.riskanalytics.application.ui.main.action.*;
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction;
import org.pillarone.riskanalytics.application.ui.main.view.CompareParameterizationMenuItem;
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem;
import org.pillarone.riskanalytics.core.simulation.item.Parameterization;
import org.pillarone.riskanalytics.core.workflow.Status;

public class ParameterizationNode extends VersionedItemNode {


    public ParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem, false);
    }

    @Override
    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        return new AbstractParameterNodePopupMenu(tree) {
            protected boolean hasRenameAction() { return true; }
            protected boolean hasCreateNewMajorVersionAction() { return true; }
            protected boolean addMenuItemsForWorkflowState(ULCTableTree tree) {
                if (UserContext.hasCurrentUser()) {
                    Boolean transactionsEnabled = (Boolean)ApplicationHolder.getApplication().getConfig().getProperty("transactionsEnabled");
                    if (transactionsEnabled != null && transactionsEnabled) {
                        add(new ULCMenuItem(new ChooseDealAction(tree, getAbstractUIItem().mainModel)));
                        add(new ULCMenuItem(new StartWorkflowAction(tree, getAbstractUIItem().mainModel)));
                        return true;
                    }
                }
                return false;
            }
            protected boolean hasDeleteAction() { return true; }
        };
    }

    private boolean isValid() {
        return getParameterization().isValid();
    }

    public Status getStatus() {
        return getParameterization().getStatus();
    }

    private Parameterization getParameterization() {
        return ((Parameterization)getAbstractUIItem().getItem());
    }

    @Override
    public Font getFont(String fontName, int fontSize) {
        return new Font(fontName, !isValid() ? Font.ITALIC : Font.PLAIN, fontSize);
    }

    @Override
    public String getToolTip() {
        return getParameterization().getStatus() == Status.NONE
                ? String.valueOf("")
                : getParameterization().getStatus().getDisplayName();
    }

    protected abstract class AbstractParameterNodePopupMenu extends ULCPopupMenu {
        public AbstractParameterNodePopupMenu(ULCTableTree tree) {
            super();
            setName("parameterNodePopUpMenu");
            add(new ULCMenuItem(new OpenItemAction(tree, getAbstractUIItem().mainModel)));
            add(new ULCMenuItem(new SimulationAction(tree, getAbstractUIItem().mainModel)));
            addSeparator();
            CompareParameterizationMenuItem compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, getAbstractUIItem().mainModel));
            tree.addTreeSelectionListener(compareParameterizationMenuItem);
            add(compareParameterizationMenuItem);
            add(new ULCMenuItem(new TagsAction(tree, getAbstractUIItem().mainModel)));
            Boolean useSetFilterToSelectionPopupMenu = (Boolean) ApplicationHolder.getApplication().getConfig().getProperty("useSetFilterToSelectionPopupMenu");
            if (useSetFilterToSelectionPopupMenu != null && useSetFilterToSelectionPopupMenu) {
                add(new ULCMenuItem(new SetFilterToSelection(tree, getAbstractUIItem().mainModel)));
            }
            addSeparator();
            if (hasRenameAction()) add(new ULCMenuItem(new RenameAction(tree, getAbstractUIItem().mainModel)));
            add(new ULCMenuItem(new SaveAsAction(tree, getAbstractUIItem().mainModel)));
            if (hasCreateNewMajorVersionAction()) add(new ULCMenuItem(new CreateNewMajorVersion(tree, getAbstractUIItem().mainModel)));
            add(new ULCMenuItem(new ExportItemAction(tree, getAbstractUIItem().mainModel)));

            addSeparator();
            boolean separatorNeeded = addMenuItemsForWorkflowState(tree);

            addReportMenus(this, tree, separatorNeeded);
            if (hasDeleteAction()) {
                addSeparator();
                add(new ULCMenuItem(new DeleteAction(tree, getAbstractUIItem().mainModel)));
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
        protected abstract boolean addMenuItemsForWorkflowState(ULCTableTree tree);
    }
}
