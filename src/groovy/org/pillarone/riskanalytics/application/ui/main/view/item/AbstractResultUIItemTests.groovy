package org.pillarone.riskanalytics.application.ui.main.view.item
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractResultUIItemTests extends AbstractSimulationSettingsUIItemTest {

    @Override
    AbstractUIItem createUIItem() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Application'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Application'])
        new ResultStructureImportService().compareFilesAndWriteToDB(['Application'])

        ModellingItemFactory.clear()

        init()

        createResults("run1", 1)

        Simulation simulation1 = new Simulation("run1")
        simulation1.load()

        StochasticResultUIItem uiItem = new StochasticResultUIItem(simulation1)
        return uiItem
    }
}
