package org.pillarone.riskanalytics.functional

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.server.ApplicationConfiguration
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.fileimport.ModelFileImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import com.ulcjava.testframework.operator.*
import org.springframework.transaction.support.TransactionSynchronizationManager

import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AbstractFunctionalTestCase extends RiskAnalyticsAbstractStandaloneTestCase {

    ULCFrameOperator mainFrameOperator

    protected void setUp() {
        ApplicationConfiguration.reset()
        new ResultConfigurationImportService().compareFilesAndWriteToDB(["Core"])
        new ModelStructureImportService().compareFilesAndWriteToDB(["Core"])
        new ModelFileImportService().compareFilesAndWriteToDB(["Core"])
        ModelRegistry.instance.loadFromDatabase()
        ModellingItemSearchService.getInstance().refresh()
        super.setUp()
    }

    private void stubLocaleResource() {
        LocaleResources.metaClass.getLocale = {
            return new Locale("en")
        }
    }

    @Override
    protected Class getApplicationClass() {
        P1RATApplication
    }

    ULCFrameOperator getMainFrameOperator() {
        if (mainFrameOperator == null) {
            mainFrameOperator = new ULCFrameOperator("Risk Analytics")
        }
        return mainFrameOperator
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

    ULCTableTreeOperator getSelectionTableTreeRowHeader() {
        return getTableTreeOperatorByName("selectionTreeRowHeader")
    }

    ULCTableOperator getTableOperator(String name) {
        return new ULCTableOperator(getMainFrameOperator(), new ComponentByNameChooser(name))
    }

    protected pushKeyOnPath(ULCTableTreeOperator tree, TreePath path, int key, int mask) {

        int row = tree.getRowForPath(path)
        tree.selectCell(row, 0)
        tree.pushKey(key, mask)
    }
}
