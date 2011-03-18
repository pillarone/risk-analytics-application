package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.result.view.CompareSimulationsCriteriaView
import org.pillarone.riskanalytics.application.ui.result.view.CompareSimulationsView

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareSimulationViewTests extends AbstractP1RATTestCase {

    CompareSimulationsView view
    CompareSimulationsViewModel viewModel

    public void testColumnCount() {
        ULCTableTreeOperator tableTreeOperator = getTableTreeOperatorByName("resultDescriptorTreeContent")
        assertEquals "column count does not match", 2, tableTreeOperator.getColumnCount()
        int counter = 0
        viewModel.getTreeModel().setReferencedSimulationClosure = {
            counter++
        }
        ULCComboBoxOperator comboBoxOperator = getComboBoxOperator("${CompareSimulationsCriteriaView.class.getSimpleName()}.simulationComboBox")
        comboBoxOperator.selectItem 1
        assertEquals "selected item in combobox, but no action", 1, counter

    }

    ULCComponent createContentPane() {
        viewModel = new MockCompareSimulationViewModel()
        view = new CompareSimulationsView(viewModel, null)
        return view.content;
    }
}
