package org.pillarone.riskanalytics.application.ui.base.action

import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterTableModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataTableModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter

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
        assertEquals "title1\n1\n", tableCopier.copyContent([0] as int[], [0] as int[])
        assertEquals "title2\n4\n", tableCopier.copyContent([1] as int[], [1] as int[])
    }

    public void testCopyMDPContent() {
        def mdp = new SimpleMultiDimensionalParameter([[1, 2, 3], [1, 2, 3], [1, 2, 3]])
        MultiDimensionalParameterTableModel tableModel = new MultiDimensionalParameterTableModel()
        tableModel.multiDimensionalParam = mdp
        TableCopier tableCopier = new TableCopier(model: tableModel)
        assertNotNull tableCopier.copyContent([0, 1] as int[], [0, 1] as int[])
    }
}
