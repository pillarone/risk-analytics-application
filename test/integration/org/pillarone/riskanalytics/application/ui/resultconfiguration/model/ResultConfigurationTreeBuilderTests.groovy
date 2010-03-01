package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import models.application.ApplicationModel
import models.core.CoreModel
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class ResultConfigurationTreeBuilderTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testBuildTree() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])

        ResultConfiguration configuration = new ResultConfiguration("ApplicationResultConfiguration")
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

    void testBuildTreeWithPacketOutput() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['CoreResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])

        ResultConfiguration configuration = new ResultConfiguration("CoreResultConfiguration")
        configuration.load()
        ModelStructure modelStructure = new ModelStructure("CoreStructure")
        modelStructure.load()

        Model model = new CoreModel()
        model.init()

        ResultConfigurationTreeBuilder builder = new ResultConfigurationTreeBuilder(model, modelStructure, configuration)
        assertNotNull builder.root


        assertTrue builder.root.childCount == 0
    }


    void testDynamicOutputProperties() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])

        ResultConfiguration configuration = new ResultConfiguration("ApplicationResultConfiguration")
        configuration.load()
        ModelStructure modelStructure = new ModelStructure("ApplicationStructure")
        modelStructure.load()

        Model model = new ApplicationModel()
        model.init()

        ITableTreeNode root = new ResultConfigurationTreeBuilder(model, modelStructure, configuration).root
        assertNotNull root

        ITableTreeNode dynamicSub = root.getChildAt(0).getChildAt(1)
        assertEquals 2, dynamicSub.childCount //subFirstComponent subSecondComponent
    }

    void testDynamicPropertiesOrder() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])

        ResultConfiguration configuration = new ResultConfiguration("ApplicationResultConfiguration")
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
