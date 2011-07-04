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

    // just to compile jrxml file .to jasper
    void testSubReports() {
//        ReportHelper.compileSubReport("CERSub_Comments")
        //compileSubReport("TableOfContentsReport")
        //compileSubReport("CERSub_Content")
        //compileSubReport("CERSub_ValuesTable")
        //compileSubReport("HeadingsReport")
        //compileSubReport("CERSub_PDFChart_legend")
        //compileSubReport("CERSub_ReportInfoTable")
        //compileSubReport("CERSub_WaterfallOverview")
        //compileSubReport("CERSub_PDFChartAndCommentsInfo")
        //compileSubReport("CERSub_PDFCharts")
        //compileSubReport("CERSub_Component")
    }

    //compile jrxml file to jasper
    public static void compileSubReport(String source) {
        String reportName = source + ".jrxml"
        String dir = "C:/riskanalytics/RiskAnalyticsApplication/src/java/reports" + "/"
        String dirTaget = ReportHelper.getReportFolder().toExternalForm() + "/"
        try {
            String src = dir + source + ".jrxml"
            String target = dirTaget + source + ".jasper"
            println "src : ${src}"
            println "target : ${target}"
            String res = JasperCompileManager.compileReportToFile(src)
            println "result : $res"
            File targetFile = new File(target)
            if (targetFile.exists()) targetFile.delete()
            FileUtils.copyFile(new File(res), targetFile)
        } catch (JRException ex) {
            println "-------------- start"
            println "${ex}"
            println "${ex.getStackTrace()}"
            ex.printStackTrace()
            println "-------------- end"
        } catch (Exception ex) {
            println "-------------- start"
            println "${ex}"
            println "${ex.getStackTrace()}"
            ex.printStackTrace()
            println "-------------- end"
        }

    }
}
