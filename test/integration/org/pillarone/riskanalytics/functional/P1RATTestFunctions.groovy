package org.pillarone.riskanalytics.functional

import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.functional.main.ImportExportParametrizationTests

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class P1RATTestFunctions extends P1RATFunctionalTestCase {

    public def importCoreAltenativeParameters() {
        ULCTableTreeOperator tableTree = getSelectionTreeRowHeader()
        TreePath parametrizationPath = tableTree.findPath(["Core", "Parameterization"] as String[])
        assertNotNull "path not found", parametrizationPath
        popUpContextMenu(1, "Import (force)", tableTree)
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
        ULCTableTreeOperator tableTree = getSelectionTreeRowHeader()
        TreePath parametrizationPath = tableTree.findPath(["Core", "Parameterization", parameterizationName] as String[])
        assertNotNull "path not found", parametrizationPath
        tableTree.doExpandRow 0
        tableTree.doExpandRow 1
        popUpContextMenu(2, "Export", tableTree)
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
