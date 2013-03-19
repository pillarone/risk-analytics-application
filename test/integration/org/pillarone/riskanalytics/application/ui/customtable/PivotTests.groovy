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
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCDialogOperator
import org.pillarone.riskanalytics.application.ui.customtable.view.TableSizeDialog


class PivotTests extends AbstractSimpleFunctionalTest {
    CustomTableView      customTableView

    @Override
    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")

        customTableView = new CustomTableView(null)
        customTableView.parent = frame

        frame.setSize(800, 600)
        frame.setContentPane(customTableView.content)
        frame.visible = true

    }


    void testColNo () {
        assertTrue CustomTableHelper.getColNo("A")   == 1
        assertTrue CustomTableHelper.getColNo("B")   == 2
        assertTrue CustomTableHelper.getColNo("C")   == 3
        assertTrue  CustomTableHelper.getColNo("AA")  == 27
        assertTrue  CustomTableHelper.getColNo("AB")  == 28
        assertTrue  CustomTableHelper.getColNo("AC")  == 29
        assertTrue  CustomTableHelper.getColNo("BA")  == 53
        assertTrue  CustomTableHelper.getColNo("BB")  == 54
        assertTrue  CustomTableHelper.getColNo("BC")  == 55
        assertTrue  CustomTableHelper.getColNo("AAA") == 703
        assertTrue  CustomTableHelper.getColNo("AAB") == 704
        assertTrue  CustomTableHelper.getColNo("AAC") == 705
        assertTrue  CustomTableHelper.getColNo("ABA") == 729
        assertTrue  CustomTableHelper.getColNo("ABB") == 730
        assertTrue  CustomTableHelper.getColNo("ABC") == 731
    }

    void testColString() {
        assertTrue  CustomTableHelper.getColString(1) == "A"
        assertTrue  CustomTableHelper.getColString(2) == "B"
        assertTrue  CustomTableHelper.getColString(3) == "C"
        assertTrue  CustomTableHelper.getColString(27) == "AA"
        assertTrue  CustomTableHelper.getColString(28) == "AB"
        assertTrue  CustomTableHelper.getColString(29) == "AC"
        assertTrue  CustomTableHelper.getColString(53) == "BA"
        assertTrue  CustomTableHelper.getColString(54) == "BB"
        assertTrue  CustomTableHelper.getColString(55) == "BC"
        assertTrue  CustomTableHelper.getColString(703) == "AAA"
        assertTrue  CustomTableHelper.getColString(704) == "AAB"
        assertTrue  CustomTableHelper.getColString(705) == "AAC"
        assertTrue  CustomTableHelper.getColString(729) == "ABA"
        assertTrue  CustomTableHelper.getColString(730) == "ABB"
        assertTrue  CustomTableHelper.getColString(731) == "ABC"
    }

    void testCopyData () {
        assertTrue  CustomTableHelper.copyData ("=SUM(A1;A2;A3)", 0, 1) == "=SUM(B1;B2;B3)"
        assertTrue  CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 1, 0) == "=SUM(A2;B2;C2)"
        assertTrue  CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 1, 1) == "=SUM(B2;C2;D2)"

        assertTrue  CustomTableHelper.copyData ("=SUM(A1;A2;A3)", 0, 2) == "=SUM(C1;C2;C3)"
        assertTrue  CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 2, 0) == "=SUM(A3;B3;C3)"
        assertTrue  CustomTableHelper.copyData ("=SUM(A1;B1;C1)", 2, 2) == "=SUM(C3;D3;E3)"

        assertTrue  CustomTableHelper.copyData ("=SUM(\$A1;A\$2;A3)", 0, 1) == "=SUM(\$A1;B\$2;B3)"
        assertTrue  CustomTableHelper.copyData ("=SUM(A\$1;\$B1;C1)", 1, 0) == "=SUM(A\$1;\$B2;C2)"
        assertTrue  CustomTableHelper.copyData ("=SUM(\$A\$1;\$B1;C\$1)", 1, 1) == "=SUM(\$A\$1;\$B2;D\$1)"
    }
}