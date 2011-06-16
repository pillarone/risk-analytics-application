package org.pillarone.riskanalytics.application.ui.result.model

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.example.model.ExtendedCoreModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import groovy.mock.interceptor.StubFor
import org.pillarone.riskanalytics.application.util.LocaleResources

class ResultTableTreeModelTests extends GroovyTestCase {

    StubFor stub

    void setUp() {
        LocaleResources.setTestMode()

        stub = new StubFor(PostSimulationCalculation)
        stub.demand.executeQuery(1..2) {query, params -> return [] }
    }

    @Override
    protected void tearDown() {
        LocaleResources.clearTestMode()
    }


    void testModel() {
        Parameterization parameterization = new Parameterization("name")
        parameterization.periodCount = 3
        parameterization.modelClass = ApplicationModel
        parameterization.periodLabels = ["Q1", "Q2", "Q3"]

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child = new SimpleTableTreeNode("child")
        SimpleTableTreeNode grandChild = new SimpleTableTreeNode("grandChild")
        root.add(child)
        child.add(grandChild)

        stub.use {
            ResultTableTreeModel model = new ResultTableTreeModel(root, new SimulationRun(name: "testRun", periodCount: 1), parameterization, new MeanFunction(), new ApplicationModel())
            assertEquals 2, model.getColumnCount()
            assertEquals 2, model.functions.size()
            assertFalse model.isLeaf(root)
            assertTrue model.isLeaf(grandChild)
            assertEquals "Wrong columnName for col 0", "Name", model.getColumnName(0)
            assertEquals "Wrong columnName for col 1", "Mean Q1", model.getColumnName(1)
            assertNull model.getColumnName(2)
            assertEquals("root", model.getValueAt(root, 0))
            assertSame root, model.getRoot()
            assertSame child, model.getChild(root, 0)
            assertEquals 1, model.getChildCount(root)
            assertEquals 0, model.getIndexOfChild(root, child)
            model = new ResultTableTreeModel(root, new SimulationRun(name: "testRun", periodCount: 3), parameterization, new MeanFunction(), new ApplicationModel())
            assertEquals 4, model.columnCount
            assertEquals 4, model.functions.size()
            assertEquals "Wrong columnName for col 0", "Name", model.getColumnName(0)
            assertEquals "Wrong columnName for col 1", "Mean Q1", model.getColumnName(1)
            assertEquals "Wrong columnName for col 2", "Mean Q2", model.getColumnName(2)
            assertEquals "Wrong columnName for col 3", "Mean Q3", model.getColumnName(3)
        }
    }

    void testPeriodCounterLabels() {
        Parameterization parameterization = new Parameterization("name")
        parameterization.modelClass = ExtendedCoreModel
        parameterization.periodCount = 3
        parameterization.periodLabels = ["2009-01-01", "2010-01-01", "2011-01-01"]

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child = new SimpleTableTreeNode("child")
        SimpleTableTreeNode grandChild = new SimpleTableTreeNode("grandChild")
        root.add(child)
        child.add(grandChild)
        stub.use {
            ResultTableTreeModel model = new ResultTableTreeModel(root, new SimulationRun(name: "testRun", periodCount: 3), parameterization, new MeanFunction(), new ExtendedCoreModel())
            assertEquals "Wrong columnName for col 0", "Name", model.getColumnName(0)
            assertEquals "Wrong columnName for col 1", "Mean Jan 01, 2009", model.getColumnName(1)
            assertEquals "Wrong columnName for col 2", "Mean Jan 01, 2010", model.getColumnName(2)
            assertEquals "Wrong columnName for col 3", "Mean Jan 01, 2011", model.getColumnName(3)
        }

    }

    void testSimpleLabels() {
        Parameterization parameterization = new Parameterization("name")
        parameterization.modelClass = ApplicationModel
        parameterization.periodCount = 3
        parameterization.periodLabels = []

        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        SimpleTableTreeNode child = new SimpleTableTreeNode("child")
        SimpleTableTreeNode grandChild = new SimpleTableTreeNode("grandChild")
        root.add(child)
        child.add(grandChild)
        stub.use {
            ResultTableTreeModel model = new ResultTableTreeModel(root, new SimulationRun(name: "testRun", periodCount: 3), parameterization, new MeanFunction(), new ApplicationModel())

            assertEquals "Wrong columnName for col 0", "Name", model.getColumnName(0)
            assertEquals "Wrong columnName for col 1", "Mean P0", model.getColumnName(1)
            assertEquals "Wrong columnName for col 2", "Mean P1", model.getColumnName(2)
            assertEquals "Wrong columnName for col 3", "Mean P2", model.getColumnName(3)
        }
    }

}

