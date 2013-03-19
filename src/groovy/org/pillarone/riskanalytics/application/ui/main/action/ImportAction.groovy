package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.Cursor
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileLoadHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportAction extends SelectionTreeAction {
    protected UserPreferences userPreferences
    ULCWindow ancestor
    boolean forceImport = false
    Log LOG = LogFactory.getLog(ImportAction)

    public ImportAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        this(tree, model, "Import")
    }

    public ImportAction(ULCTableTree tree, RiskAnalyticsMainModel model, boolean forceImport) {
        super(forceImport ? "forceImport" : "Import", tree, model)
        this.forceImport = forceImport
        userPreferences = UserPreferencesFactory.getUserPreferences()
        ancestor = getAncestor()
    }


    public ImportAction(ULCTableTree tree, RiskAnalyticsMainModel model, String actionName) {
        super(actionName, tree, model)
        userPreferences = UserPreferencesFactory.getUserPreferences()
        ancestor = getAncestor()
    }

    public ImportAction(String title) {
        super(title)
        userPreferences = UserPreferencesFactory.getUserPreferences()
        ancestor = getAncestor()
    }

    public void doActionPerformed(ActionEvent event) {
        doAction(tree.selectedPath.lastPathComponent)
    }

    protected doAction(ITableTreeNode node) {
        //do nothing when not on an ItemGroupNode
    }

    protected doAction(ItemGroupNode node) {

        if (Simulation == node.itemClass) {
            doAction(node as ITableTreeNode)
            return
        }
        importItem(node)
    }

    protected void importItem(def node) {
        ancestor?.cursor = Cursor.WAIT_CURSOR

        FileChooserConfig config = getFileChooserConfig(node)
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.IMPORT_DIR_KEY))

        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    filePaths?.each {def selectedFile ->
                        ItemLoadHandler handler = new ItemLoadHandler(this, node)
                        handler.forceImport = this.forceImport
                        ClientContext.loadFile(handler, selectedFile)
                    }
                },
                onFailure: {reason, description ->
                    if (IFileLoadHandler.CANCELLED != reason) {
                        LOG.error description
                        ULCAlert alert = new I18NAlert(ancestor, "importError")
                        alert.show()
                    }
                    ancestor?.cursor = Cursor.DEFAULT_CURSOR
                }] as IFileChooseHandler, config, ancestor)
    }


    protected FileChooserConfig getFileChooserConfig(node) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = getDialogTitle(node)
        config.dialogType = FileChooserConfig.OPEN_DIALOG
        config.setFileSelectionMode(FileChooserConfig.FILES_ONLY)
        config.setMultiSelectionEnabled(true)
        def ext = new String[1]
        ext[0] = "groovy"
        config.addFileFilterConfig(new FileChooserConfig.FileFilterConfig(ext, "description (*.groovy)"));
        config.setAcceptAllFileFilterUsed(false)
        return config
    }

    protected String getDialogTitle(node) {
        return "Import"
    }

    ULCWindow getAncestor() {
        return UlcUtilities.getWindowAncestor(tree)
    }


}
