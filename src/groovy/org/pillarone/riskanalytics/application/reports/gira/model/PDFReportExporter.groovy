package org.pillarone.riskanalytics.application.reports.gira.model

import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.engine.JRExporter

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class PDFReportExporter extends AbstractReportExporter {

    public PDFReportExporter(){
        exporter = new JRPdfExporter()
    }

}
