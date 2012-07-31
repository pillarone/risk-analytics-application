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

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeViewTests extends AbstractP1RATTestCase {

    def mockBatch = new MockFor(BatchRun)
    def mockBatchSimRun = new MockFor(BatchRunSimulationRun)

    @Override
    protected void setUp() {
        super.setUp()    //To change body of overridden methods use File | Settings | File Templates.

        //        BatchRuns and batch simulation runs are not available in unit tests. Mock the DB calls.

        mockBatch.demand.findByName(1) { String giveMeAString -> new BatchRun(name: "test")}
        mockBatch.demand.getExecuted { false }

        mockBatchSimRun.demand.findAllByBatchRun(1) { batchRun, map -> new ArrayList<BatchRunSimulationRun>()}

    }

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


    public void testOpenBatch() {

        mockBatchSimRun.use {
            mockBatch.use {

                ULCPopupMenuOperator popupMenuOperator = getTestBatchPopupMenu()
                ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "Open")
                assertNotNull openBatch
            }
        }


    }

    public void testRunBatch() {
        mockBatchSimRun.use {
            mockBatch.use {

                ULCPopupMenuOperator popupMenuOperator = getTestBatchPopupMenu()

                ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "Run now")
                assertNotNull openBatch
            }
        }

    }

    public void testDeleteBatch() {
        mockBatchSimRun.use {
            mockBatch.use {

                ULCPopupMenuOperator popupMenuOperator = getTestBatchPopupMenu()

                ULCMenuItemOperator openBatch = new ULCMenuItemOperator(popupMenuOperator, "Delete")
                assertNotNull openBatch
            }
        }


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
