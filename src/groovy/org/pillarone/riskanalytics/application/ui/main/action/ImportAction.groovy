package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tree.ITreeNode
import com.ulcjava.base.application.util.Cursor
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileLoadHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.UserPreferences
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportAction extends SelectionTreeAction {
    UserPreferences userPreferences
    ULCWindow ancestor
    boolean forceImport = false
    Log LOG = LogFactory.getLog(ImportAction)

    public ImportAction(ULCTree tree, P1RATModel model) {
        super("Import", tree, model)
        userPreferences = new UserPreferences()
        ancestor = getAncestor()
    }

    public ImportAction(ULCTree tree, P1RATModel model, boolean forceImport) {
        super(forceImport ? "forceImport" : "Import", tree, model)
        this.forceImport = forceImport
        userPreferences = new UserPreferences()
        ancestor = getAncestor()
    }


    public ImportAction(ULCTree tree, P1RATModel model, String actionName) {
        super(actionName, tree, model)
        userPreferences = new UserPreferences()
        ancestor = getAncestor()
    }

    public ImportAction(String title) {
        super(title)
        userPreferences = new UserPreferences()
        ancestor = getAncestor()
    }

    public void doActionPerformed(ActionEvent event) {
        doAction(tree.selectionPath.lastPathComponent)
    }

    protected doAction(ITreeNode node) {
        new I18NAlert("FunctionNotImplemented").show()
    }

    protected doAction(ItemGroupNode node) {

        if (Simulation == node.itemClass) {
            doAction(node as ITreeNode)
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
                    String selectedFile = filePaths[0]
                    ItemLoadHandler handler = new ItemLoadHandler(this, node)
                    handler.forceImport = this.forceImport
                    ClientContext.loadFile(handler, selectedFile)
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
        def ext = new String[1]
        ext[0] = "groovy"
        config.addFileFilterConfig(new FileChooserConfig.FileFilterConfig(ext, "description (*.groovy)"));
        config.setAcceptAllFileFilterUsed(false)
        return config
    }

    protected GString getDialogTitle(node) {
        return "Import ${node != null ? node.userObject : ''}"
    }

    ULCWindow getAncestor() {
        return UlcUtilities.getWindowAncestor(tree)
    }


}