package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.tree.ITreeNode
import com.ulcjava.base.application.util.Cursor
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileLoadHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import java.text.SimpleDateFormat
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.base.model.ModelNode
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.DefaultParameterizationDialog
import org.pillarone.riskanalytics.application.ui.main.view.NodeNameDialog
import org.pillarone.riskanalytics.application.ui.util.ExcelExporter
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.UserPreferences
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.util.IConfigObjectWriter
import org.springframework.transaction.TransactionStatus
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.core.simulation.item.*

abstract class SelectionTreeAction extends ResourceBasedAction {

    ULCTree tree
    P1RATModel model

    def SelectionTreeAction(name, tree, P1RATModel model) {
        super(name);
        this.tree = tree;
        this.model = model
        checkForIcon()
    }

    public SelectionTreeAction(String title) {
        super(title);
        checkForIcon()
    }

    private checkForIcon() {
        if (getValue(IAction.SMALL_ICON) == null) {
            putValue(IAction.SMALL_ICON, UIUtils.getIcon("clear.png"));
        }
    }

    Object getSelectedItem() {
        ITreeNode itemNode = tree.selectionPath?.lastPathComponent
        return itemNode instanceof ItemNode ? itemNode.item : null
    }

    List getSelectedObjects(Class itemClass) {
        List selectedObjects = []
        tree.selectionPaths.each {Object selectedPath ->
            selectedPath.getPath().each {obj ->
                if (obj instanceof ItemGroupNode) {
                    try {
                        if (obj.itemClass == itemClass && selectedPath?.lastPathComponent != null) {

                            selectedObjects.add(selectedPath.lastPathComponent)
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return selectedObjects
    }

    Model getSelectedModel() {
        ITreeNode itemNode = tree?.selectionPath?.lastPathComponent
        return getSelectedModel(itemNode)
    }

    Model getSelectedModel(ITreeNode itemNode) {
        ITreeNode modelNode = null
        while (modelNode == null) {
            if (itemNode instanceof ModelNode) {
                modelNode = itemNode
            } else {
                itemNode = itemNode?.parent
            }
        }
        return modelNode?.item
    }

    Class getSelectedItemGroupClass() {
        return getSelectedItemGroupNode().itemClass
    }

    ItemGroupNode getSelectedItemGroupNode() {
        ITreeNode itemNode = tree.selectionPath.lastPathComponent
        ITreeNode groupNode = null
        while (groupNode == null) {
            if (itemNode instanceof ItemGroupNode) {
                groupNode = itemNode
            } else {
                itemNode = itemNode.parent
            }
        }
        return groupNode
    }
}

class OpenItemAction extends SelectionTreeAction {

    def OpenItemAction(ULCTree tree, P1RATModel model) {
        super("Open", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        Model model = getSelectedModel()
        def item = getSelectedItem()
        if (model != null && item != null) {
            this.model.openItem(model, item)
        }
    }

}
class RenameAction extends SelectionTreeAction {

    public RenameAction(ULCTree tree, P1RATModel model) {
        super("Rename", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        /* For opening the cellEditor implement a extension and call startEditingPath on clientSide (remember to convert the TreePath)
        ULCTreeModelAdapter adapter = ULCSession.currentSession().getModelAdapterProvider().getModelAdapter(ITreeModel.class, tree.model)
        tree.invokeUI("startEditingAtPath", [adapter.getDescriptionForPath(tree.getSelectionPath())] as Object[])
        */
        boolean usedInSimulation = false
        def selectedItem = getSelectedItem()
        if (selectedItem instanceof Parameterization || selectedItem instanceof ResultConfiguration) {
            selectedItem.setModelClass(getSelectedModel().class)
            usedInSimulation = selectedItem.isUsedInSimulation()
        }
        if (!usedInSimulation) {
            NodeNameDialog dialog = new NodeNameDialog(UlcUtilities.getWindowAncestor(tree), selectedItem)
            dialog.title = dialog.getText("renameTitle") + " " + selectedItem.name

            dialog.okAction = { model.renameItem(selectedItem, dialog.nameInput.text) }
            dialog.show()
        } else {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "RenamingLocked")
            alert.show()
        }
    }

}

class CompareSimulationsAction extends SelectionTreeAction {

    public CompareSimulationsAction(ULCTree tree, P1RATModel model) {
        super("CompareSimulations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Simulation.class)
        try {
            validate(elements)
            Model model = getSelectedModel(elements[0])
            if (model != null && elements[0].item != null) {
                this.model.compareItems(model, elements)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

    private void validate(List elements) throws IllegalArgumentException, Exception {
        if (elements.size() < 2) throw new IllegalArgumentException("select at lease two simulations for compare")
        Class modelClass = elements[0].item.modelClass
        elements.each {
            if (it.item.modelClass != modelClass) {
                throw new IllegalArgumentException("select a simulations with same ModelClass")
            }
        }
    }

    public boolean isEnabled() {
        List elements = getSelectedObjects(Simulation.class)
        try {
            validate(elements)
        } catch (IllegalArgumentException ex) {
            return false
        }
        return true
    }

}

class CompareParameterizationsAction extends SelectionTreeAction {

    public CompareParameterizationsAction(ULCTree tree, P1RATModel model) {
        super("CompareParameterizations", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        List elements = getSelectedObjects(Parameterization.class)
        try {
            validate(elements)
            Model model = getSelectedModel(elements[0])
            if (model != null && elements[0] != null) {
                this.model.compareParameterizations(model, elements)
            }
        } catch (IllegalArgumentException ex) {
            println "$ex"
        }
    }

    private void validate(List elements) throws IllegalArgumentException {
        if (elements.size() < 2) throw new IllegalArgumentException("select at lease two parameterizations for compare")
        Model model = getSelectedModel(elements[0])
        elements.each {
            if (getSelectedModel(it) != model) {
                throw new IllegalArgumentException("select a parameterizations with same ModelClass")
            }
        }
    }

    public boolean isEnabled() {
        List elements = getSelectedObjects(Parameterization.class)
        try {
            validate(elements)
        } catch (IllegalArgumentException ex) {
            return false
        }
        return true
    }

}



abstract class DeleteAllAction extends SelectionTreeAction {

    public DeleteAllAction(ULCTree tree, P1RATModel model) {
        super("DeleteAll", tree, model)
    }

    protected void deleteParameterizations(List items) {
        boolean usedInSimulation = false
        //Parameterization
        for (Parameterization parameterization: items) {
            usedInSimulation = parameterization.isUsedInSimulation()
            if (usedInSimulation == true)
                break
        }
        if (!usedInSimulation) {
            model.removeItems(getSelectedModel(), getSelectedItemGroupNode(), items)
        } else {
            ULCAlert alert = new I18NAlert("DeleteAllError")
            alert.show()
        }
    }

    protected void deleteResultConfigurations(List items) {
        //ResultTemplate
        boolean usedInSimulation = false
        for (ResultConfiguration resultConfiguration: items) {
            usedInSimulation = resultConfiguration.isUsedInSimulation()
            if (usedInSimulation == true)
                break
        }
        if (!usedInSimulation) {
            model.removeItems(getSelectedModel(), getSelectedItemGroupNode(), items)
        } else {
            ULCAlert alert = new I18NAlert("DeleteAllError")
            alert.show()
        }
    }

    protected void deleteResults(List items) {
        model.removeItems(getSelectedModel(), getSelectedItemGroupNode(), items)
    }

    private boolean isInList(List<Simulation> simulations, Parameterization parameterization) {
        if (simulations == null || simulations.size() == 0) return false
        def result = simulations.any {
            it.parameterization == parameterization
        }
        return result
    }


    private void removeAllChildren(Model selectedModel, ModellingItem selectedItem) {
        model.selectionTreeModel.removeAllNodeForItem(selectedItem)
        model.fireModelChanged()
    }

}

abstract class ExportAction extends SelectionTreeAction {
    UserPreferences userPreferences
    Log LOG = LogFactory.getLog(ExportAction)

    public ExportAction(ULCTree tree, P1RATModel model, String title) {
        super(title, tree, model)
        userPreferences = new UserPreferences()
    }

    public ExportAction(String title) {
        super(title)
        userPreferences = new UserPreferences()
    }

    protected void exportSimulations(List items) {
        int itemCount = items.size()
        ULCWindow ancestor = getAncestor()
        if (!validate(items)) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "resultExcelExportError")
            alert.show()
        } else {
            FileChooserConfig config = new FileChooserConfig()
            config.dialogTitle = "Excel Export"
            config.dialogType = FileChooserConfig.SAVE_DIALOG
            config.fileSelectionMode = itemCount > 1 ? FileChooserConfig.DIRECTORIES_ONLY : FileChooserConfig.FILES_ONLY
            config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.EXPORT_DIR_KEY))
            if (items.size() == 1) {
                config.selectedFile = "${items[0].name}.xls".replaceAll(':', '-')
            }


            ClientContext.chooseFile([
                    onSuccess: {filePaths, fileNames ->
                        userPreferences.setUserDirectory(UserPreferences.EXPORT_DIR_KEY, filePaths[0])
                        ExcelExporter exporter = new ExcelExporter()
                        items.each {Simulation item ->

                            SingleValueResult.withTransaction {trx ->
                                def simulationFileName = "${item.name}.xls".replaceAll(':', '-')
                                String selectedFile = itemCount > 1 ? "${filePaths[0]}/$simulationFileName" : filePaths[0]
                                item.load()
                                def simulationRun = item.simulationRun
                                List rawData = ResultAccessor.getRawData(simulationRun)
                                ClientContext.storeFile([prepareFile: {OutputStream stream ->
                                    try {
                                        exporter.exportResults rawData
                                        exporter.addTab("Simulation settings", getSimulationSettings(simulationRun))
                                        exporter.writeWorkBook stream
                                    } catch (Throwable t) {
                                        new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                                    } finally {
                                        stream.close()
                                    }
                                }, onSuccess: {path, name ->
                                }, onFailure: {reason, description ->
                                    new ULCAlert(ancestor, "Export failed", description, "Ok").show()
                                }] as IFileStoreHandler, selectedFile)
                            }

                        }
                    },
                    onFailure: {reason, description ->
                    }] as IFileChooseHandler, config, ancestor)
        }

    }

    ULCWindow getAncestor() {
        ULCWindow ancestor = UlcUtilities.getWindowAncestor(tree)
        return ancestor
    }

    protected List<List<String>> getSimulationSettings(SimulationRun simulationRun) {
        SimulationRun.withTransaction {status ->
            simulationRun = SimulationRun.get(simulationRun.id)
            Parameterization parameterization = ModellingItemFactory.getParameterization(simulationRun?.parameterization)
            Class modelClass = parameterization.modelClass
            Simulation simulation = ModellingItemFactory.getSimulation(simulationRun?.name, modelClass)
            simulation.load()

            List data = []
            data << ["", "", "Version"]
            data << ["Simulation Name:", "$simulation.name"]
            data << ["Comment:", simulation.comment]
            data << ["Model:", "${simulation.modelClass.name}", "${simulation.modelVersionNumber}"]
            data << ["Parameterization:", "$simulation.parameterization.name", "${simulation.parameterization.versionNumber.toString()}"]
            data << ["Template:", "$simulation.template.name", "${simulation.template.versionNumber.toString()}"]
            data << ["Structure:", "$simulation.structure.name", "${simulation.structure.versionNumber.toString()}"]
            data << ["Number of Periods:", simulation.periodCount]
            data << ["Number of Iterations:", simulation.numberOfIterations]
            data << ["Simulation end Date:", simulation.end ? new SimpleDateFormat('dd.MM.yyyy').format(simulation.end) : ""]
            return data
        }
    }

    protected def exportItems(List items) {
        int itemCount = items.size()
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Export"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.fileSelectionMode = itemCount > 1 ? FileChooserConfig.DIRECTORIES_ONLY : FileChooserConfig.FILES_ONLY
        config.setCurrentDirectory(userPreferences.getUserDirectory(UserPreferences.EXPORT_DIR_KEY))
        if (items.size() == 1) {
            config.selectedFile = "${items[0].name}.groovy"
        }

        ULCWindow ancestor = getAncestor()
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    userPreferences.setUserDirectory(UserPreferences.EXPORT_DIR_KEY, filePaths[0])
                    items.each {ModellingItem item ->
                        if (!item.isLoaded()) {
                            item.load()
                        }
                        IConfigObjectWriter writer = item.getWriter()
                        String selectedFile = getFileName(itemCount, filePaths, item)
                        LOG.info " selectedFile : $selectedFile"
                        ClientContext.storeFile([prepareFile: {OutputStream stream ->
                            try {
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream))
                                writer.write(getConfigObject(item), bw)
                            } catch (Throwable t) {
                                new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                            } finally {
                                stream.close()
                            }
                        }, onSuccess: {path, name ->
                            LOG.info " $selectedFile exported"
                        }, onFailure: {reason, description ->
                            new ULCAlert(ancestor, "Export failed", description, "Ok").show()
                        }] as IFileStoreHandler, selectedFile)
                    }
                },
                onFailure: {reason, description ->
                }] as IFileChooseHandler, config, ancestor)
    }

    String getFileName(int itemCount, filePaths, ModellingItem item) {
        String selectedFile = itemCount > 1 ? "${filePaths[0]}/${item.name}.groovy" : filePaths[0]
        return selectedFile
    }

    private ConfigObject getConfigObject(Parameterization parameterization) {
        ConfigObject result
        ParameterizationDAO.withTransaction {TransactionStatus status ->
            parameterization.load()
            result = parameterization.toConfigObject()
        }
        return result
    }

    private ConfigObject getConfigObject(ConfigObjectBasedModellingItem modellingItem) {
        modellingItem.data
    }

    private ConfigObject getConfigObject(ResultConfiguration resultConfiguration) {
        ConfigObject result
        ResultConfigurationDAO.withTransaction {TransactionStatus status ->
            result = resultConfiguration.toConfigObject()
        }
        return result
    }

    private boolean validate(List items) {
        boolean status = true
        items.each {Simulation item ->
            SingleValueResult.withTransaction {trx ->
                item.load()
                def simulationRun = item.simulationRun
                def count = SingleValueResult.countBySimulationRun(simulationRun)
                if (count > 50000) {
                    status = false
                }
            }
        }
        return status
    }
}


class ExportItemGroupAction extends ExportAction {
    boolean onlyNewestVersion = true

    public ExportItemGroupAction(ULCTree tree, P1RATModel model, String title) {
        super(tree, model, title)
    }

    public ExportItemGroupAction(ULCTree tree, P1RATModel model, String title, boolean onlyNewestVersion) {
        super(tree, model, onlyNewestVersion ? (title + "NV") : title)
        this.onlyNewestVersion = onlyNewestVersion
    }


    public void doActionPerformed(ActionEvent actionEvent) {
        Class itemClass = getSelectedItemGroupClass()
        def modelClass = getSelectedModel().class
        switch (itemClass) {
            case Parameterization:
                if (onlyNewestVersion)
                    exportItems(ModellingItemFactory.getNewestParameterizationsForModel(modelClass))
                else
                    exportItems(ModellingItemFactory.getParameterizationsForModel(modelClass))
                break;
            case ResultConfiguration:
                exportItems(ModellingItemFactory.getNewestResultConfigurationsForModel(modelClass))
                break;
            case Simulation:
                exportSimulations(ModellingItemFactory.getActiveSimulationsForModel(modelClass))
                break;
        }
    }

    String getFileName(int itemCount, Object filePaths, ModellingItem item) {
        String paramName = (item.properties.keySet().contains("versionNumber")) ? "${item.name}_v${item.versionNumber}" : "${item.name}"
        paramName = paramName.replaceAll(" ", "")
        File file = new File("${filePaths[0]}/${item.modelClass.name}/")
        if (!file.exists())
            file.mkdir()
        return "${filePaths[0]}/${item.modelClass.name}/${paramName}.groovy"
    }

}

class DeleteAllGroupAction extends DeleteAllAction {

    public DeleteAllGroupAction(ULCTree tree, P1RATModel model) {
        super(tree, model)
    }

    public void doActionPerformed(ActionEvent actionEvent) {
        Class itemClass = getSelectedItemGroupClass()
        def modelClass = getSelectedModel().class
        switch (itemClass) {
            case Parameterization:
                deleteParameterizations(ModellingItemFactory.getNewestParameterizationsForModel(modelClass))
                break;
            case ResultConfiguration:
                deleteResultConfigurations(ModellingItemFactory.getNewestResultConfigurationsForModel(modelClass))
                break;
            case Simulation:
                deleteResults(ModellingItemFactory.getActiveSimulationsForModel(modelClass))
                break;
        }
    }

}


class ExportItemAction extends ExportAction {

    public ExportItemAction(ULCTree tree, P1RATModel model) {
        super(tree, model, "Export")
    }


    public void doActionPerformed(ActionEvent event) {
        doAction(getSelectedItem())
    }

    protected void doAction(ModellingItem item) {
        if (item.changed) {
            ULCAlert alert = new I18NAlert("UnsavedChanged")

            alert.addWindowListener([windowClosing: {WindowEvent windowEvent ->
                def value = windowEvent.source.value
                if (value.equals(alert.firstButtonLabel)) {
                    item.save()
                    exportItems([item])
                }
            }] as IWindowListener)

            alert.show()
        } else {
            exportItems([item])
        }
    }

    protected void doAction(Simulation item) {
        exportSimulations([item])
    }

}

class CreateDefaultParameterizationAction extends SelectionTreeAction {

    public CreateDefaultParameterizationAction(ULCTree tree, P1RATModel model) {
        super("CreateDefaultParameterization", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        Model simulationModel = getSelectedModel()
        DefaultParameterizationDialog dialog = new DefaultParameterizationDialog(UlcUtilities.getWindowAncestor(tree))
        dialog.title = dialog.getText("title")
        dialog.okAction = {
            if (ParameterizationDAO.findByNameAndModelClassName(dialog.nameInput.text, simulationModel.class.name)) {
                I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "UniquesNamesRequired")
                alert.show()
            } else {
                try {
                    def param = ParameterizationHelper.createDefaultParameterization(simulationModel, dialog.periodCount.value)
                    param.name = dialog.nameInput.text
                    param.save()
                    dialog.hide()

                    model.selectionTreeModel.addNodeForItem(param)
                    model.fireModelChanged()
                } catch (Exception ex) {
                    I18NAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "CreationError")
                    alert.show()
                }
            }

        }
        dialog.show()

    }

}

class ImportAction extends SelectionTreeAction {
    UserPreferences userPreferences
    ULCWindow ancestor

