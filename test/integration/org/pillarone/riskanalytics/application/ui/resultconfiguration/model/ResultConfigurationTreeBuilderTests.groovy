package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import models.application.ApplicationModel
import models.core.CoreModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

import static org.junit.Assert.*

class ResultConfigurationTreeBuilderTests {

    @Before
    void setUp() {
        LocaleResources.setTestMode(true)
    }

    @After
    void tearDown() {
        LocaleResources.setTestMode(false)
    }

    @Test
    void testBuildTree() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])

        ResultConfiguration configuration = new ResultConfiguration("ApplicationResultConfiguration", ApplicationModel)
        configuration.load()
        ModelStructure modelStructure = new ModelStructure("ApplicationStructure")
        modelStructure.load()

        Model model = new ApplicationModel()
        model.init()


        ResultConfigurationTreeBuilder builder = new ResultConfigurationTreeBuilder(model, modelStructure, configuration)
        assertNotNull builder.root

        ITableTreeNode exampleOutput = builder.root.getChildAt(0)
        ResultConfigurationTableTreeNode outClaims = exampleOutput.getChildAt(0)

        //todo fja check why outClaims.collector is null
//        assertNotNull outClaims.collector
//        assertEquals SingleValueCollectingModeStrategy.IDENTIFIER, outClaims.collector.mode.identifier
    }

    @Test
    void testBuildTreeWithPacketOutput() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Core'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Core'])

        ResultConfiguration configuration = new ResultConfiguration("CoreResultConfiguration", ApplicationModel)
        configuration.load()
        ModelStructure modelStructure = new ModelStructure("CoreStructure")
        modelStructure.load()

        Model model = new CoreModel()
        model.init()

        ResultConfigurationTreeBuilder builder = new ResultConfigurationTreeBuilder(model, modelStructure, configuration)
        assertNotNull builder.root


        assertTrue builder.root.childCount == 0
    }


    @Test
    void testDynamicOutputProperties() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])

        ResultConfiguration configuration = new ResultConfiguration("ApplicationResultConfiguration", ApplicationModel)
        configuration.load()
        ModelStructure modelStructure = new ModelStructure("ApplicationStructure")
        modelStructure.load()

        Model model = new ApplicationModel()
        model.init()

        ITableTreeNode root = new ResultConfigurationTreeBuilder(model, modelStructure, configuration).root
        assertNotNull root

        //exactly 2 root components contain collectable output
        //sub components containg no out properties or only out props which are no SVP or MVP should not be here
        assertEquals 2, root.childCount

        ITableTreeNode dynamicComponent = root.getChildAt(0)
        //1 out value & subcomponents, NOT 3 because only SVP & MVP should be collectable
        assertEquals 2, dynamicComponent.childCount
        ITableTreeNode dynamicSub = dynamicComponent.getChildAt(1)
        assertEquals 2, dynamicSub.childCount //subFirstComponent subSecondComponent
    }

    @Test
    void testDynamicPropertiesOrder() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])

        ResultConfiguration configuration = new ResultConfiguration("ApplicationResultConfiguration", ApplicationModel)
        configuration.load()
        ModelStructure modelStructure = new ModelStructure("ApplicationStructure")
        modelStructure.load()

        Model model = new ApplicationModel()
        model.init()


        ITableTreeNode root = new ResultConfigurationTreeBuilder(model, modelStructure, configuration).root
        assertNotNull root

        ITableTreeNode dynamicSub = root.getChildAt(0)
        assertEquals 2, dynamicSub.childCount
        assertTrue dynamicSub.getChildAt(0) instanceof ResultConfigurationTableTreeNode
        assertTrue dynamicSub.getChildAt(1) instanceof ComponentTableTreeNode
    }

}
