package org.pillarone.riskanalytics.application.reports.gira.action

import org.pillarone.riskanalytics.application.reports.AbstractReportActionTests
import org.pillarone.riskanalytics.application.reports.ReportHelper

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class GiraSubReportTests extends AbstractReportActionTests {

    void testSubReports(){
//        ReportHelper.compileSubReport("FieldValuesTable")
        ReportHelper.compileSubReport("CERSub_Comments")
        ReportHelper.compileSubReport("TableOfContentsReport")
        ReportHelper.compileSubReport("CERSub_Content")
        ReportHelper.compileSubReport("CERSub_ValuesTable")
        ReportHelper.compileSubReport("HeadingsReport")
        ReportHelper.compileSubReport("CERSub_PDFChart_legend")
        ReportHelper.compileSubReport("CERSub_ReportInfoTable")
        ReportHelper.compileSubReport("CERSub_WaterfallOverview")
        ReportHelper.compileSubReport("CERSub_PDFChartAndCommentsInfo")
        ReportHelper.compileSubReport("CERSub_PDFCharts")
        ReportHelper.compileSubReport("CERSub_Component")
    }
}
