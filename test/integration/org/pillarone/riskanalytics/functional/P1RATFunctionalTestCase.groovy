package org.pillarone.riskanalytics.functional

import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.ModelFileImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class P1RATFunctionalTestCase extends P1RATAbstractStandaloneTestCase {
    ULCFrameOperator mainFrameOperator

    protected void setUp() {
        new ResultConfigurationImportService().compareFilesAndWriteToDB(["Core"])
        new ModelStructureImportService().compareFilesAndWriteToDB(["Core"])
        new ModelFileImportService().compareFilesAndWriteToDB(["Core"])
        super.setUp()
    }

    private void stubLocaleResource() {
        LocaleResources.metaClass.getLocale = {
            return new Locale("en")
        }
    }


    protected String getConfigurationResourceName() {
        return "/org/pillarone/riskanalytics/functional/resources/ULCApplicationConfiguration.xml"
    }

    ULCFrameOperator getMainFrameOperator() {
        if (mainFrameOperator == null) {
            mainFrameOperator = new ULCFrameOperator("Risk Analytics")
        }
        return mainFrameOperator;
    }

    ULCTableTreeOperator getTableTreeOperatorByName(String name) {
        new ULCTableTreeOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCTreeOperator getTreeOperatorByName(String name) {
        new ULCTreeOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCButtonOperator getButtonOperator(String name) {
        new ULCButtonOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComboBoxOperator getComboBoxOperator(String name) {
        new ULCComboBoxOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCComponentOperator getComponentOperatorByName(String name) {
        return new ULCComponentOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCPopupMenuOperator getPopupMenuOperator(String name) {
        return new ULCPopupMenuOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCTextFieldOperator getTextFieldOperator(String name) {
        return new ULCTextFieldOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCDialogOperator getDialogOperator(String name) {
        return new ULCDialogOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    ULCTableTreeOperator getSelectionTreeRowHeader() {
        ULCTableTreeOperator tableTree = getTableTreeOperatorByName("selectionTreeRowHeader")
        assertNotNull tableTree
        return tableTree
    }

    ULCTreeOperator getSelectionTree() {
        ULCTreeOperator tree = getTreeOperatorByName("selectionTree")
        assertNotNull tree
        return tree
    }

    void popUpContextMenu(int row, String itemName, ULCTableTreeOperator tableTree) {
        ULCPopupMenuOperator parametrizationContextMenu = tableTree.callPopupOnCell(row, 0)
        assertNotNull parametrizationContextMenu
        parametrizationContextMenu.pushMenu(itemName)
    }

    void popUpContextMenu(TreePath treePath, String itemName, ULCTreeOperator tree) {
        tree.doExpandRow 0
        tree.doExpandRow 1
        ULCPopupMenuOperator parametrizationContextMenu = tree.callPopupOnPath(treePath)
        assertNotNull parametrizationContextMenu
        parametrizationContextMenu.pushMenu(itemName)
    }
}