    public ImportAction(ULCTree tree, P1RATModel model) {
        super("Import", tree, model)
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
                    ClientContext.loadFile(new ItemLoadHandler(this, node), selectedFile)
                },
                onFailure: {reason, description ->
                    if (IFileLoadHandler.CANCELLED != reason) {
                        new ULCAlert(ancestor, "Import failed", description, "Ok").show()
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

class CreateNewMajorVersion extends SelectionTreeAction {
    public CreateNewMajorVersion(ULCTree tree, P1RATModel model) {
        super("NewMajorVersion", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        createNewVersion(getSelectedItem())
    }

    private void createNewVersion(Parameterization item) {
        model.createNewVersion(selectedModel, item, false)
    }


    private void createNewVersion(ResultConfiguration template) {
        model.createNewVersion(selectedModel, template)
    }

    private void createNewVersion(def node) {}

}

class SaveAsAction extends SelectionTreeAction {

    public SaveAsAction(ULCTree tree, P1RATModel model) {
        super("SaveAs", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        NodeNameDialog dialog = new NodeNameDialog(UlcUtilities.getWindowAncestor(tree), getSelectedItem())
        dialog.title = dialog.getText("title")
        dialog.okAction = {  model.addItem(getSelectedItem(), dialog.nameInput.text) }
        dialog.show()
    }

}

class SimulationAction extends SelectionTreeAction {

    public SimulationAction(ULCTree tree, P1RATModel model) {
        super("RunSimulation", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        Model selectedModel = getSelectedModel()
        Object selectedItem = getSelectedItem()
        Simulation simulation = new Simulation("Simulation")
        simulation.parameterization = selectedItem instanceof Parameterization ? selectedItem : null
        simulation.template = selectedItem instanceof ResultConfiguration ? selectedItem : null
        model.openItem(selectedModel, simulation)
    }

}

class DeleteAction extends SelectionTreeAction {

    public DeleteAction(ULCTree tree, P1RATModel model) {
        super("Delete", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {

        boolean usedInSimulation = false
        def selectedItem = getSelectedItem()
        if (selectedItem instanceof Parameterization || selectedItem instanceof ResultConfiguration) {
            usedInSimulation = selectedItem.isUsedInSimulation()
        }
        if (!usedInSimulation) {
            model.removeItem(getSelectedModel(), selectedItem)
        } else {
            ULCAlert alert = new ULCAlert('Item already used', 'This item has already been used in a simulation and cannot be deleted', 'OK')
            alert.messageType = ULCAlert.INFORMATION_MESSAGE
            alert.parent = UlcUtilities.getWindowAncestor(event.source)
            alert.show()
        }

    }

}