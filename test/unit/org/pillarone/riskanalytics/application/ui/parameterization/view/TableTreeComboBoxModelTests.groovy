package org.pillarone.riskanalytics.application.ui.parameterization.view

class TableTreeComboBoxModelTests extends GroovyTestCase {

    void testEquals() {
        TableTreeComboBoxModel modelA = new TableTreeComboBoxModel(['one', null])
        TableTreeComboBoxModel modelB = new TableTreeComboBoxModel(['one', null])
        TableTreeComboBoxModel modelC = new TableTreeComboBoxModel(['one', 'two'])
        TableTreeComboBoxModel modelD = new TableTreeComboBoxModel(['one', 'two'])
        assert modelA.equals(modelB)
        assert modelB.equals(modelA)
        assert modelC.equals(modelD)
        assert modelD.equals(modelC)
        assert !modelB.equals(modelC)
        assert !modelC.equals(modelB)
    }
}
