package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.ulcjava.base.application.event.ITableTreeModelListener
import com.ulcjava.base.application.event.TableTreeModelEvent
import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.server.SimpleContainerServices
import com.ulcjava.base.server.ULCSession
import models.application.ApplicationModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.example.model.EmptyModel
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameter.ParameterizationTag
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.functional.RiskAnalyticsAbstractStandaloneTestCase

class ModellingInformationTableTreeModelTests extends RiskAnalyticsAbstractStandaloneTestCase {

    ModellingInformationTableTreeModel model
    RiskAnalyticsMainModel mainModel
    ULCSession session = new ULCSession('Test', new SimpleContainerServices())
    private TestModelListener modelListener

    void setUp() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(['Application'])
        ModelRegistry.instance.clear()
        ModelRegistry.instance.addModel(ApplicationModel)
        newParameterization('Parametrization X', '1')
        newParameterization('Parametrization X', '1.2')
        newParameterization('Parametrization X', '1.3')
        newParameterization('Parametrization X', '1.4')
        newParameterization('Parametrization X', '1.4.1')
        newParameterization('Parametrization X', '2')
        newParameterization('Parametrization X', '3')
        newParameterization('Parametrization X', '4')
        newParameterization('Parametrization X', '5')
        newParameterization('Parametrization X', '6')
        newParameterization('Parametrization X', '7')
        newParameterization('Parametrization X', '8')
        newParameterization('Parametrization X', '9')
        newParameterization('Parametrization X', '10')
        newParameterization('Parametrization X', '11')
        mainModel = new RiskAnalyticsMainModel()
        model = new ModellingInformationTableTreeModel(mainModel)
        model.buildTreeNodes()
        model.service.registerSession(session)
        modelListener = new TestModelListener()
        model.addTableTreeModelListener(modelListener)
    }

    private void newParameterization(String name, String version) {
        new ParameterizationDAO(name: name, itemVersion: version,
                modelClassName: 'models.application.ApplicationModel', periodCount: 1,
                status: Status.NONE, creationDate: new DateTime(), modificationDate: new DateTime()).save(flush: true)

    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
        model.service.unregisterSession(session)
        SimulationRun.list()*.delete(flush: true)
        ParameterizationDAO.list()*.delete(flush: true)
    }

    private void printTree() {
        printNode(model, model.root, 0)
    }

    void testColumnCount() {
        assert 8 == model.columnCount
    }

    void testTreeStructure() {
        IMutableTableTreeNode root = model.root
        assert root
        assert 3 == root.childCount
    }

    private printNode(ModellingInformationTableTreeModel model, ITableTreeNode node, int level) {
        String line = ""
        level.times { line += "\t" }
        model.columnCount.times {
            line += "${model.getValueAt(node, it)},"
        }
        println line
        level++
        node.childCount.times {
            printNode(model, node.getChildAt(it), level)
        }
    }

    void testSimpleParamStructureWithTenNodes() {
        IMutableTableTreeNode modelNode = model.root.getChildAt(0) as IMutableTableTreeNode
        IMutableTableTreeNode paramsNode = modelNode.getChildAt(0) as IMutableTableTreeNode
        assertEquals 2, paramsNode.childCount
        def v11Node = paramsNode.getChildAt(1)
        assertEquals 'Parametrization X', v11Node.name

        assertEquals '11', v11Node.abstractUIItem.item.versionNumber.toString()
        assertEquals 10, v11Node.childCount

        def v1Node = v11Node.getChildAt(9)
        assertEquals '1', v1Node.abstractUIItem.item.versionNumber.toString()
        assertEquals 3, v1Node.childCount

        def v14Node = v1Node.getChildAt(0)
        assertEquals '1.4', v14Node.abstractUIItem.item.versionNumber.toString()
        assertEquals 1, v14Node.childCount

        def v141Node = v14Node.getChildAt(0)
        assertEquals '1.4.1', v141Node.abstractUIItem.item.versionNumber.toString()
    }

    void testUpdateTreeStructure() {
        ParameterizationDAO parameterizationDAO = new ParameterizationDAO(name: 'Parametrization X', itemVersion: '12', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE)
        parameterizationDAO.save(flush: true)
        model.updateTreeStructure(session)
        IMutableTableTreeNode modelNode = model.root.getChildAt(0) as IMutableTableTreeNode
        IMutableTableTreeNode paramsNode = modelNode.getChildAt(0) as IMutableTableTreeNode
        IMutableTableTreeNode resultsNode = modelNode.getChildAt(2) as IMutableTableTreeNode
        assertEquals 2, paramsNode.childCount
        def v12Node = paramsNode.getChildAt(1)
        assertEquals 'Parametrization X', v12Node.name

        assertEquals '12', v12Node.abstractUIItem.item.versionNumber.toString()

        parameterizationDAO.status = Status.IN_REVIEW
        parameterizationDAO.save(flush: true)
        model.updateTreeStructure(session)
        assertEquals(Status.IN_REVIEW.displayName, model.getValueAt(v12Node, 1))
        parameterizationDAO.delete(flush: true)
        model.updateTreeStructure(session)
        assertEquals(2, paramsNode.childCount)
        def v11Node = paramsNode.getChildAt(1)
        assertEquals '11', v11Node.abstractUIItem.item.versionNumber.toString()
        SimulationRun run = new SimulationRun()
        run.parameterization = ParameterizationDAO.list()[0]
        run.resultConfiguration = ResultConfigurationDAO.list()[0]
        run.name = 'TestRun'
        run.startTime = new DateTime()
        run.endTime = new DateTime()
        run.model = 'models.application.ApplicationModel'
        run.save(flush: true)
        model.updateTreeStructure(session)
        assertEquals(1, resultsNode.childCount)

    }

    void testGetValueAt() {
        ITableTreeNode applicationNode = model.root.getChildAt(0)
        IMutableTableTreeNode paramsNode = applicationNode.getChildAt(0) as IMutableTableTreeNode
        assertEquals('Application', model.getValueAt(applicationNode, 0))
        assertEquals('Parameterization', model.getValueAt(paramsNode, 0))
        assertEquals('ApplicationParameters v1', model.getValueAt(paramsNode.getChildAt(0), 0))
    }

    void testBatch() {
        BatchRun run = new BatchRun()
        run.name = 'testBatch'
        BatchUIItem batchUIItem = new BatchUIItem(mainModel, run)
        model.addNodeForItem(batchUIItem)
        IMutableTableTreeNode batchNode = model.root.getChildAt(2) as IMutableTableTreeNode
        assertEquals(1, batchNode.childCount)
        assertEquals('testBatch', model.getValueAt(batchNode.getChildAt(0), 0))
        model.removeNodeForItem(new BatchUIItem(mainModel, run))
        assertEquals(0, batchNode.childCount)
    }

    void testModel() {
        model.addNodeForItem(new EmptyModel())
        IMutableTableTreeNode modelNode = model.root.getChildAt(1) as IMutableTableTreeNode
        assertEquals('Empty', model.getValueAt(modelNode, 0))
    }

    void testRefresh() {
        IMutableTableTreeNode oldModelNode = model.root.getChildAt(0) as IMutableTableTreeNode
        IMutableTableTreeNode oldParamsNode = oldModelNode.getChildAt(0) as IMutableTableTreeNode
        model.refresh()
        IMutableTableTreeNode newModelNode = model.root.getChildAt(0) as IMutableTableTreeNode
        IMutableTableTreeNode newParamsNode = newModelNode.getChildAt(0) as IMutableTableTreeNode

        assertNotSame(oldModelNode, newModelNode)
        assertNotSame(oldParamsNode, newParamsNode)
    }

    void testReturnedInstance() {
        UserContext.metaClass.static.hasCurrentUser = {->
            true
        }
        ModellingInformationTableTreeModel model = ModellingInformationTableTreeModel.getInstance(mainModel)
        assertTrue(model instanceof ModellingInformationTableTreeModel)
        assertFalse(model instanceof StandaloneTableTreeModel)
        UserContext.metaClass.static.hasCurrentUser = {->
            false
        }
        assertEquals(8, model.columnCount)
        model = ModellingInformationTableTreeModel.getInstance(mainModel)
        assertTrue(model instanceof StandaloneTableTreeModel)
        assertEquals(4, model.columnCount)
    }

    void testUpdateP14nNodes() {
        SimulationRun run = new SimulationRun()
        ParameterizationDAO parameterziation = ParameterizationDAO.list()[0]
        run.parameterization = parameterziation
        run.resultConfiguration = ResultConfigurationDAO.list()[0]
        run.name = 'TestRun1'
        run.startTime = new DateTime()
        run.endTime = new DateTime()
        run.model = 'models.application.ApplicationModel'
        run.save(flush: true)


        model.updateTreeStructure(session)
        // expect one nodeStructure changed on simulation node
        assert 1 == modelListener.nodeStructureChangedEvents.size()
        modelListener.reset()

        run = new SimulationRun()
        run.parameterization = parameterziation
        run.resultConfiguration = ResultConfigurationDAO.list()[0]
        run.name = 'TestRun2'
        run.startTime = new DateTime()
        run.endTime = new DateTime()
        run.model = 'models.application.ApplicationModel'
        run.save(flush: true)

        model.updateTreeStructure(session)
        assert 1 == modelListener.nodeInsertedEvents.size()
        modelListener.reset()

        //assert that tree contains the simulation nodes and the child nodes.
        IMutableTableTreeNode modelNode = model.root.getChildAt(0) as IMutableTableTreeNode
        ParameterizationNode paramsNode = modelNode.getChildAt(0).getChildAt(0) as ParameterizationNode
        IMutableTableTreeNode resultsNode = modelNode.getChildAt(2) as IMutableTableTreeNode
        assert 2 == resultsNode.childCount
        ParameterizationNode simulationParamsNode1 = resultsNode.getChildAt(0).getChildAt(0) as ParameterizationNode
        ParameterizationNode simulationParamsNode2 = resultsNode.getChildAt(1).getChildAt(0) as ParameterizationNode
        paramsNode.values.each { k, v ->
            assert v == simulationParamsNode1.values[k]
            assert v == simulationParamsNode2.values[k]

        }

        parameterziation.addToTags(new ParameterizationTag(parameterizationDAO: parameterziation, tag: Tag.list()[0]))
        parameterziation.save(flush: true)
        model.updateTreeStructure(session)
        assert 3 == modelListener.nodeChangedEvents.size()

        paramsNode = modelNode.getChildAt(0).getChildAt(0) as ParameterizationNode
        simulationParamsNode1 = resultsNode.getChildAt(0).getChildAt(0) as ParameterizationNode
        simulationParamsNode2 = resultsNode.getChildAt(1).getChildAt(0) as ParameterizationNode
        paramsNode.values.each { k, v ->
            assert v == simulationParamsNode1.values[k]
            assert v == simulationParamsNode2.values[k]
        }


    }

    class TestModelListener implements ITableTreeModelListener {
        List<TableTreeModelEvent> nodeChangedEvents = []
        List<TableTreeModelEvent> structureChangedEvents = []
        List<TableTreeModelEvent> nodeStructureChangedEvents = []
        List<TableTreeModelEvent> nodeInsertedEvents = []
        List<TableTreeModelEvent> nodeRemovedEvents = []

        void reset() {
            nodeChangedEvents.clear()
            structureChangedEvents.clear()
            nodeStructureChangedEvents.clear()
            nodeInsertedEvents.clear()
            nodeRemovedEvents.clear()
        }

        @Override
        void tableTreeStructureChanged(TableTreeModelEvent event) {
            structureChangedEvents << event
        }

        @Override
        void tableTreeNodeStructureChanged(TableTreeModelEvent event) {
            nodeStructureChangedEvents << event
        }

        @Override
        void tableTreeNodesInserted(TableTreeModelEvent event) {
            nodeInsertedEvents << event
        }

        @Override
        void tableTreeNodesRemoved(TableTreeModelEvent event) {
            nodeRemovedEvents << event
        }

        @Override
        void tableTreeNodesChanged(TableTreeModelEvent event) {
            nodeChangedEvents << event
        }
    }
}
