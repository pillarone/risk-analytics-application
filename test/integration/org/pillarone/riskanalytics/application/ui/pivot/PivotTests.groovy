package org.pillarone.riskanalytics.application.ui.pivot

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.pillarone.riskanalytics.application.ui.pivot.view.CustomTableView
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableHelper


class PivotTests extends AbstractSimpleFunctionalTest {


    @Override
    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")

        CustomTableView view = new CustomTableView()
        view.parent = frame

        frame.setSize(800, 600)
        frame.setContentPane(view.content)
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

    void testFrame() {
        ULCFrameOperator frameOperator = new ULCFrameOperator("test")
        sleep 1800000
    }
}
