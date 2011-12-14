package org.pillarone.riskanalytics.application.ui.parameterization.view

import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterCompareViewModel
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.base.application.ULCTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTableOperator
import com.ulcjava.testframework.operator.ComponentByNameChooser


class MultiDimensionalParameterCompareViewTests extends AbstractP1RATTestCase {

    void testView() {
        final ULCFrameOperator frameOperator = getMainFrameOperator()

        ULCTableOperator referenceTable = new ULCTableOperator(frameOperator, new ComponentByNameChooser("referenceTable"))
        assertEquals("d", referenceTable.getValueAt(2, 2))

        ULCTableOperator comparedTable = new ULCTableOperator(frameOperator, new ComponentByNameChooser("table0"))
        assertEquals("e", comparedTable.getValueAt(2, 2))

        ULCTableOperator comparedTable2 = new ULCTableOperator(frameOperator, new ComponentByNameChooser("table1"))
        assertEquals("f", comparedTable2.getValueAt(2, 2))

    }

    @Override
    ULCComponent createContentPane() {
        AbstractMultiDimensionalParameter referenceMdp = new TableMultiDimensionalParameter([["a", "b"], ["c", "d"]], ['title1', 'title2'])
        AbstractMultiDimensionalParameter comparedMdp = new TableMultiDimensionalParameter([["a", "b"], ["c", "e"]], ['title1', 'title2'])
        AbstractMultiDimensionalParameter comparedMdp2 = new TableMultiDimensionalParameter([["a", "b"], ["c", "f"]], ['title1', 'title2'])

        MultiDimensionalParameterCompareViewModel model = new MultiDimensionalParameterCompareViewModel(referenceMdp, [comparedMdp, comparedMdp2], [new Parameterization("params 1"), new Parameterization("params 2"), new Parameterization("params 3")], 0)

        final MultiDimensionalParameterCompareView compareView = new MultiDimensionalParameterCompareView(model)

        ULCTabbedPane tabbedPane = new ULCCloseableTabbedPane()
        tabbedPane.addTab("main", compareView.content)

        return tabbedPane
    }
}
