package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCDialogOperator
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class DuplicateDynamicComponentTests extends AbstractParameterFunctionalTest {

    void testDuplicateComponent() {
        parameterization.load()
        assert 1 == parameterization.getParameters('dynamicComponent:subTest:subFirstComponent:parmValue').size()
        assert 0 == parameterization.getParameters('dynamicComponent:subDuplicatedComponent:subFirstComponent:parmValue').size()

        duplicateParameter(['dynamic Component'], 'Test', "duplicated component")
        save()
        ParameterizationDAO.withNewSession {
            Parameterization parameterization = findParameterizationById()
            parameterization.load()
            assert parameterizationVersion + 1 == parameterization.dao.version
            assert 1 == parameterization.getParameters('dynamicComponent:subTest:subFirstComponent:parmValue').size()
            assert 1 == parameterization.getParameters('dynamicComponent:subDuplicatedComponent:subFirstComponent:parmValue').size()
        }
    }

    void testDuplicateAndRemove() {
        duplicateParameter(['dynamic Component'], 'Test', "duplicate")
        save()
        ParameterizationDAO.withNewSession {
            Parameterization parameterization = findParameterizationById()
            parameterization.load()
            assert parameterizationVersion + 1 == parameterization.dao.version
            assert 1 == parameterization.getParameters('dynamicComponent:subTest:subFirstComponent:parmValue').size()
            assert 1 == parameterization.getParameters('dynamicComponent:subDuplicate:subFirstComponent:parmValue').size()
        }

        removeParameter(['dynamic Component', 'duplicate'])
        clickOk()

        save()
        ParameterizationDAO.withNewSession {
            Parameterization parameterization = findParameterizationById()
            parameterization.load()
            assert parameterizationVersion + 2 == parameterization.dao.version
            assert 1 == parameterization.getParameters('dynamicComponent:subTest:subFirstComponent:parmValue').size()
            assert 0 == parameterization.getParameters('dynamicComponent:subDuplicate:subFirstComponent:parmValue').size()
        }
        removeParameter(['dynamic Component', 'Test'])
        clickOk()

        save()
        ParameterizationDAO.withNewSession {
            Parameterization parameterization = findParameterizationById()
            parameterization.load()
            assert parameterizationVersion + 3 == parameterization.dao.version
            assert 0 == parameterization.getParameters('dynamicComponent:subTest:subFirstComponent:parmValue').size()
        }

    }

    private void clickOk() {
        ULCDialogOperator alert = new ULCDialogOperator(frame)
        ULCButtonOperator okButton = new ULCButtonOperator(alert)
        okButton.clickMouse()
    }
}
