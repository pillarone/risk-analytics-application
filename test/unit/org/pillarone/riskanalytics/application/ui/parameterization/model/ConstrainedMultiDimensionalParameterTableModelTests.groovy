package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.example.constraint.EnumConstraint
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

class ConstrainedMultiDimensionalParameterTableModelTests extends GroovyTestCase {

    @Override
    protected void setUp() {
        LocaleResources.setTestMode()
    }

    @Override
    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testEnumI18N() {
        ConstrainedMultiDimensionalParameter mdp = new ConstrainedMultiDimensionalParameter([ExampleEnum.FIRST_VALUE.toString(), ExampleEnum.SECOND_VALUE.toString()], ['enum'], new EnumConstraint())
        ConstrainedMultiDimensionalParameterTableModel model = new ConstrainedMultiDimensionalParameterTableModel(mdp, true)

        assertEquals("First value", model.getValueAt(1, 1))
        assertEquals("SECOND_VALUE", model.getValueAt(2, 1))

        model.setValueAt("First value", 2, 1)
        assertEquals("FIRST_VALUE", mdp.getValueAt(2, 0))

        List values = model.getPossibleValues(1, 1)
        assertTrue(values.contains("First value"))
        assertTrue(values.contains("SECOND_VALUE"))
    }
}
