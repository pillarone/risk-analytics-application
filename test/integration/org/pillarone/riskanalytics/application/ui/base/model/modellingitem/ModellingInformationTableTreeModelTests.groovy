package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.server.SimpleContainerServices
import com.ulcjava.base.server.ULCSession
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.workflow.Status

class ModellingInformationTableTreeModelTests extends GroovyTestCase {

    ModellingInformationTableTreeModel model
    ULCSession session = new ULCSession('Test', new SimpleContainerServices())

    void setUp() {
        LocaleResources.setTestMode()
        FileImportService.importModelsIfNeeded(['Application'])
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
        model = new ModellingInformationTableTreeModel(new RiskAnalyticsMainModel())
        model.service.registerSession(session)
    }

    private void newParameterization(String name, String version) {
        new ParameterizationDAO(name: name, itemVersion: version,
                modelClassName: 'models.application.ApplicationModel', periodCount: 1,
                status: Status.NONE, creationDate: new DateTime(), modificationDate: new DateTime()).save(flush: true)

    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
        ParameterizationDAO.list().each {
            it.delete(flush: true)
        }
        model.service.unregisterSession(session)
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

    void testCheckForUpdates() {
        ParameterizationDAO dao = new ParameterizationDAO(name: 'Parametrization X', itemVersion: '12', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE)
        dao.save(flush: true)
        model.updateTreeStructure(session)
        IMutableTableTreeNode modelNode = model.root.getChildAt(0) as IMutableTableTreeNode
        IMutableTableTreeNode paramsNode = modelNode.getChildAt(0) as IMutableTableTreeNode
        assertEquals 2, paramsNode.childCount
        def v12Node = paramsNode.getChildAt(1)
        assertEquals 'Parametrization X', v12Node.name

        assertEquals '12', v12Node.abstractUIItem.item.versionNumber.toString()

        dao.status = Status.IN_REVIEW
        dao.save(flush: true)
        model.updateTreeStructure(session)
        assertEquals(Status.IN_REVIEW.displayName, model.getValueAt(v12Node, 1))
    }
}
