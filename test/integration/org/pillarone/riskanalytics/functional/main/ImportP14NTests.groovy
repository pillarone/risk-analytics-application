package org.pillarone.riskanalytics.functional.main

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

import javax.swing.tree.TreePath

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ImportP14NTests extends AbstractFunctionalTestCase {

    public void testImportParametrization() {
        ULCTableTreeOperator tree = selectionTableTreeRowHeader
        pushKeyOnPath(tree, tree.findPath(["Core", "Parameterization"] as String[]), KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK)
        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)
        ULCTextFieldOperator pathField = fileChooserOperator.pathField
        final String file = ImportP14NTests.getResource("CoreAlternativeParameters.groovy").file
        pathField.typeText(file)
        ULCButtonOperator button = fileChooserOperator.approveButton
        assertNotNull(button)
        button.getFocus()
        button.clickMouse()
        TreePath path = tree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull(path)
    }
}
