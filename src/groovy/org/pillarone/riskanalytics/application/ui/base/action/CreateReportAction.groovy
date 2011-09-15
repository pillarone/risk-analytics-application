package org.pillarone.riskanalytics.application.ui.base.action

import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import com.ulcjava.base.shared.FileChooserConfig
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.ClientContext
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.application.util.IFileChooseHandler
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import com.ulcjava.base.application.IAction


abstract class CreateReportAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateReportAction)

    IReportModel reportModel

    public CreateReportAction(IReportModel reportModel, name, tree, RiskAnalyticsMainModel model) {
        super(name, tree, model)
        this.reportModel = reportModel
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        saveReport(createReport(reportModel, (Simulation) getSelectedItem()))
    }

    abstract protected byte[] createReport(IReportModel reportModel, Simulation simulation)

    abstract protected String getFileExtension()

    protected void saveReport(byte[] output) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Save Report As"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        String fileName = "${reportModel.name} of ${((Simulation) selectedItem).name}." + getFileExtension()
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
                            stream.write(output)
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
                    if (reason != IFileChooseHandler.CANCELLED) {
                        LOG.error "Saving Report Failed: Description: ${description} Reason: ${reason}"
                        new I18NAlert(ancestor, "SaveReportError").show()
                    }
                }] as IFileChooseHandler, config, ancestor)
    }


}

class CreatePDFReportAction extends CreateReportAction {

    public CreatePDFReportAction(IReportModel reportModel, tree, RiskAnalyticsMainModel model) {
        super(reportModel, "GeneratePDFReport", tree, model)
        putValue(IAction.NAME, "PDF Report")
    }

    @Override
    protected byte[] createReport(IReportModel reportModel, Simulation simulation) {
        return ReportFactory.createPDFReport(reportModel, simulation)
    }

    @Override
    protected String getFileExtension() {
        return "pdf"
    }

}

class CreatePPTXReportAction extends CreateReportAction {

    public CreatePPTXReportAction(IReportModel reportModel, tree, RiskAnalyticsMainModel model) {
        super(reportModel, "GeneratePPTXReport", tree, model)
        putValue(IAction.NAME, "PPTX Report")
    }

    @Override
    protected byte[] createReport(IReportModel reportModel, Simulation simulation) {
        return ReportFactory.createPPTXReport(reportModel, simulation)
    }

    @Override
    protected String getFileExtension() {
        return "pptx"
    }

}
