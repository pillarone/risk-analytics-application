package org.pillarone.riskanalytics.application.ui.main.action

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.util.IConfigObjectWriter
import org.springframework.transaction.TransactionStatus

import org.pillarone.riskanalytics.core.simulation.item.*
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.ExcelExporter
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.UserPreferences
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class ExportAction extends SelectionTreeAction {
    UserPreferences userPreferences
    Log LOG = LogFactory.getLog(ExportAction)

    public ExportAction(ULCTableTree tree, P1RATModel model, String title) {
        super(title, tree, model)
        userPreferences = new UserPreferences()
    }

    public ExportAction(String title) {
        super(title)
        userPreferences = new UserPreferences()
    }

    protected void exportAll(List items) {
        if (!items || items.isEmpty()) return
        switch (items[0].class) {
            case Simulation.class: exportSimulations(items); break;
            default: exportItems(items); break;
        }
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
                        items.each {def item ->
                            exportItem(item, itemCount, filePaths, ancestor)
                        }
                    },
                    onFailure: {reason, description ->
                        if (reason != IFileChooseHandler.CANCELLED) {
                            LOG.error description
                            showAlert("exportError")
                        }
                    }] as IFileChooseHandler, config, ancestor)
        }

    }

    private void exportItem(Simulation item, int itemCount, filePaths, ULCWindow ancestor) {
        ExcelExporter exporter = new ExcelExporter()
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
                LOG.error description
                showAlert("exportError")
            }] as IFileStoreHandler, selectedFile)
        }
    }

    private void exportItem(ModellingItem item, int itemCount, filePaths, ULCWindow ancestor) {
        if (!item.isLoaded()) {
            item.load()
        }
        IConfigObjectWriter writer = item.getWriter()
        String selectedFile = getFileName(itemCount, filePaths, item)
        LOG.info " selectedFile : $selectedFile "
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
            LOG.error description
            showAlert("exportError")
        }] as IFileStoreHandler, selectedFile)
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
                    userPreferences.setUserDirectory(UserPreferences.EXPORT_DIR_KEY, getFolderName(itemCount, filePaths))
                    items.each {def item ->
                        exportItem(item, itemCount, filePaths, ancestor)
                    }
                },
                onFailure: {reason, description ->
                    if (reason != IFileChooseHandler.CANCELLED) {
                        LOG.error description
                        showAlert("exportError")
                    }

                }] as IFileChooseHandler, config, ancestor)
    }


    private def showAlert(String errorName) {
        ULCAlert alert = new I18NAlert(ancestor, errorName)
        alert.show()
    }

    String getFileName(int itemCount, filePaths, ModellingItem item) {
        String selectedFile = itemCount > 1 ? "${filePaths[0]}${getFileSeparator()}${item.name}.groovy" : filePaths[0]
        return validateFileName(selectedFile)
    }

    String getFolderName(int itemCount, filePaths) {
        String selectedFile = itemCount > 1 ? "${filePaths[0]}" : filePaths[0].substring(0, filePaths[0].lastIndexOf(getFileSeparator()))
        return validateFileName(selectedFile)
    }

    private ConfigObject getConfigObject(Parameterization parameterization) {
        ConfigObject result
        ParameterizationDAO.withTransaction {TransactionStatus status ->
            if (!parameterization.isLoaded())
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
        items.each {def item ->
            if (item instanceof Simulation) {
                SingleValueResult.withTransaction {trx ->
                    item.load()
                    def simulationRun = item.simulationRun
                    def count = SingleValueResult.countBySimulationRun(simulationRun)
                    if (count > 50000) {
                        status = false
                    }
                }
            }
        }
        return status
    }

    static String validateFileName(String filename) {
        String separator = getFileSeparator()
        def arr = filename.split(Pattern.quote(separator))
        def pattern = ~/[^\w.]/
        arr[arr.size() - 1] = pattern.matcher(arr[arr.size() - 1]).replaceAll("")

        filename = ""
        arr.eachWithIndex {String p, int index ->
            filename += p
            if (index != arr.size() - 1)
                filename += separator
        }
        return filename
    }

    static String getFileSeparator() {
        return ClientContext.getSystemProperty("file.separator")
    }
}
