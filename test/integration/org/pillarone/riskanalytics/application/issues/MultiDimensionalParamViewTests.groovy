package org.pillarone.riskanalytics.application.issues

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTabbedPaneOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class MultiDimensionalParamViewTests extends AbstractSimpleFunctionalTest {

    Parameterization parameterization

    protected void doStart() {
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        Model model = new ApplicationModel()
        model.initComponents()

        def dao = ParameterizationDAO.findByModelClassName(model.class.name)
        parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        dao = ModelStructureDAO.findByModelClassName(model.class.name)
        ModelStructure structure = ModellingItemFactory.getModelStructure(dao)
        structure.load()
        ParameterViewModel parameterViewModel = new ParameterViewModel(model, parameterization, structure)
        ParameterView view = new ParameterView(parameterViewModel, new RiskAnalyticsMainModel())
        view.model.treeModel.root.childCount.times {
            view.tree.expandPath new TreePath([view.model.treeModel.root, view.model.treeModel.root.getChildAt(it)] as Object[])
        }

        frame.setContentPane(view.content)
        ULCClipboard.install()
        UIUtils.setRootPane(frame)
        frame.visible = true
    }

    void testOpeningMultiDimensionalParam() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))

        ULCTabbedPaneOperator tabbedPane = new ULCTabbedPaneOperator(frameOperator)
        assertEquals 1, tabbedPane.tabCount

        ULCTableTreeOperator parameterTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeContent"))
        parameterTree.clickForEdit(4, 0)

        assertEquals "not tab opened for MDP", 2, tabbedPane.tabCount

    }
}