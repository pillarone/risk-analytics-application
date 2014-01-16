package org.pillarone.riskanalytics.application.reports

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.junit.Test

class ReportHelperTests {

    @Test
    void testGetReport() {
        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource([])

        Map params = new HashMap()
        params["_file"] = "CommentReport"
        def report = ReportHelper.getReportOutputStream(params, beanCollectionDataSource)
        assert report != null
    }

}
