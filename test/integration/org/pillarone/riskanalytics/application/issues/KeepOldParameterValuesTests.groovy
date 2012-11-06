package org.pillarone.riskanalytics.application.issues

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import java.awt.event.KeyEvent
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.testframework.operator.*
import org.pillarone.riskanalytics.application.ui.util.UIUtils

public class KeepOldParameterValuesTests extends AbstractSimpleFunctionalTest {

    Parameterization parameterization

    protected void doStart() {
        FileImportService.importModelsIfNeeded(["Core"])
        ModellingItemFactory.clear()

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        Model model = new CoreModel()
        model.initComponents()

        def dao = ParameterizationDAO.findByModelClassName(model.class.name)
        parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        dao = ModelStructureDAO.findByModelClassName(model.class.name)
        ModelStructure structure = ModellingItemFactory.getModelStructure(dao)
        structure.load()
        ParameterViewModel parameterViewModel = new ParameterViewModel(model, parameterization, structure)
        frame.setContentPane(new ParameterView(parameterViewModel).content)
        ULCClipboard.install()
        UIUtils.setRootPane(frame)
        frame.visible = true
    }

    void testChangeStrategy() {
 /*       ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))

        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))
        ULCTableTreeOperator parameterTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeContent"))
        componentTree.doExpandRow 1
        componentTree.doExpandRow 2

        parameterTree.clickForEdit(3, 0)
        ULCComboBoxOperator combo = new ULCComboBoxOperator(parameterTree)
        combo.selectItem "TYPE1"

        parameterTree.clickForEdit(4, 0)
        ULCTextFieldOperator editor = new ULCTextFieldOperator(parameterTree)
        editor.enterText "100"
        editor.typeKey((char) KeyEvent.VK_ENTER)


        parameterTree.clickForEdit(3, 0)
        combo = new ULCComboBoxOperator(parameterTree)
        combo.selectItem "TYPE2"

        assertEquals 100d, parameterTree.getValueAt(4, 0) */
    }

}