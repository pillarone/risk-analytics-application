package org.pillarone.riskanalytics.application.reports.gira.action

import org.pillarone.riskanalytics.application.reports.AbstractReportActionTests
import org.pillarone.riskanalytics.application.reports.ReportHelper
import net.sf.jasperreports.engine.JasperCompileManager
import org.apache.commons.io.FileUtils
import net.sf.jasperreports.engine.JRException

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class GiraSubReportTests extends AbstractReportActionTests {

    // just to compile jrxml file to .jasper
    void testSubReports() {
        compile()
    }

    public static void compile() {
        compileSubReport("CERSub_Comments")
        compileSubReport("CERSub_ValuesTable")
        compileSubReport("CERSub_PDFChart_legend")
        compileSubReport("CERSub_ReportInfoTable")
        compileSubReport("CERSub_WaterfallOverview")
        compileSubReport("CERSub_PDFChartAndCommentsInfo")
        compileSubReport("CERSub_PDFCharts")
//        compileSubReport("CERSub_Component")
        compileSubReport("GiraReport")
    }

    //compile jrxml file to jasper
    public static void compileSubReport(String source) {
        File f = new File(ReportHelper.getReportFolder().toURI())
        String dir = f.getAbsolutePath() + "/"
        try {
            String src = dir + source + ".jrxml"
            println " compiling ${src}.jrxml ..."
            String local = "C:/dev/RiskAnalytics/RiskAnalyticsApplication/src/java/reports/" + source + ".jrxml"
//            String target = dir + source + ".jasper"
            JasperCompileManager.compileReportToFile(src)
            JasperCompileManager.compileReportToFile(local)
            println "${src} compiled"
        } catch (JRException ex) {
            println "$ex"
            ex.printStackTrace()
        } catch (Exception ex) {
            println "$ex"
            ex.printStackTrace()
        }

    }
}
