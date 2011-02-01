package org.pillarone.riskanalytics.functional.main

import com.ulcjava.testframework.operator.ULCTreeOperator
import javax.swing.tree.TreePath
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import com.ulcjava.base.application.event.KeyEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import models.core.CoreModel
import org.pillarone.riskanalytics.core.ParameterizationDAO

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportExportParametrizationTests extends AbstractFunctionalTestCase {

    public void testImportParametrization() {
        ULCTreeOperator tree = getSelectionTree()
        importFile(tree)

        verifyImport()
    }

    private void importFile(ULCTreeOperator tree) {
        pushKeyOnPath(tree, tree.findPath(["Core", "Parameterization"] as String[]), KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK)
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.getPathField()
        pathField.typeText(ImportExportParametrizationTests.getResource("CoreAlternativeParameters.groovy").getFile())
        ULCButtonOperator button = fileChooserOperator.getApproveButton()
        assertNotNull(button)
        button.getFocus()
        button.clickMouse()
    }

    public void testExportParametrization() {
        File testExportFile = File.createTempFile("testParameter", ".groovy")
        String parameterizationName = "CoreAlternativeParameters"
        String fileName = testExportFile.getAbsolutePath()

        ULCTreeOperator tree = getSelectionTree()
        importFile(tree)
        TreePath parametrizationPath = tree.findPath(["Core", "Parameterization", parameterizationName] as String[])
        assertNotNull "path not found", parametrizationPath

        //tree.doExpandPath opens parameterization...
        tree.doExpandRow(0)
        tree.doExpandRow(1)
        tree.clickOnPath(parametrizationPath)

        tree.pushKey(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK)
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.getPathField()
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

    private void verifyImport() {
        ULCTreeOperator tableTree = getSelectionTree()
        TreePath path = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull(path)
    }

}
