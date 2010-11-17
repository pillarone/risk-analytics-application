package org.pillarone.riskanalytics.application.ui.main.action

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.pillarone.riskanalytics.core.model.Model

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.Cursor
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileLoadHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportAllAction extends ImportAction {
    P1RATMainView p1RATMainView
    Log LOG = LogFactory.getLog(ImportAllAction)

    public ImportAllAction(ULCTableTree tree, P1RATModel model, String actionName) {
        super(tree, model, actionName)
    }

    public ImportAllAction(P1RATMainView p1RATMainView, P1RATModel model, String actionName) {
        super(actionName)
        this.p1RATMainView = p1RATMainView
        this.model = model
        ancestor = getAncestor()
    }

    public void doActionPerformed(ActionEvent event) {
        importItems()
    }

    protected void importItems() {
        ancestor?.cursor = Cursor.WAIT_CURSOR

        FileChooserConfig config = getFileChooserConfig(null)
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.IMPORT_DIR_KEY))

        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedPath = filePaths[0]
                    File dir = new File(selectedPath)
                    loadFile(dir)
                    dir.eachDir {File modelName ->
                        String modelDir = selectedPath + "/" + modelName.name
                        File modelFile = new File(modelDir)
                        loadFile(modelFile)
                    }
                },
                onFailure: {reason, description ->
                    if (IFileLoadHandler.CANCELLED != reason) {
                        new ULCAlert(ancestor, "Import failed", description, "Ok").show()
                    }
                    ancestor?.cursor = Cursor.DEFAULT_CURSOR
                }] as IFileChooseHandler, config, ancestor)
    }

    protected void loadFile(File modelFile) {
        modelFile.eachFile {File selectedFile ->
            if (selectedFile.name.endsWith(".groovy")) {
                LOG.info "importing a parameterization   $selectedFile.name ..."
                synchronized (this.getClass()) {
                    ClientContext.loadFile(new ItemLoadHandler(this, null, true), modelFile.path + "/" + selectedFile.name)
                }
            }
        }
    }


    protected FileChooserConfig getFileChooserConfig(Object node) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = getDialogTitle(node)
        config.dialogType = FileChooserConfig.OPEN_DIALOG
        config.setFileSelectionMode(FileChooserConfig.DIRECTORIES_ONLY)
        return config
    }

    Model getSelectedModel() {
        return null
    }

    ULCWindow getAncestor() {
        return UlcUtilities.getWindowAncestor(p1RATMainView?.content)
    }


}
