package org.pillarone.riskanalytics.application.reports

import javax.sql.DataSource
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import org.codehaus.groovy.grails.commons.ApplicationHolder
import net.sf.jasperreports.engine.*

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

    public static OutputStream commentReport(Map parameters, JRBeanCollectionDataSource collectionDataSource) {
        String reportName = parameters["_file"] + ".jrxml"

        URL reportsDir = getReportFolder()
        URL reportUrl = new URL(reportsDir.toExternalForm() + "/" + reportName)

        JRExporter exporter = new JRPdfExporter()

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream()
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, byteArray)

        JasperPrint jasperPrint
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportUrl.openStream())

            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, collectionDataSource)

        } catch (Exception ex) {
            println "${ex}"
        } 

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint)
        exporter.exportReport()
        return byteArray

    }

}
