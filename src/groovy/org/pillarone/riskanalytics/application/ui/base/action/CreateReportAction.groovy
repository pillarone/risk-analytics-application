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
import org.pillarone.riskanalytics.application.document.ShowDocumentStrategyFactory
import net.sf.jmimemagic.Magic
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.reports.IReportableNode


public class CreateReportAction extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateReportAction)

    IReportModel reportModel
    ReportFactory.ReportFormat reportFormat

    public CreateReportAction(IReportModel reportModel, ReportFactory.ReportFormat reportFormat, tree, RiskAnalyticsMainModel model) {
        super("GenerateReport", tree, model)
        this.reportModel = reportModel
        this.reportFormat = reportFormat
        putValue(IAction.NAME, reportModel.getName() + " " + reportFormat.getRenderedFormatSuchAsPDF())
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        IReportData reportData = getReportData()
        try {
            byte[] report = ReportFactory.createReport(reportModel, reportData, reportFormat)
            saveReport(report, reportData)
        } catch (UnsupportedReportParameterException e) {
             LOG.error "Unsupported input to report: ${e}", e
             new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "UnsupportedReportInput", e.getMessage()).show()
         }
     }

    /**
     *
     * @return An IReportData. If the list of reporting items is of lenth 1, return the simpler
     * single object, otherwise use the composite pattern and return a list of IReportData's
     *
     */
    public IReportData getReportData() {
        List<ItemNode> selectedItems = getReportingModellingNodes()
        Collection<ModellingItem> modellingItems = selectedItems.collect {
            itemNode ->
                    if(itemNode instanceof IReportableNode) {
                        itemNode.modellingItemsForReport()
                    }
        }.flatten()
        Collection<IReportData> reportData = new ArrayList<IReportData>()
        for (ModellingItem aModellingItem in modellingItems) {
            reportData << new ModellingItemReportData(aModellingItem)
        }
//        If there is only one entry this is a special case. Return an individual modelling item report data.
        if(reportData.size() == 1) {
            return reportData.get(0)
        }
        return new ReportDataCollection( reportData )
    }

    private void saveReport(byte[] output, IReportData reportData) {
        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Save Report As"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        String fileName = reportModel.getDefaultReportFileNameWithoutExtension(reportData) + "." + reportFormat.getFileExtension()
        fileName = fileName.replace(":", "")
        fileName = fileName.replace("/", "")
        fileName = fileName.replace("*", "")
        fileName = fileName.replace("?", "")
        fileName = fileName.replace("\"", "")
        fileName = fileName.replace("<", "")
        fileName = fileName.replace(">", "")
        fileName = fileName.replace("|", "")
        config.selectedFile = fileName

        ShowDocumentStrategyFactory.getInstance().showDocument(fileName, output, Magic.getMagicMatch(output).getMimeType())
    }


}
