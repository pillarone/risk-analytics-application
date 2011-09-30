package org.pillarone.riskanalytics.application.reports

import javax.sql.DataSource
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import org.codehaus.groovy.grails.commons.ApplicationHolder
import net.sf.jasperreports.engine.*
import org.apache.commons.io.FileUtils

class ReportHelper {

    public static URL getReportFolder() {
        return ReportHelper.class.getResource("/reports")
    }

    public static OutputStream getReportOutputStream(Map parameters, JRBeanCollectionDataSource collectionDataSource) {
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
            ex.printStackTrace()
        }

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint)
        exporter.exportReport()
        return byteArray

    }



}
