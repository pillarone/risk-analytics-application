package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.apache.log4j.Logger
import org.pillarone.riskanalytics.application.reports.ReportFactory
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.util.I18NAlert

class GenerateReportAction extends SelectionTreeAction implements ITreeSelectionListener {
    String name
    static final Logger LOG = Logger.getLogger(GenerateReportAction)

    public GenerateReportAction(String name, ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("GenerateReport", tree, model)
        this.@name = name
        putValue(IAction.NAME, getValue(IAction.NAME) + " " + name);
        tree.addTreeSelectionListener(this)
    }

    public void checkAvailability(def object) {
        setEnabled(false)
    }

    public void checkAvailability(Simulation simulation) {
        try {
            ReportFactory.getReportModel(simulation, name)
            setEnabled(true)
        } catch (IllegalArgumentException e) {
            setEnabled(false)
        } catch (Exception ex) {
            setEnabled(false)
        }
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        checkAvailability(selectedItem)
    }

    public void doActionPerformed(ActionEvent event) {
        try {
            def output = ReportFactory.getReport((Simulation) selectedItem, name)
            if (!ReportFactory.testMode) {
                saveReport output
            }
        } catch (IllegalArgumentException e) {
            LOG.error "Can not create report: ${e.message} Stacktrace: ${e.stackTrace}"
            if (!ReportFactory.testMode) {
                ULCAlert alert = new ULCAlert("Report Generation not Possible", e.message, "Close")
                alert.messageType = ULCAlert.INFORMATION_MESSAGE
                alert.show()
            }
        }
    }


    protected void saveReport(def output) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Save Report As"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        String fileName = "$name of ${((Simulation) selectedItem).name}.pdf"
        fileName = fileName.replace(":", "")
        fileName = fileName.replace("/", "")
        fileName = fileName.replace("*", "")
        fileName = fileName.replace("?", "")
        fileName = fileName.replace("\"", "")
        fileName = fileName.replace("<", "")
        fileName = fileName.replace(">", "")
        fileName = fileName.replace("|", "")
        config.selectedFile = fileName

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(tree)
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedFile = filePaths[0]

                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        try {
                            stream.write output
                        } catch (UnsupportedOperationException t) {
                            LOG.error "Saving Report Failed: ${t}", t
                            new I18NAlert(ancestor, "SaveReportError").show()
                        } catch (Throwable t) {
                            LOG.error "Saving Report Failed: ${t}", t
                            new I18NAlert(ancestor, "SaveReportError").show()
                            throw t
                        } finally {
                            stream.close()
                        }
                    }, onSuccess: {path, name ->
                    }, onFailure: {reason, description ->
                        LOG.error "Saving Report Failed: Description: ${description} Reason: ${reason}"
                        new I18NAlert(ancestor, "SaveReportError").show()
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                    LOG.error "Saving Report Failed: Description: ${description} Reason: ${reason}"
                    new I18NAlert(ancestor, "SaveReportError").show()
                }] as IFileChooseHandler, config, ancestor)
    }


}