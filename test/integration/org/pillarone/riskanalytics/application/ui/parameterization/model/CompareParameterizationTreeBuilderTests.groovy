package org.pillarone.riskanalytics.application.ui.parameterization.model

import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad jaada
 */

public class CompareParameterizationTreeBuilderTests extends GroovyTestCase {

    ModelStructure structure
    Model model
    Parameterization parameterization1
    Parameterization parameterization2

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void prepareModel() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreAlternativeParameters'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])

        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('CoreStructure'))

        model = new CoreModel()
        model.init()

        parameterization1 = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreParameters'))
        parameterization1.load()
        structure.load()

        parameterization2 = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreAlternativeParameters'))
        parameterization2.load()

        structure.load()
    }

    void testTreeStructure() {

        prepareModel()
        assertNotNull parameterization1
        assertNotNull parameterization2
        List parameterizations = new ArrayList()
        parameterizations.add(parameterization1)
        parameterizations.add(parameterization2)

        CompareParameterizationTreeBuilder builder = new CompareParameterizationTreeBuilder(model, structure, parameterization1, parameterizations)
        def root = builder.root
        assertNotNull root

        assertEquals 2, root.childCount
        assertTrue builder.parameterizations.size() == 2
        assertTrue builder.minPeriod == 1
        assertEquals builder.item, parameterization1

    }


}
