package org.pillarone.riskanalytics.functional

import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.operator.ULCTreeOperator
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.functional.main.ImportExportParametrizationTests

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class P1RATTestFunctions extends P1RATFunctionalTestCase {

    public def importCoreAltenativeParameters() {
        ULCTreeOperator tableTree = getSelectionTree()
        TreePath parametrizationPath = tableTree.findPath(["Core", "Parameterization"] as String[])
        assertNotNull "path not found", parametrizationPath
        popUpContextMenu(parametrizationPath, "Import (force)", tableTree)
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.getPathField()
        pathField.typeText(ImportExportParametrizationTests.getResource("CoreAlternativeParameters.groovy").getFile())
        ULCButtonOperator button = fileChooserOperator.getApproveButton()
        assertNotNull(button)
        button.getFocus()
        button.clickMouse()
    }



    public void exportCoreAltenativeParameters(String parameterizationName, String fileName) {
        ULCTreeOperator tableTree = getSelectionTree()
        TreePath parametrizationPath = tableTree.findPath(["Core", "Parameterization", parameterizationName] as String[])
        assertNotNull "path not found", parametrizationPath
        tableTree.doExpandRow 0
        tableTree.doExpandRow 1
        popUpContextMenu(parametrizationPath, "Export", tableTree)
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.getPathField()
        pathField.typeText(fileName)
        ULCButtonOperator button = fileChooserOperator.getApproveButton()
        assertNotNull(button)
        button.getFocus()
        button.clickMouse()

    }
}
