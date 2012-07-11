package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.table.ITableModel
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCTableOperator
import models.application.ApplicationModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.example.constraint.CopyPasteConstraint
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.TestMultiDimensionalParameterModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

abstract class MultiDimensionalParameterCopyPasteTests extends AbstractSimpleFunctionalTest {

    AbstractMultiDimensionalParameter multiDimensionalParameter

    abstract protected String getPasteContent()

    protected void doStart() {
        ULCClipboard.install()
        ULCClipboard clipboard = ULCClipboard.getClipboard()
        clipboard.content = getPasteContent()
        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        def mdp = new ConstrainedMultiDimensionalParameter([[1], [2.2], [new DateTime()], [true], ["text"], ['hierarchy component']], ['integer', 'double', 'date', 'boolean', 'string', 'marker'], ConstraintsFactory.getConstraints(CopyPasteConstraint.IDENTIFIER))
        Model simulationModel = new ApplicationModel()
        simulationModel.init()
        simulationModel.injectComponentNames()

        def component = simulationModel.dynamicComponent.createDefaultSubComponent()
        component.name = "subNewComponent"
        simulationModel.dynamicComponent.addSubComponent(component)

        ParameterHolder holder = ParameterHolderFactory.getHolder("path", 0, mdp)
        Parameterization parameterization = new Parameterization("")
        parameterization.addParameter(holder)

        def node = ParameterizationNodeFactory.getNode(holder.path, parameterization, simulationModel)
        MultiDimensionalParameterModel model = new TestMultiDimensionalParameterModel(null, node, 1)
        multiDimensionalParameter = model.multiDimensionalParameterInstance
        frame.contentPane = new MultiDimensionalParameterView(model).content
        frame.visible = true
    }

    abstract protected void selectCell(ULCTableOperator table)

    abstract protected void assertTable(ITableModel model)

    void testPaste() {

        ULCFrameOperator frame = new ULCFrameOperator("test")
        ULCTableOperator table = new ULCTableOperator(frame)
        selectCell(table)

        table.pushKey(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK)

        assertTable(table.getULCTable().model)

    }


}
