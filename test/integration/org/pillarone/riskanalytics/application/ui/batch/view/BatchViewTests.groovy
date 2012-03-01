package org.pillarone.riskanalytics.application.ui.batch.view

import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import com.ulcjava.testframework.operator.*
import com.ulcjava.base.application.event.KeyEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchViewTests extends AbstractFunctionalTestCase {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        LocaleResources.setTestMode()
        super.setUp();

    }

    @Override protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown()
    }

    public void testAddNewBatch() {
        ULCTableTreeOperator tableTree = getSelectionTableTreeRowHeader()
        TreePath batchPath = tableTree.findPath(["Batches"] as String[])
        assertNotNull "path not found", batchPath

        tableTree.selectPath(batchPath)

        ULCPopupMenuOperator popupMenuOperator = tableTree.callPopupOnCell(tableTree.getRowForPath(batchPath), 0)

        assertNotNull popupMenuOperator
        ULCMenuItemOperator newBatch = new ULCMenuItemOperator(popupMenuOperator, "New")
        assertNotNull newBatch
        newBatch.clickMouse()

        ULCTextFieldOperator textFieldOperator = getTextFieldOperator("batchNameTextField")
        textFieldOperator.clearText()
        textFieldOperator.typeText("test")
        assertNotNull textFieldOperator
        ULCButtonOperator addButton = getButtonOperator("addButton")
        assertNotNull addButton
        Thread.sleep(2000)
        addButton.getFocus()
        addButton.clickMouse()
        TreePath newbatch = tableTree.findPath(["Batches", "test"] as String[])
        assertNotNull "path not found", newbatch

    }


}
