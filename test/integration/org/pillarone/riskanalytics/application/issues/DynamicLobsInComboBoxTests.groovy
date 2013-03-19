package org.pillarone.riskanalytics.application.issues

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTableOperator
import models.core.CoreModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.TestMultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterView
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.parameter.MultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class DynamicLobsInComboBoxTests extends AbstractSimpleFunctionalTest {

    AbstractMultiDimensionalParameter multiDimensionalParameter

    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        def mdp = new ComboBoxTableMultiDimensionalParameter(['exampleOutputComponent', 'hierarchyComponent'], ['title'], ITestComponentMarker)
        def param = new MultiDimensionalParameter(path: 'path', periodIndex: 0)
        param.parameterInstance = mdp
        param.save(flush: true)

        ParameterHolder holder = ParameterHolderFactory.getHolder(param)
        Parameterization parameterization = new Parameterization("DynamicLobsInComboBoxTests")
        parameterization.addParameter(holder)

        CoreModel simulationModel = new CoreModel()
        simulationModel.init()
        simulationModel.injectComponentNames()

        def node = ParameterizationNodeFactory.getNode("path", parameterization, simulationModel)
        MultiDimensionalParameterModel model = new TestMultiDimensionalParameterModel(null, node, 1)
        multiDimensionalParameter = model.multiDimensionalParameterInstance

        frame.contentPane = new MultiDimensionalParameterView(model).content
        frame.visible = true
    }

    void testChangeDimension() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        ULCTableOperator table = new ULCTableOperator(frame)

        def operator = table.clickForEdit(1, 1) as ULCComboBoxOperator
        operator.selectItem 'example output component'
        operator = table.clickForEdit(1, 1) as ULCComboBoxOperator
        operator.selectItem 'hierarchy output component'
    }

}