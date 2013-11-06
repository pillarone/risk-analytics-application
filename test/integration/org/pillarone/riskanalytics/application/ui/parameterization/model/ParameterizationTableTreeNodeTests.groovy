package org.pillarone.riskanalytics.application.ui.parameterization.model

import models.application.ApplicationModel
import models.core.CoreModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.example.component.ExampleParameterComponent
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.example.component.ExampleInputOutputComponent
import org.pillarone.riskanalytics.core.example.component.ExampleOutputComponent
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ParameterInjector
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

import static org.junit.Assert.*

class ParameterizationTableTreeNodeTests {

    @Before
    void setUp() {
        LocaleResources.setTestMode()
    }

    @After
    void tearDown() {
        LocaleResources.clearTestMode()
    }

    @Test
    void testGetValueAt() {
        Model model = new CoreModel()

        List parameters = new ArrayList()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, 1)
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, 2)
        def node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)

        assertEquals ComponentUtils.getNormalizedName("parmName"), node.getValueAt(0)
        assertEquals 1, node.getValueAt(1)
        assertEquals 2, node.getValueAt(2)

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, 1.2)
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, 2.2)
        node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)

        assertEquals ComponentUtils.getNormalizedName("parmName"), node.getValueAt(0)
        assertEquals 1.2, node.getValueAt(1)
        assertEquals 2.2, node.getValueAt(2)

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, 'text1')
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, 'text2')
        node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)

        assertEquals ComponentUtils.getNormalizedName("parmName"), node.getValueAt(0)
        assertEquals 'text1', node.getValueAt(1)
        assertEquals 'text2', node.getValueAt(2)

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, ExampleEnum.FIRST_VALUE)
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, ExampleEnum.SECOND_VALUE)
        node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)

        assertEquals ComponentUtils.getNormalizedName("parmName"), node.getValueAt(0)
        assertEquals 'FIRST_VALUE', node.getValueAt(1)
        assertEquals 'SECOND_VALUE', node.getValueAt(2)
    }

    @Test
    void testSetValueAt() {
        Model model = new CoreModel()

        List parameters = new ArrayList()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, 1)
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, 2)
        def node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)

        node.setValueAt(3, 1)
        node.setValueAt(4, 2)
        assertEquals 3, parameters[0].businessObject
        assertEquals 4, parameters[1].businessObject

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, 1.2)
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, 2.2)
        node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)

        node.setValueAt(3.3, 1)
        node.setValueAt(4.4, 2)
        assertEquals 3.3, parameters[0].businessObject
        assertEquals 4.4, parameters[1].businessObject

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, 'text1')
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, 'text2')
        node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)

        node.setValueAt('text3', 1)
        node.setValueAt('text4', 2)
        assertEquals 'text3', parameters[0].businessObject
        assertEquals 'text4', parameters[1].businessObject

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 0, ExampleEnum.FIRST_VALUE)
        parameters << ParameterHolderFactory.getHolder('path:to:parmName', 1, ExampleEnum.SECOND_VALUE)
        node = ParameterizationNodeFactory.getNode('path:to:parmName', createParameterization(parameters), model)
        node.parent = new ComponentTableTreeNode(new ExampleInputOutputComponent(), 'propName')

        node.setValueAt('SECOND_VALUE', 1)
        node.setValueAt('FIRST_VALUE', 2)
        assertEquals ExampleEnum.SECOND_VALUE, parameters[0].businessObject
        assertEquals ExampleEnum.FIRST_VALUE, parameters[1].businessObject
    }

    @Test
    void testNullValues_PMO353() {
        def mdp = new SimpleMultiDimensionalParameter([1, 2, 3])
        def parameters = []
        parameters << ParameterHolderFactory.getHolder('testPath', 1, mdp)

        def node = ParameterizationNodeFactory.getNode('testPath', createParameterization(parameters), new CoreModel())

        assertNull node.getValueAt(1)
        assertNotNull node.getValueAt(2)
    }

    @Test
    void testConstrainedStringNode() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Application"])
        Model model = new ApplicationModel()
        model.init()
        model.injectComponentNames()

        Parameterization p = new Parameterization("ApplicationParameters")
        p.load()

        new ParameterInjector(p.toConfigObject()).injectConfiguration(model)

        Parameterization parameterization = createParameterization([ParameterHolderFactory.getHolder("path", 0, new ConstrainedString(ITestComponentMarker, "Component Default"))])
        ConstrainedStringParameterizationTableTreeNode node = ParameterizationNodeFactory.getNode("path", parameterization, model)
        node.setParent(new ComponentTableTreeNode(null, "name"))

        //Test PMO-555: node must be editable and the value not null if it's initialized with a component default
        assertTrue node.isCellEditable(1)
        assertNotNull node.getValueAt(1)

        //ART-83
        String stringValue = parameterization.getParameterHolder(node.parameterPath, 0).businessObject.stringValue
        assertTrue node.values.contains(node.nameToNormalized.get(stringValue))

        assertNotNull node
        assertEquals 5, node.getValues().size()

        node.addComponent(new ExampleOutputComponent(name: "newExampleOutputComponent"))

        assertEquals 6, node.getValues().size()

        node.addComponent(new ExampleParameterComponent(name: "newExampleParameterComponent"))
        //Test PMO-540: check marker class when adding components
        assertEquals 6, node.getValues().size()
    }

    @Test
    void testConstrainedStringNode_PMO1562() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Application"])
        Model model = new ApplicationModel()

        Parameterization parameterization = createParameterization([ParameterHolderFactory.getHolder("path", 0, new ConstrainedString(ITestComponentMarker, "Component Default"))])
        ConstrainedStringParameterizationTableTreeNode node = ParameterizationNodeFactory.getNode("path", parameterization, model)
        node.setParent(new ComponentTableTreeNode(null, "name"))

        assertEquals "", parameterization.getParameterHolder(node.parameterPath, 0).businessObject.stringValue

        ExampleOutputComponent component = new ExampleOutputComponent(name: "example1")
        node.addComponent(component)

        assertEquals "example1", parameterization.getParameterHolder(node.parameterPath, 0).businessObject.stringValue

        ExampleOutputComponent component2 = new ExampleOutputComponent(name: "example2")
        node.addComponent(component2)
        assertEquals "example1", parameterization.getParameterHolder(node.parameterPath, 0).businessObject.stringValue

        node.setValueAt(ComponentUtils.getNormalizedName("example2"), 1)
        assertEquals "example2", parameterization.getParameterHolder(node.parameterPath, 0).businessObject.stringValue

        node.removeComponent(component2)
        assertEquals "example1", parameterization.getParameterHolder(node.parameterPath, 0).businessObject.stringValue

        node.removeComponent(component)
        assertEquals "", parameterization.getParameterHolder(node.parameterPath, 0).businessObject.stringValue


    }

    private Parameterization createParameterization(List<ParameterHolder> parameterHolders) {
        Parameterization parameterization = new Parameterization("")
        parameterHolders.each { parameterization.addParameter(it) }

        return parameterization
    }

}
