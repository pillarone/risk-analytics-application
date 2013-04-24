package org.pillarone.riskanalytics.application.ui.resultconfiguration.view

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import org.pillarone.riskanalytics.core.fileimport.ModelFileImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

class OpenResultConfigurationTests extends AbstractFunctionalTestCase {

    @Override
    protected void setUp() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])
        new ModelFileImportService().compareFilesAndWriteToDB(['Application'])
        ModelRegistry.instance.clear()
        ModelRegistry.instance.loadFromDatabase()
        super.setUp()
    }

    void testSelectResultConfigurationFromTree() {
        ULCTableTreeOperator tree = getSelectionTableTreeRowHeader()
        verifyPersistentState(4)

        pushKeyOnPath(tree, tree.findPath(['Application', 'Result Templates', 'ApplicationResultConfiguration'] as String[]),KeyEvent.VK_ENTER,0)
        //user clicks on element again.
        pushKeyOnPath(tree, tree.findPath(['Application', 'Result Templates', 'ApplicationResultConfiguration'] as String[]),KeyEvent.VK_ENTER,0)
        def collectors = new ULCTableTreeOperator(getMainFrameOperator(),new ComponentByNameChooser('resultConfigurationTreeContent'))
        assert collectors
        ULCComboBoxOperator comboboxOperator = collectors.clickForEdit(2,0)
        comboboxOperator.selectItem('Single')
        comboboxOperator.pressKey(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)

        verifyPersistentState(5)
    }

    private void verifyPersistentState(int collectorCount) {
        ResultConfigurationDAO.withNewSession {
            ResultConfigurationDAO resultConfiguration = ResultConfigurationDAO.findByName('ApplicationResultConfiguration')
            assert resultConfiguration
            assert collectorCount == resultConfiguration.collectorInformation.size()
            def pathNames = resultConfiguration.collectorInformation.path.pathName
        }


    }
}
