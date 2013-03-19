package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.table.ITableModel
import com.ulcjava.testframework.operator.ULCTableOperator
import org.joda.time.DateTime

class PasteCompleteRowTests extends MultiDimensionalParameterCopyPasteTests {

    @Override
    protected void assertTable(ITableModel model) {
        assertEquals 123, model.getValueAt(1, 1)
        assertEquals 1.23d, model.getValueAt(1, 2)
        assertEquals new DateTime(2010, 1, 1, 0, 0, 0, 0).millis, (model.getValueAt(1, 3) as Date).time
        assertEquals false, model.getValueAt(1, 4)
        assertEquals "new text", model.getValueAt(1, 5)
        assertEquals "first component", model.getValueAt(1, 6)
    }

    @Override
    protected String getPasteContent() {
        return "123\t1.23\t01.01.2010\tfalse\tnew text\tfirst component"
    }

    @Override
    protected void selectCell(ULCTableOperator table) {
        table.selectCell(1, 1)
    }


}
