package org.pillarone.riskanalytics.application.ui.base.model.modellingitem

import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.server.SimpleContainerServices
import com.ulcjava.base.server.ULCSession
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
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '1', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '1.2', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '1.3', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '1.4', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '1.4.1', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '2', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '3', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '4', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '5', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '6', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '7', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '8', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '9', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '10', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        new ParameterizationDAO(name: 'Paramertization X', itemVersion: '11', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE).save(flush: true)
        model = new ModellingInformationTableTreeModel()
        model.service.registerSession(session)
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
        printTree()
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
        assertEquals 'Paramertization X', v11Node.name

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
        ParameterizationDAO dao = new ParameterizationDAO(name: 'Paramertization X', itemVersion: '12', modelClassName: 'models.application.ApplicationModel', periodCount: 1, status: Status.NONE)
        dao.save(flush: true)
        model.updateTreeStructure(session)
        IMutableTableTreeNode modelNode = model.root.getChildAt(0) as IMutableTableTreeNode
        IMutableTableTreeNode paramsNode = modelNode.getChildAt(0) as IMutableTableTreeNode
        assertEquals 2, paramsNode.childCount
        def v12Node = paramsNode.getChildAt(1)
        assertEquals 'Paramertization X', v12Node.name

        assertEquals '12', v12Node.abstractUIItem.item.versionNumber.toString()

        dao.status = Status.IN_REVIEW
        dao.save(flush: true)
        model.updateTreeStructure(session)

        printTree()
    }
}
