package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import models.application.ApplicationModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.DynamicComposedComponentTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import static org.junit.Assert.*

class ParameterizationTreeBuilderTests {


    @Before
    void setUp() {
        LocaleResources.setTestMode()
    }

    @After
    void tearDown() {
        LocaleResources.clearTestMode()
    }

    @Test
    void testTreeStructure() {

        Parameterization parameterization
        ModelStructure structure
        Model model

        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])

        model = new ApplicationModel()
        model.init()

        parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('ApplicationParameters'))
        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('ApplicationStructure'))
        parameterization.load()
        structure.load()

        ParameterizationTreeBuilder builder = new ParameterizationTreeBuilder(model, structure, parameterization)
        def root = builder.root
        assertNotNull root

        assertEquals 5, root.childCount
        def structureNode = root.getChildAt(0)
        assertEquals ComponentUtils.getNormalizedName("hierarchyLevel"), structureNode.displayName

        assertEquals 1, structureNode.childCount
        def componentNode = structureNode.getChildAt(0)
        assertEquals 1, componentNode.childCount

        def parameterNode = componentNode.getChildAt(0)
        assertEquals ComponentUtils.getNormalizedName("parmValue"), parameterNode.displayName
        assertTrue parameterNode instanceof SimpleValueParameterizationTableTreeNode

        def popNode = root.getChildAt(2).getChildAt(2)
        assertEquals ComponentUtils.getNormalizedName("parmNestedMdp"), popNode.displayName
        assertTrue popNode instanceof ParameterObjectParameterTableTreeNode
        assertEquals 2, popNode.childCount

        assertTrue popNode.getChildAt(0) instanceof ParameterizationClassifierTableTreeNode
        assertTrue popNode.getChildAt(1) instanceof MultiDimensionalParameterizationTableTreeNode
    }

    @Test
    void testDynamicTreeStructure() {

        Parameterization parameterization
        ModelStructure structure
        Model model

        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])

        model = new ApplicationModel()
        model.init()

        parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('ApplicationParameters'))
        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('ApplicationStructure'))
        parameterization.load()
        structure.load()

        ParameterizationTreeBuilder builder = new ParameterizationTreeBuilder(model, structure, parameterization)
        def root = builder.root
        assertNotNull root

        //test dynamic lobs (dynamic components with sub components)

        assertEquals 5, root.childCount
        def dynamicComponentNode = root.getChildAt(3)
        assertEquals ComponentUtils.getNormalizedName("dynamicComponent"), dynamicComponentNode.displayName

        assertEquals 1, dynamicComponentNode.childCount
        def dynamicSubcomponentNode = dynamicComponentNode.getChildAt(0)
        assertEquals 'Test', dynamicSubcomponentNode.displayName
        assertEquals 4, dynamicSubcomponentNode.childCount

        def nestedComponentNode = dynamicSubcomponentNode.getChildAt(0)
        assertTrue nestedComponentNode instanceof ComponentTableTreeNode
        assertEquals ComponentUtils.getNormalizedName("subFirstComponent"), nestedComponentNode.displayName
        assertEquals 1, nestedComponentNode.childCount

        nestedComponentNode = dynamicSubcomponentNode.getChildAt(1)
        assertTrue nestedComponentNode instanceof ComponentTableTreeNode
        assertEquals ComponentUtils.getNormalizedName("subSecondComponent"), nestedComponentNode.displayName
        assertEquals 1, nestedComponentNode.childCount

        assertTrue dynamicSubcomponentNode.getChildAt(2) instanceof ParameterizationTableTreeNode
        assertTrue dynamicSubcomponentNode.getChildAt(3) instanceof ParameterizationTableTreeNode
    }

    @Test
    void testNestedSubComponents() {
        Parameterization parameterization
        ModelStructure structure
        Model model

        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])

        model = new ApplicationModel()
        model.init()

        parameterization = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('ApplicationParameters'))
        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('ApplicationStructure'))
        parameterization.load()
        structure.load()

        ParameterizationTreeBuilder builder = new ParameterizationTreeBuilder(model, structure, parameterization)
        def root = builder.root
        assertNotNull root

        ITableTreeNode composedComponentNode = root.getChildAt(4)
        assertEquals 1, composedComponentNode.childCount

        DynamicComposedComponentTableTreeNode dynamicComposedComponentNode = composedComponentNode.getChildAt(0)
        ComponentTableTreeNode subComponentNode = dynamicComposedComponentNode.getChildAt(0)

        assertEquals 4, subComponentNode.childCount
        assertEquals "Nested", subComponentNode.displayName
    }
}