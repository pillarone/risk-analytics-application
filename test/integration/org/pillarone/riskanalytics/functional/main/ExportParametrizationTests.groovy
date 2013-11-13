package org.pillarone.riskanalytics.functional.main

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService

import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ExportParametrizationTests extends AbstractFunctionalTestCase {
    @Override
    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        ModellingItemSearchService.instance.refresh()
        super.setUp()
    }

    @Override
    protected void tearDown() {
        super.tearDown()
    }

    public void testExportParametrization() {
        File testExportFile = File.createTempFile("testParameter", ".groovy")
        testExportFile.delete()
        String parameterizationName = "CoreAlternativeParameters"
        String fileName = testExportFile.getAbsolutePath()

        ULCTableTreeOperator tree = getSelectionTableTreeRowHeader()
        int coreRow = tree.getRowForPath(tree.findPath(['Core'] as String[]))
        int coreParamRow = tree.getRowForPath(tree.findPath(['Core','Parameterization'] as String[]))
        TreePath parametrizationPath = tree.findPath(["Core", "Parameterization", parameterizationName] as String[])
        assertNotNull "path not found", parametrizationPath

        //tree.doExpandPath opens parameterization...
        tree.doExpandRow(coreRow)
        tree.doExpandRow(coreParamRow)
        int row = tree.getRowForPath(parametrizationPath)
        tree.selectCell(row, 0)
        tree.pushKey(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK)
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.getPathField()
        pathField.clearText()
        pathField.typeText(fileName)
        ULCButtonOperator button = fileChooserOperator.getApproveButton()
        assertNotNull(button)
        button.getFocus()
        button.clickMouse()
        verifyExport(testExportFile)
    }

    private void verifyExport(File exportedFile) {
        assertTrue(exportedFile.exists())
        assertTrue("parametrization not exported", exportedFile.size() > 0)
        exportedFile.delete()
    }


}
