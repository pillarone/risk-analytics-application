package org.pillarone.riskanalytics.application.ui.base.action

import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.report.IReportModel
import org.pillarone.riskanalytics.core.report.ReportFactory
import com.ulcjava.base.shared.FileChooserConfig
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.ClientContext
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.application.util.IFileChooseHandler
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.pillarone.riskanalytics.core.report.IReportData
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.report.impl.ModellingItemReportData
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.report.impl.ReportDataCollection
import org.pillarone.riskanalytics.core.report.UnsupportedReportParameterException
import com.ulcjava.base.application.IAction


abstract class CreateReportAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateReportAction)

    IReportModel reportModel

    public CreateReportAction(IReportModel reportModel, String renderedFormatSuchAsPDF, tree, RiskAnalyticsMainModel model) {
        super("GenerateReport", tree, model)
        this.reportModel = reportModel
        putValue(IAction.NAME, reportModel.getName() + " " + renderedFormatSuchAsPDF)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        IReportData reportData = getReportData()
        try {
            byte[] report = createReport(reportModel, reportData)
            saveReport(report, reportData)
        } catch (UnsupportedReportParameterException e) {
             LOG.error "Unsupported input to report: ${e}", e
             new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "UnsupportedReportInput", e.getMessage()).show()
         }
     }

    private IReportData getReportData() {
        List<AbstractUIItem> selectedItems = getSelectedUIItems()
        if (selectedItems.size() == 1) {
            Object item = selectedItems.get(0).item
            return getReportData(item)
        } else {
            return new ReportDataCollection(selectedItems.collect { uiItem -> getReportData(uiItem.item) })
        }
    }

    private IReportData getReportData(Object item) {
        if (item instanceof ModellingItem) {
            ModellingItem modellingItem = (ModellingItem) item;
            return new ModellingItemReportData(modellingItem)
        } else {
            throw new IllegalArgumentException("Cannot create IReportData object for this selected UIItem: " + item)
        }
    }

    abstract protected byte[] createReport(IReportModel reportModel, IReportData reportData)

    abstract protected String getFileExtension()

    private void saveReport(byte[] output, IReportData reportData) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Save Report As"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        String fileName = reportModel.getDefaultReportFileNameWithoutExtension(reportData) + "." + getFileExtension()
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
        super(reportModel, "PDF", tree, model)
    }

    @Override
    protected byte[] createReport(IReportModel reportModel, IReportData reportData) {
        return ReportFactory.createPDFReport(reportModel, reportData)
    }

    @Override
    protected String getFileExtension() {
        return "pdf"
    }

}

class CreatePPTXReportAction extends CreateReportAction {

    public CreatePPTXReportAction(IReportModel reportModel, tree, RiskAnalyticsMainModel model) {
        super(reportModel, "PowerPoint", tree, model)
    }

    @Override
    protected byte[] createReport(IReportModel reportModel, IReportData reportData) {
        return ReportFactory.createPPTXReport(reportModel, reportData)
    }

    @Override
    protected String getFileExtension() {
        return "pptx"
    }

}

class CreateXlsReportAction extends CreateReportAction {

    public CreateXlsReportAction(IReportModel reportModel, tree, RiskAnalyticsMainModel model) {
        super(reportModel, "Excel", tree, model)
    }

    @Override
    protected byte[] createReport(IReportModel reportModel, IReportData reportData) {
        return ReportFactory.createXLSReport(reportModel, reportData)
    }

    @Override
    protected String getFileExtension() {
        return "xls"
    }

}
