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
import org.pillarone.riskanalytics.application.example.model.ExtendedCoreModel

/**
 * @author fouad jaada
 */

public class CompareParameterizationTableTreeModelTests extends GroovyTestCase {

    Parameterization parameterization1
    Parameterization parameterization2
    ModelStructure structure
    Model model

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }


    void prepareModel() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Core'])

        structure = ModellingItemFactory.getModelStructure(ModelStructureDAO.findByName('CoreStructure'))

        model = new ExtendedCoreModel()
        model.init()

        parameterization1 = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreParameters'))
        parameterization1.load()
        structure.load()

        parameterization2 = ModellingItemFactory.getParameterization(ParameterizationDAO.findByName('CoreAlternativeParameters'))
        parameterization2.load()

        structure.load()
    }


    void testCompareTwoParameterizationsOnePeriod() {
        prepareModel()
        assertNotNull parameterization1
        assertNotNull parameterization2
        List parameterizations = new ArrayList()
        parameterizations.add(parameterization1)
        parameterizations.add(parameterization2)

        ParameterizationTreeBuilder builder = new ParameterizationTreeBuilder(model, structure, parameterization1)

        CompareParameterizationTableTreeModel cpttm = new CompareParameterizationTableTreeModel(builder, parameterizations)

        assertEquals "Name", cpttm.getColumnName(0)
        assertTrue cpttm.getColumnName(1).endsWith("CoreParameters v1")
        assertTrue cpttm.getColumnName(2).endsWith("CoreAlternativeParameters v1")

        assertEquals 0, cpttm.getParameterizationIndex(0)
        assertEquals 0, cpttm.getParameterizationIndex(1)
        assertEquals 1, cpttm.getParameterizationIndex(2)

        assertEquals cpttm.columnCount, 3

    }

}
