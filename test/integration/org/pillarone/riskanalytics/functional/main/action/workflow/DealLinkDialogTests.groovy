package org.pillarone.riskanalytics.functional.main.action.workflow

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCDialogOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase
import com.ulcjava.base.application.event.KeyEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DealLinkDialogTests extends AbstractFunctionalTestCase {

    protected void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        super.setUp();
    }



    public void testChooseDeal() {
        ULCTableTreeOperator tableTree = getSelectionTableTreeRowHeader()
        TreePath parametrizationPath = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull "path not found", parametrizationPath

        int row = tableTree.getRowForPath(parametrizationPath)
        tableTree.selectCell(row, 0)
        tableTree.pushKey(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK)


        ULCDialogOperator dialog = getDialogOperator("dealDialog")
        assertNotNull dialog

        ULCButtonOperator okButton = new ULCButtonOperator(dialog, new ComponentByNameChooser('okButton'))
        assertNotNull okButton

        okButton.getFocus()
        okButton.clickMouse()

    }


}
