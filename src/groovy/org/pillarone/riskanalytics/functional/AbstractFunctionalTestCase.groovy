package org.pillarone.riskanalytics.functional

import com.ulcjava.base.server.ApplicationConfiguration
import com.ulcjava.testframework.operator.*
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.core.fileimport.ModelFileImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.search.CacheItemSearchService

import javax.swing.tree.TreePath

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AbstractFunctionalTestCase extends RiskAnalyticsAbstractStandaloneTestCase {

    ULCFrameOperator mainFrameOperator
    CacheItemSearchService cacheItemSearchService

    void setUp() {
        cacheItemSearchService = Holders.grailsApplication.mainContext.cacheItemSearchService
        ApplicationConfiguration.reset()
        new ResultConfigurationImportService().compareFilesAndWriteToDB(["Core"])
        new ModelStructureImportService().compareFilesAndWriteToDB(["Core"])
        new ModelFileImportService().compareFilesAndWriteToDB(["Core"])
        ModelRegistry.instance.loadFromDatabase()
        cacheItemSearchService.refresh()
        super.setUp()
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
