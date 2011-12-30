package org.pillarone.riskanalytics.application.ui.customtable

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.pillarone.riskanalytics.application.ui.customtable.view.CustomTableView
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper
import com.ulcjava.testframework.operator.ULCTableOperator
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCComponentOperator


class PivotTests extends AbstractSimpleFunctionalTest {
    CustomTableView      customTableView

    @Override
    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")

        customTableView = new CustomTableView()
        customTableView.parent = frame

        frame.setSize(800, 600)
        frame.setContentPane(customTableView.content)
        frame.visible = true

    }


    void testColNo () {
        assert CustomTableHelper.getColNo("A")   == 1
        assert CustomTableHelper.getColNo("B")   == 2
        assert CustomTableHelper.getColNo("C")   == 3
        assert CustomTableHelper.getColNo("AA")  == 27
        assert CustomTableHelper.getColNo("AB")  == 28
        assert CustomTableHelper.getColNo("AC")  == 29
        assert CustomTableHelper.getColNo("BA")  == 53
        assert CustomTableHelper.getColNo("BB")  == 54
        assert CustomTableHelper.getColNo("BC")  == 55
        assert CustomTableHelper.getColNo("AAA") == 703
        assert CustomTableHelper.getColNo("AAB") == 704
        assert CustomTableHelper.getColNo("AAC") == 705
        assert CustomTableHelper.getColNo("ABA") == 729
        assert CustomTableHelper.getColNo("ABB") == 730
        assert CustomTableHelper.getColNo("ABC") == 731
    }

    void testColString() {
        assert CustomTableHelper.getColString(1) == "A"
        assert CustomTableHelper.getColString(2) == "B"
        assert CustomTableHelper.getColString(3) == "C"
        assert CustomTableHelper.getColString(27) == "AA"
        assert CustomTableHelper.getColString(28) == "AB"
        assert CustomTableHelper.getColString(29) == "AC"
        assert CustomTableHelper.getColString(53) == "BA"
        assert CustomTableHelper.getColString(54) == "BB"
        assert CustomTableHelper.getColString(55) == "BC"
        assert CustomTableHelper.getColString(703) == "AAA"
        assert CustomTableHelper.getColString(704) == "AAB"
        assert CustomTableHelper.getColString(705) == "AAC"
        assert CustomTableHelper.getColString(729) == "ABA"
        assert CustomTableHelper.getColString(730) == "ABB"
        assert CustomTableHelper.getColString(731) == "ABC"
    }

    void testCopyData () {
        assert CustomTableHelper.copyData ("=SUM(A1;A2;A3)", 0, 1) == "=SUM(B1;B2;B3)"
        assert CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 1, 0) == "=SUM(A2;B2;C2)"
        assert CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 1, 1) == "=SUM(B2;C2;D2)"

        assert CustomTableHelper.copyData ("=SUM(A1;A2;A3)", 0, 2) == "=SUM(C1;C2;C3)"
        assert CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 2, 0) == "=SUM(A3;B3;C3)"
        assert CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 2, 2) == "=SUM(C3;D3;E3)"

        assert CustomTableHelper.copyData ("=SUM(\$A1;A\$2;A3)", 0, 1) == "=SUM(\$A1;B\$2;B3)"
        assert CustomTableHelper.copyData ("=SUM(A\$1;\$B1;C1)", 1, 0) == "=SUM(A\$1;\$B2;C2)"
        assert CustomTableHelper.copyData ("=SUM(\$A\$1;\$B1;C\$1)", 1, 1) == "=SUM(\$A\$1;\$B2;D\$1)"
    }

    void testFrame() {
        ULCButtonOperator    insertRowButton    = new ULCButtonOperator(customTableView.newRowButton)
        ULCButtonOperator    insertColButton    = new ULCButtonOperator(customTableView.newColButton)
        ULCTableOperator     table              = new ULCTableOperator(customTableView.customTable)
//        ULCTextFieldOperator cellEditTextField  = new ULCTextFieldOperator(customTableView.cellEditTextField)
//        ULCComponentOperator dataCellEditPane   = new ULCComponentOperator(customTableView.dataCellEditPane)

        // Test insert Row
        assert table.rowCount == 0
        insertRowButton.clickMouse()
        assert table.rowCount == 1
        insertRowButton.clickMouse(3)
        assert table.rowCount == 4

        // Test insert Col
        assert table.columnCount == 0
        insertColButton.clickMouse()
        assert table.columnCount == 1
        insertColButton.clickMouse(3)
        assert table.columnCount == 4

        // Insert data test
//        setTextOnCell(0,0,"10")
//        setTextOnCell(1,0,"20")
//        setTextOnCell(2,0,"30")
//        setTextOnCell(0,1,"=SUM(A1:A3)")

//        sleep 10000
    }

//    private setTextOnCell (int row, int col, String text) {
//        table.selectCell(row,col)
//        assert cellEditTextField.isVisible() == true
//        assert dataCellEditPane.isVisible() == false
//        assert cellEditTextField.hasFocus() == false
//        table.pressKey (KeyEvent.VK_ENTER)
//        assert cellEditTextField.hasFocus() == true
//        cellEditTextField.enterText(text)
//        cellEditTextField.pressKey (KeyEvent.VK_ENTER)
//        assert table.getValueAt(row,col) == text
//    }
}
