package org.pillarone.riskanalytics.functional.main.action.workflow

import com.ulcjava.testframework.operator.ULCTableTreeOperator
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

import javax.swing.tree.TreePath

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DealLinkDialogTests extends AbstractFunctionalTestCase {

    void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(["Core"])
        LocaleResources.testMode = true
        super.setUp();

    }

    void tearDown() {
        LocaleResources.testMode = false
        super.tearDown()
    }


    public void testChooseDeal() {
        ULCTableTreeOperator tableTree = selectionTableTreeRowHeader
        TreePath parametrizationPath = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull "path not found", parametrizationPath

//        mainFrameOperator.close()

        //ART-392: functionality currently disabled
        /*int row = tableTree.getRowForPath(parametrizationPath)
        tableTree.selectCell(row, 0)
        tableTree.pushKey(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK)

        ULCDialogOperator dialog = getDialogOperator("dealDialog")
        assertNotNull dialog

        ULCComboBoxOperator quarter = new ULCComboBoxOperator(dialog, new ComponentByNameChooser("quarter"))
        ULCSpinnerOperator year = new ULCSpinnerOperator(dialog, new ComponentByNameChooser("year"))

        year.scrollToObject(2012, ScrollAdjuster.INCREASE_SCROLL_DIRECTION)
        quarter.selectItem("Q2")

        ULCButtonOperator okButton = new ULCButtonOperator(dialog, new ComponentByNameChooser('okButton'))
        assertNotNull okButton

        okButton.getFocus()
        okButton.clickMouse()

        tableTree.selectCell(row, 0)
        sleep 1000
        tableTree.pushKey(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK)

        dialog = getDialogOperator("dealDialog")
        quarter = new ULCComboBoxOperator(dialog, new ComponentByNameChooser("quarter"))
        year = new ULCSpinnerOperator(dialog, new ComponentByNameChooser("year"))

        assertEquals 2012, year.value
        assertEquals "Q2", quarter.selectedItem*/

    }


}
