package org.pillarone.riskanalytics.application.ui.parameterization.model

import models.core.CoreModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.example.parameter.ExampleParameterObjectClassifier
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

class ParameterizationNodeFactoryTests extends GroovyTestCase {

    Model model

    void setUp() {
        LocaleResources.setTestMode()
        model = new CoreModel()
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testGetNode() {

        List parameters = new ArrayList()
        parameters << ParameterHolderFactory.getHolder('path', 0, 0)
        parameters << ParameterHolderFactory.getHolder('path', 1, 0)
        def node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, node.childCount

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path', 0, 0d)
        parameters << ParameterHolderFactory.getHolder('path', 1, 0d)
        node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, node.childCount

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path', 0, 'val')
        parameters << ParameterHolderFactory.getHolder('path', 1, 'val')
        node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, node.childCount

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path', 0, ExampleEnum.FIRST_VALUE)
        parameters << ParameterHolderFactory.getHolder('path', 1, ExampleEnum.SECOND_VALUE)
        node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof EnumParameterizationTableTreeNode
        assertEquals 0, node.childCount

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path', 0, true)
        node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof BooleanTableTreeNode
        assertEquals 0, node.childCount

        parameters.clear()
        parameters << ParameterHolderFactory.getHolder('path', 0, new SimpleMultiDimensionalParameter(['']))
        parameters << ParameterHolderFactory.getHolder('path', 1, new SimpleMultiDimensionalParameter(['']))
        node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof MultiDimensionalParameterizationTableTreeNode
        assertEquals 0, node.childCount

    }

    void testGetParameterObjectTableTreeNodes() {
        List parameters = new ArrayList()
        parameters << ParameterHolderFactory.getHolder("path", 0, ExampleParameterObjectClassifier.TYPE0.getParameterObject(["a": 0, "b": 0]))
        parameters << ParameterHolderFactory.getHolder("path", 1, ExampleParameterObjectClassifier.TYPE0.getParameterObject(["a": 0, "b": 0]))

        def node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof ParameterObjectParameterTableTreeNode
        assertEquals 3, node.childCount

        def classifierNode = node.getChildAt(0)
        assertTrue classifierNode instanceof ParameterizationClassifierTableTreeNode
        assertEquals 0, classifierNode.childCount

        def paramNode = node.getChildAt(1)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        paramNode = node.getChildAt(2)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount
    }

    void testGetParameterObjectTableTreeNodesWithDifferentClassifiers() {
        List parameters = new ArrayList()
        parameters << ParameterHolderFactory.getHolder("path", 0, ExampleParameterObjectClassifier.TYPE0.getParameterObject(["a": 0, "b": 0]))
        parameters << ParameterHolderFactory.getHolder("path", 1, ExampleParameterObjectClassifier.TYPE1.getParameterObject(["p1": 0, "p2": 0]))

        def node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof ParameterObjectParameterTableTreeNode
        assertEquals 5, node.childCount

        def classifierNode = node.getChildAt(0)
        assertTrue classifierNode instanceof ParameterizationClassifierTableTreeNode
        assertEquals 0, classifierNode.childCount

        def paramNode = node.getChildAt(1)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        paramNode = node.getChildAt(2)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        paramNode = node.getChildAt(3)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        paramNode = node.getChildAt(4)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount
    }

    void testGetParameterObjectTableTreeNodesWithNullClassifiers() {
        List parameters = new ArrayList()
        parameters << ParameterHolderFactory.getHolder("path", 0, ExampleParameterObjectClassifier.TYPE0.getParameterObject(["a": 0, "b": 0]))
        parameters << ParameterHolderFactory.getHolder("path", 2, ExampleParameterObjectClassifier.TYPE1.getParameterObject(["p1": 0, "p2": 0]))

        def node = ParameterizationNodeFactory.getNode("path", createParameterization(parameters), model)
        assertTrue node instanceof ParameterObjectParameterTableTreeNode
        assertEquals 5, node.childCount

        def classifierNode = node.getChildAt(0)
        assertTrue classifierNode instanceof ParameterizationClassifierTableTreeNode
        assertEquals 0, classifierNode.childCount

        assertTrue classifierNode.isCellEditable(1)
        assertFalse classifierNode.isCellEditable(2)
        assertTrue classifierNode.isCellEditable(3)

        def paramNode = node.getChildAt(1)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        assertFalse paramNode.isCellEditable(2)

        paramNode = node.getChildAt(2)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        assertFalse paramNode.isCellEditable(2)

        paramNode = node.getChildAt(3)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        assertFalse paramNode.isCellEditable(2)

        paramNode = node.getChildAt(4)
        assertTrue paramNode instanceof SimpleValueParameterizationTableTreeNode
        assertEquals 0, paramNode.childCount

        assertFalse paramNode.isCellEditable(2)
    }

    private Parameterization createParameterization(List<ParameterHolder> parameterHolders) {
        Parameterization parameterization = new Parameterization("")
        parameterHolders.each { parameterization.addParameter(it) }

        return parameterization
    }

}