package org.pillarone.riskanalytics.application.ui.base.action

import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataTableModel
import org.pillarone.riskanalytics.application.util.LocaleResources

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TableCopierTests extends GroovyTestCase {
    @Override protected void setUp() {
        super.setUp()
        LocaleResources.setTestMode()
    }

    @Override protected void tearDown() {
        super.tearDown()
        LocaleResources.clearTestMode()
    }





    public void testCopyContent() {
        List<List<Number>> data = [[1, 2], [3, 4], [5, 6], [7, 8]]
        ResultIterationDataTableModel model = new ResultIterationDataTableModel(data, ["title1", "title2"])
        TableCopier tableCopier = new TableCopier(model: model)
        assertEquals "title1\ttitle2\n1\t2\n3\t4\n", tableCopier.copyContent([0, 1] as int[], [0, 1] as int[])

    }
}
