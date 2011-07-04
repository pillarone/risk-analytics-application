package org.pillarone.riskanalytics.application.reports

import org.pillarone.riskanalytics.application.util.LocaleResources
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.reports.bean.PropertyValuePairBean

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractReportActionTests extends GroovyTestCase {

    @Override
    protected void setUp() {
        LocaleResources.setTestMode()
        super.setUp()
    }

    @Override
    protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown()
    }

        //just for test
    public static JRBeanCollectionDataSource createItemSettingsDataSource() {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()
        currentValues << new PropertyValuePairBean(property: "Model", value: "ModelTest")
        currentValues << new PropertyValuePairBean(property: "Parameterization", value: "P1")
        currentValues << new PropertyValuePairBean(property: "Number of Iterations", value: "P1")
        currentValues << new PropertyValuePairBean(property: "End Date", value: DateFormatUtils.formatDetailed(new DateTime()))
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource

    }

    protected void verifyExport(File exportedFile) {
        assertTrue(exportedFile.exists())
        assertTrue("pdf not exported", exportedFile.size() > 0)
        exportedFile.delete()
    }
}
