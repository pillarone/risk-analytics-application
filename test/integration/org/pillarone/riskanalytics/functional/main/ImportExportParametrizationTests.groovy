package org.pillarone.riskanalytics.functional.main

import com.ulcjava.testframework.operator.ULCTableTreeOperator
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.functional.P1RATTestFunctions

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportExportParametrizationTests extends P1RATTestFunctions {

    public void testImportParametrization() {
        importCoreAltenativeParameters()
        verifyImport()
    }

    public void testExportParametrization() {
        File testExportFile = File.createTempFile("testParameter", ".groovy")
        exportCoreAltenativeParameters("CoreAlternativeParameters", testExportFile.getAbsolutePath())
        verifyExport(testExportFile)
    }

    private void verifyExport(File exportedFile) {
        assertTrue(exportedFile.exists())
        assertTrue("parametrization not exported", exportedFile.size() > 0)
        exportedFile.delete()
    }

    private void verifyImport() {
        ULCTableTreeOperator tableTree = getSelectionTreeRowHeader()
        TreePath path = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull(path)
    }


}
