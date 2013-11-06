package org.pillarone.riskanalytics.application.ui.parameterization.model

import models.core.CoreModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class ParameterViewModelTests {
    private ParameterViewModel parameterViewModel

    @Before
    void setUp() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(['Core'])
        Model model = new CoreModel()
        model.init()

        ParameterizationDAO parameterizationDAO = ParameterizationDAO.findByName('CoreParameters')
        ModelStructureDAO structureDAO = ModelStructureDAO.findByName('CoreStructure')
        Parameterization parameterization = ModellingItemFactory.getParameterization(parameterizationDAO)
        ModelStructure modelStructure = ModellingItemFactory.getModelStructure(structureDAO)
        parameterization.load()
        modelStructure.load()

        parameterViewModel = new TestParameterViewModel(model, parameterization, modelStructure)
    }

    @After
    void tearDown() {
        LocaleResources.clearTestMode()
    }

    @Test
    void testBuildingTree() {
        assertNotNull "ParameterTreeBuilder created", parameterViewModel.builder
        assertNotNull "ctor builds tree root", parameterViewModel.getTreeModel().getRoot()
        assertNotNull "ctor create TreeTableModel", parameterViewModel.getTreeModel().getRoot()
        assertNotNull "periodCount read from parameter", parameterViewModel.periodCount
        assertEquals "columnCount of TreeTableModel", parameterViewModel.periodCount + 1, parameterViewModel.treeModel.columnCount
    }

    @Test
    void testComment_NonExistingPath() {
        Comment comment = new Comment('nonExisting', 1)
        assert !CommentAndErrorView.findNodeForPath(parameterViewModel.root, comment.path)
        parameterViewModel.commentsChanged([comment])

    }

    @Test
    void testComment_ExistingPath() {
        Comment comment = new Comment('Core', 1)
        SimpleTableTreeNode treeNode = CommentAndErrorView.findNodeForPath(parameterViewModel.root, comment.path) as SimpleTableTreeNode
        parameterViewModel.commentsChanged([comment])
        assert 1 == treeNode.comments.size()
    }
}

class TestParameterViewModel extends ParameterViewModel {

    public TestParameterViewModel(Model model, Parameterization parameterization, ModelStructure structure) {
        super(model, parameterization, structure);
    }

    protected changeUpdateMode(def model) {

    }


}