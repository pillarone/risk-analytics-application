package org.pillarone.riskanalytics.application.ui.parameterization.model

import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ParameterViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(['Core'])
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testBuildingTree() {
        Model model = new CoreModel()
        model.init()

        ParameterizationDAO parameterizationDAO = ParameterizationDAO.findByName('CoreParameters')
        ModelStructureDAO structureDAO = ModelStructureDAO.findByName('CoreStructure')
        Parameterization parameterization = ModellingItemFactory.getParameterization(parameterizationDAO)
        ModelStructure modelStructure = ModellingItemFactory.getModelStructure(structureDAO)
        parameterization.load()
        modelStructure.load()

        ParameterViewModel parameterViewModel = new TestParameterViewModel(model, parameterization, modelStructure)

        assertNotNull "ParameterTreeBuilder created", parameterViewModel.builder
        assertNotNull "ctor builds tree root", parameterViewModel.treeRoot
        assertNotNull "ctor create TreeTableModel", parameterViewModel.treeModel
        assertNotNull "periodCount read from parameter", parameterViewModel.periodCount

        assertEquals "columnCount of TreeTableModel", parameterViewModel.periodCount + 1, parameterViewModel.treeModel.columnCount
        assertSame "root node of TreeTableModel", parameterViewModel.treeRoot, parameterViewModel.treeModel.root
    }

}

class TestParameterViewModel extends ParameterViewModel {

    public TestParameterViewModel(Model model, Parameterization parameterization, ModelStructure structure) {
        super(model, parameterization, structure);
    }

    protected changeUpdateMode(def model) {

    }


}