package org.pillarone.riskanalytics.application.ui.resultconfiguration.view

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import models.core.CoreModel
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class ResultConfigurationViewTests extends AbstractSimpleFunctionalTest {

    ResultConfiguration configuration
    ModelStructure structure

    protected void doStart() {

        new ResultConfigurationImportService().compareFilesAndWriteToDB(['CoreResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])

        configuration = new ResultConfiguration("CoreResultConfiguration")
        structure = new ModelStructure("CoreStructure")

        configuration.load()
        structure.load()

        ULCFrame frame = new ULCFrame("Test")
        frame.name = "Test"
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE

        Model model = new CoreModel()
        model.init()

        ResultConfigurationView view = new ResultConfigurationView(new ResultConfigurationViewModel(model, configuration, structure))

        frame.contentPane = view.content
        frame.visible = true
    }

    void testView() {
        ULCFrameOperator frame = new ULCFrameOperator("Test")
        assertNotNull frame

        ULCTableTreeOperator rowHeader = new ULCTableTreeOperator(frame, new ComponentByNameChooser("resultConfigurationTreeRowHeader"))
        rowHeader.selectCell(3, 0)
        //Also tests if the correct paths are expanded
        assertEquals "value1", rowHeader.getValueAt(rowHeader.selectedRow, rowHeader.selectedColumn)
        assertEquals "value2", rowHeader.getValueAt(rowHeader.selectedRow + 1, rowHeader.selectedColumn)

        ULCTableTreeOperator viewPort = new ULCTableTreeOperator(frame, new ComponentByNameChooser("resultConfigurationTreeContent"))
        viewPort.selectCell(3, 0)
        assertEquals "Single", viewPort.getValueAt(viewPort.selectedRow, viewPort.selectedColumn)
        assertEquals "Single", viewPort.getValueAt(viewPort.selectedRow + 1, viewPort.selectedColumn)

        ULCComboBoxOperator comboBoxOperator = viewPort.clickForEdit(3, 0)
        comboBoxOperator.selectItem "Aggregated"

        assertEquals "Aggregated", viewPort.getValueAt(viewPort.selectedRow, viewPort.selectedColumn)
    }

}