package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class OpenItemDialog {
    private ULCWindow parent
    ULCTableTree tree
    Model model
    P1RATModel p1RATModel
    ULCDialog dialog
    ModellingItem item

    private ULCButton createCopyButton
    private ULCButton readOnlyButton
    private ULCButton deleteDependingResultsButton
    private ULCButton cancelButton
    private Dimension buttonDimension = new Dimension(160, 20)

    Closure closeAction = {event -> dialog.visible = false; dialog.dispose()}


    public OpenItemDialog(ULCTableTree tree, Model model, P1RATModel p1RATModel, ModellingItem item) {
        this.tree = tree
        this.model = model
        this.item = item
        this.p1RATModel = p1RATModel
    }

    public void init() {
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        if (tree)
            this.parent = UlcUtilities.getWindowAncestor(tree)
        dialog = new ULCDialog(parent, UIUtils.getText(this.class, "title"), true)
        dialog.name = 'OpenItemDialog'
        createCopyButton = new ULCButton(UIUtils.getText(this.class, "createCopy"))
        createCopyButton.setPreferredSize(buttonDimension)
        createCopyButton.name = "OpenItemDialog.createCopy"
        readOnlyButton = new ULCButton(UIUtils.getText(this.class, "OpenReadOnly"))
        readOnlyButton.setPreferredSize(buttonDimension)
        readOnlyButton.name = "OpenItemDialog.readOnly"
        deleteDependingResultsButton = new ULCButton(UIUtils.getText(this.class, "DeleteDependingResults"))
        deleteDependingResultsButton.name = "OpenItemDialog.deleteDependingResultsButton"
        deleteDependingResultsButton.setPreferredSize(new Dimension(200, 20))
        cancelButton = new ULCButton(UIUtils.getText(this.class, "cancel"))
        cancelButton.setPreferredSize(buttonDimension)
        cancelButton.name = "OpenItemDialog.cancelButton"
    }

    private void layoutComponents() {
        ULCBoxPane content = new ULCBoxPane(rows: 1, columns: isWorkflowItem() ? 3 : 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        if (!isWorkflowItem()) {
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, createCopyButton)
        }
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, readOnlyButton)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, deleteDependingResultsButton)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, cancelButton)

        dialog.add(content)
        dialog.setLocationRelativeTo(parent)
        dialog.pack()
        dialog.resizable = false
    }

    private void attachListeners() {
        createCopyButton.addActionListener([actionPerformed: {ActionEvent event ->
            closeAction.call()
            ModellingItem modellingItem
            if (!item.isLoaded()) {
                item.load()
            }
            item.daoClass.withTransaction {status ->
                if (!item.isLoaded())
                    item.load()
                modellingItem = ModellingItemFactory.incrementVersion(item)
                p1RATModel.fireModelChanged()
                p1RATModel.selectionTreeModel.addNodeForItem(modellingItem)
            }
            p1RATModel.openItem(model, modellingItem)
        }] as IActionListener)

        readOnlyButton.addActionListener([actionPerformed: {ActionEvent event ->
            closeAction.call()
            if (!item.isLoaded())
                item.load()
            p1RATModel.notifyOpenDetailView(model, item)
        }] as IActionListener)

        deleteDependingResultsButton.addActionListener([actionPerformed: {ActionEvent event ->
            closeAction.call()
            p1RATModel.deleteDependingResults(model, item)
            p1RATModel.openItem(model, item)
        }] as IActionListener)

        cancelButton.addActionListener([actionPerformed: {ActionEvent event ->
            closeAction.call()
        }] as IActionListener)
    }

    public void setVisible(boolean visible) {
        dialog.setVisible(visible)
    }

    private boolean isWorkflowItem() {
        if (item instanceof Parameterization || item instanceof ResultConfiguration) {
            return item.versionNumber.workflow
        }

        return false
    }
}
