package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.table.ITableModel
import com.ulcjava.testframework.operator.ULCTableOperator
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.ComponentUtils


class PastePartialRowTests extends MultiDimensionalParameterCopyPasteTests {

    @Override
    protected void assertTable(ITableModel model) {
        assertEquals 1, model.getValueAt(1, 1)
        assertEquals 1.23d, model.getValueAt(1, 2)
        assertEquals new DateTime(2010, 1, 1, 0, 0, 0, 0).millis, (model.getValueAt(1, 3) as Date).time
        assertEquals true, model.getValueAt(1, 4)
        assertEquals "text", model.getValueAt(1, 5)
        assertEquals ComponentUtils.getNormalizedName("hierarchyComponent"), model.getValueAt(1, 6)
    }

    @Override
    protected String getPasteContent() {
        return "1.23\t01.01.2010"
    }

    @Override
    protected void selectCell(ULCTableOperator table) {
        table.selectCell(1, 2)
    }


}
