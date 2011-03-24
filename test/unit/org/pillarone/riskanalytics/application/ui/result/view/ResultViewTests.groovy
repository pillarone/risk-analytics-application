package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.result.model.MockResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultViewTests extends AbstractP1RATTestCase {

    ResultViewModel resultViewModel
    ResultView resultView

    @Override
    ULCComponent createContentPane() {
        resultViewModel = new MockResultViewModel()
        resultView = new ResultView(resultViewModel)

        return resultView.content
    }

    public void testView() {
        assertNotNull getTableTreeOperatorByName("resultDescriptorTreeRowHeader")
    }


}
