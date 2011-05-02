package org.pillarone.riskanalytics.functional.main

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportParametrizationTests extends AbstractFunctionalTestCase {

    public void testImportParametrization() {
        ULCTableTreeOperator tree = getSelectionTableTreeRowHeader()

        pushKeyOnPath(tree, tree.findPath(["Core", "Parameterization"] as String[]), KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK)
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.getPathField()
        pathField.typeText(ImportParametrizationTests.getResource("CoreAlternativeParameters.groovy").getFile())
        ULCButtonOperator button = fileChooserOperator.getApproveButton()
        assertNotNull(button)
        button.getFocus()
        button.clickMouse()

        verifyImport()
    }


    private void verifyImport() {
        ULCTableTreeOperator tableTree = getSelectionTableTreeRowHeader()
        TreePath path = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull(path)
    }

}
