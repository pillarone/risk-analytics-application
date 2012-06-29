package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTableOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.example.constraint.LinePercentage
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.TestMultiDimensionalParameterModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.components.ComponentUtils

class ConstrainedMultiDimensionalParameterViewTests extends AbstractSimpleFunctionalTest {

    AbstractMultiDimensionalParameter multiDimensionalParameter

    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        def mdp = new ConstrainedMultiDimensionalParameter([['hierarchyComponent', 'hierarchyComponent', 'hierarchyComponent'], [new Integer(1), 2d, 3d]], ['line', 'percentage'], ConstraintsFactory.getConstraints(LinePercentage.IDENTIFIER))
        Model simulationModel = new ApplicationModel()
        simulationModel.init()
        simulationModel.injectComponentNames()

        def component = simulationModel.dynamicComponent.createDefaultSubComponent()
        component.name = "subNewComponent"
        simulationModel.dynamicComponent.addSubComponent(component)

        def node = ParameterizationNodeFactory.getNode([ParameterHolderFactory.getHolder("path", 0, mdp)], simulationModel)
        MultiDimensionalParameterModel model = new TestMultiDimensionalParameterModel(null, node, 1)
        multiDimensionalParameter = model.multiDimensionalParameterInstance
        frame.contentPane = new MultiDimensionalParameterView(model).content
        frame.visible = true
    }

    void testOpenAndEdit() {
        ULCFrameOperator frame = new ULCFrameOperator("test")
        ULCTableOperator table = new ULCTableOperator(frame)

        def operator = table.clickForEdit(1, 1) as ULCComboBoxOperator
        assertEquals ComponentUtils.getNormalizedName("hierarchyComponent"), operator.selectedItem
        operator.selectItem ComponentUtils.getNormalizedName("subFirstComponent")

        operator = table.clickForEdit(1, 2) as ULCTextFieldOperator
        assertEquals "1", operator.getText()

        //A double data type is created (based on MDP constraint info), despite the actual value being an integer
        assertFalse operator.getUITextField().fDataType.integer
        operator.enterText("2")
    }

}
