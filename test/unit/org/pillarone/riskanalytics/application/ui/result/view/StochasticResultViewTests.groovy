package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.result.model.MockResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class StochasticResultViewTests extends AbstractP1RATTestCase {

    ResultViewModel resultViewModel
    StochasticResultView resultView

    @Override
    ULCComponent createContentPane() {
        resultViewModel = new MockResultViewModel()
        resultView = new StochasticResultView(null)
        resultView.metaClass.getResultSettingView = {-> new ULCBoxPane()}
        resultView.setModel resultViewModel

        return resultView.content
    }

    public void testView() {
        assertNotNull getTableTreeOperatorByName("resultDescriptorTreeRowHeader")

        ULCComboBoxOperator profitComboBox = getComboBoxOperator("profitComboBox")
        assertNotNull profitComboBox
        assertEquals 2, profitComboBox.getItemCount()

        assertNotNull getButtonOperator("percentileButton")
        assertNotNull getButtonOperator("varButton")
        assertNotNull getButtonOperator("tvarButton")
    }
}
