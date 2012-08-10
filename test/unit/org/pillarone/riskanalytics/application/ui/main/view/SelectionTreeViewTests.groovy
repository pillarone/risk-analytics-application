package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCMenuItemOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase

import javax.swing.tree.TreePath
import groovy.mock.interceptor.MockFor
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import groovy.mock.interceptor.StubFor

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeViewTests extends AbstractP1RATTestCase {

    ULCComponent createContentPane() {
        RiskAnalyticsMainModel mainModel = getMockRiskAnalyticsMainModel()
        SelectionTreeView view = new SelectionTreeView(mainModel)
        return view.content;
    }

    public void testView() {
//        Thread.sleep 15000
    }



    public void testOpenItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openItem = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openItem
    }

    public void testExportItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator exportItem = new ULCMenuItemOperator(popupMenuOperator, "Export")
        assertNotNull exportItem
        exportItem.clickMouse()
    }

    public void testRenameItem() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator renameItem = new ULCMenuItemOperator(popupMenuOperator, "Rename ...")
        assertNotNull renameItem
    }

    public void testRun() {

        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

    }

    public void testSaveAsAction() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator saveAs = new ULCMenuItemOperator(popupMenuOperator, "Save as ...")
        assertNotNull saveAs
        saveAs.clickMouse()
    }

    public void testCreateNewVersion() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 1

        componentTree.selectCell(2, 0)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(2, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator newVersion = new ULCMenuItemOperator(popupMenuOperator, "Create new version")
        assertNotNull newVersion
    }

    private ULCPopupMenuOperator getTestBatchPopupMenu() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        TreePath batchPath = componentTree.findPath(["Batches"] as String[])
        componentTree.doExpandPath(batchPath)
        TreePath testBatchPath = componentTree.findPath(["Batches", "test"] as String[])
        componentTree.selectPath(testBatchPath)

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(componentTree.getRowForPath(testBatchPath), 0)

        assertNotNull popupMenuOperator
        return popupMenuOperator

    }

    public void testNewBatch() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        TreePath batchPath = componentTree.findPath(["Batches"] as String[])

        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(componentTree.getRowForPath(batchPath), 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "New")
        assertNotNull openBatch
    }


    public void testOpenResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 3
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(4, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator openResult = new ULCMenuItemOperator(popupMenuOperator, "Open")
        assertNotNull openResult
    }

    public void testExportResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 3
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(4, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator exportItem = new ULCMenuItemOperator(popupMenuOperator, "Export")
        assertNotNull exportItem

    }

    public void testRenameResult() {
        ULCTableTreeOperator componentTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        componentTree.doExpandRow 0
        componentTree.doExpandRow 3
        ULCPopupMenuOperator popupMenuOperator = componentTree.callPopupOnCell(4, 0)

        assertNotNull popupMenuOperator

        ULCMenuItemOperator renameItem = new ULCMenuItemOperator(popupMenuOperator, "Rename ...")
        assertNotNull renameItem
        renameItem.clickMouse()

    }


}
