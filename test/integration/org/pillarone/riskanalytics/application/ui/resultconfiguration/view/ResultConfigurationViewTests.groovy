package org.pillarone.riskanalytics.application.ui.resultconfiguration.view

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.components.ComponentUtils

class ResultConfigurationViewTests extends AbstractSimpleFunctionalTest {

    ResultConfiguration configuration
    ModelStructure structure

    protected void doStart() {

        ULCFrame frame = new ULCFrame("Test")
        frame.name = "Test"
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE

        new ResultConfigurationImportService().compareFilesAndWriteToDB(['ApplicationResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['ApplicationStructure'])

        configuration = new ResultConfiguration("ApplicationResultConfiguration")
        configuration.modelClass = ApplicationModel
        configuration.load()

        structure = new ModelStructure("ApplicationStructure")
        structure.load()

        Model model = new ApplicationModel()
        model.init()

        ResultConfigurationView view = new ResultConfigurationView(new ResultConfigurationViewModel(model, configuration, structure))

        frame.contentPane = view.content
        frame.visible = true
    }

    void testView() {
        ULCFrameOperator frame = new ULCFrameOperator("Test")
        assertNotNull frame

        ULCTableTreeOperator rowHeader = new ULCTableTreeOperator(frame, new ComponentByNameChooser("resultConfigurationTreeRowHeader"))
        rowHeader.doExpandRow 1
        rowHeader.selectCell(0, 0)
        //Also tests if the correct paths are expanded
        assertEquals ComponentUtils.getNormalizedName("Application"), rowHeader.getValueAt(rowHeader.selectedRow, 0)
        assertEquals ComponentUtils.getNormalizedName("dynamicComponent"), rowHeader.getValueAt(rowHeader.selectedRow + 1, 0)
        assertEquals ComponentUtils.getNormalizedName("outValue1"), rowHeader.getValueAt(rowHeader.selectedRow + 2, 0)
        assertEquals ComponentUtils.getNormalizedName("subsubcomponents"), rowHeader.getValueAt(rowHeader.selectedRow + 3, 0)

        ULCTableTreeOperator viewPort = new ULCTableTreeOperator(frame, new ComponentByNameChooser("resultConfigurationTreeContent"))
        viewPort.selectCell(2, 0)
        assertEquals "Not collected", viewPort.getValueAt(viewPort.selectedRow, viewPort.selectedColumn)
        assertEquals "", viewPort.getValueAt(viewPort.selectedRow + 1, viewPort.selectedColumn)

        ULCComboBoxOperator comboBoxOperator = viewPort.clickForEdit(2, 0)
        comboBoxOperator.selectItem "Aggregated"

        assertEquals "Aggregated", viewPort.getValueAt(viewPort.selectedRow, viewPort.selectedColumn)
    }

}