package org.pillarone.riskanalytics.functional.main

import com.ulcjava.testframework.operator.ULCTreeOperator
import javax.swing.tree.TreePath
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportExportParametrizationTests extends AbstractFunctionalTestCase {

    public void testImportParametrization() {
        ULCTreeOperator tree = getSelectionTree()
        showPopupOnParameterizationGroupNode(tree, "Core", "Import (force)")
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.getPathField()
        pathField.typeText(ImportExportParametrizationTests.getResource("CoreAlternativeParameters.groovy").getFile())
        ULCButtonOperator button = fileChooserOperator.getApproveButton()
        assertNotNull(button)
        button.getFocus()
        button.clickMouse()

        verifyImport()
    }

    public void testExportParametrization() {
        File testExportFile = File.createTempFile("testParameter", ".groovy")
        String parameterizationName = "CoreAlternativeParameters"
        String fileName = testExportFile.getAbsolutePath()

        ULCTreeOperator tree = getSelectionTree()
        TreePath parametrizationPath = tree.findPath(["Core", "Parameterization", parameterizationName] as String[])
        assertNotNull "path not found", parametrizationPath
        showPopupOnParameterizationNode(tree, "Core", parameterizationName, "Export")
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
