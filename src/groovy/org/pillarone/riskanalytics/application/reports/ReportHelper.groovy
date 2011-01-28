package org.pillarone.riskanalytics.application.reports

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.*
import javax.sql.DataSource
import org.codehaus.groovy.grails.commons.ApplicationHolder

class ReportHelper {

    public static URL getReportFolder() {
        return ReportHelper.class.getResource("/reports")
    }

    public static OutputStream generateReport(Collection reportData, Map parameters) {

        DataSource dataSource = ApplicationHolder.application.mainContext.getBean("dataSource")

        String reportName = parameters["_file"] + ".jrxml"

        JRExporter exporter = new JRPdfExporter()

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream()
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)

        URL reportsDir = getReportFolder()
        URL reportUrl = new URL(reportsDir.toExternalForm() + "/" + reportName)
        JasperPrint jasperPrint

        if (reportData != null) {
            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(reportData);
            if (reportName.endsWith('.jasper')) {
                jasperPrint = JasperFillManager.fillReport(reportUrl.openStream(), parameters, jrBeanCollectionDataSource)
            } else {
                jasperPrint = JasperFillManager.fillReport(JasperCompileManager.compileReport(reportUrl.openStream()), parameters, jrBeanCollectionDataSource)
            }
        } else {
            java.sql.Connection conn = dataSource.getConnection()
            try {
                if (reportName.endsWith('.jasper')) {
                    jasperPrint = JasperFillManager.fillReport(reportUrl.openStream(), parameters, conn)
                } else {
                    jasperPrint = JasperFillManager.fillReport(JasperCompileManager.compileReport(reportUrl.openStream()), parameters, conn)
                }
            }
            finally {
                conn.close()
            }
        }

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint)
        exporter.exportReport()
        return byteArray
    }
}
