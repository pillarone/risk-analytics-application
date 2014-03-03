package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.testframework.operator.*
import org.hibernate.Session
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory

import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.search.CacheItemSearchService
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import org.pillarone.riskanalytics.functional.RiskAnalyticsAbstractStandaloneTestCase

import javax.swing.tree.TreePath

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddTagDialogTests extends RiskAnalyticsAbstractStandaloneTestCase {

    @Override
    protected Class getApplicationClass() {
        P1RATApplication
    }

    protected void setUp() {
        FileImportService.importModelsIfNeeded(["Core"])
        CacheItemSearchService.getInstance().refresh()
        ModellingItemFactory.clear()
        LocaleResources.setTestMode()
        removeTags()
        initTags()
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown();
        removeTags()
        LocaleResources.clearTestMode()
        BatchUIItem
    }

    void testAddNewTag() {
        int size = Tag.findAllByTagType(EnumTagType.PARAMETERIZATION).size()
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")

        ULCTableTreeOperator tableTree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("selectionTreeRowHeader"))
        assertNotNull tableTree

        TreePath pathForRename = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull "path not found", pathForRename

        printTree(tableTree.ULCTableTree)
        tableTree.doExpandPath(pathForRename.parentPath)
        int row = tableTree.getRowForPath(pathForRename)
        tableTree.selectCell(row, 0)

        ULCPopupMenuOperator popUpMenu = tableTree.callPopupOnCell(row, 0)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Tags")
//        Thread.sleep 1000

        ULCDialogOperator addTagDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('AddTagDialog'))
        assertNotNull addTagDialog


        ULCTextFieldOperator newTagField = new ULCTextFieldOperator(addTagDialog, new ComponentByNameChooser('newTag'))
        assertNotNull newTagField

        newTagField.clearText()
        newTagField.typeText('Test')

        ULCButtonOperator addNewButton = new ULCButtonOperator(addTagDialog, new ComponentByNameChooser('addNew'))
        assertNotNull addNewButton
//
        addNewButton.getFocus()
        addNewButton.clickMouse()

        ULCCheckBoxOperator newCheckBox = new ULCCheckBoxOperator(addTagDialog, new ComponentByNameChooser("Test"))
        assertNotNull newCheckBox

        assertEquals "add new tag failed: ", size + 1, Tag.findAllByTagType(EnumTagType.PARAMETERIZATION).size()

    }

    void testAddTagToParameterization() {
        Tag.withSession { Session session ->

            int size = Tag.findAllByTagType(EnumTagType.PARAMETERIZATION).size()
            ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")

            ULCTableTreeOperator tableTree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("selectionTreeRowHeader"))
            assertNotNull tableTree

            TreePath pathForRename = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
            assertNotNull "path not found", pathForRename
            tableTree.doExpandPath(pathForRename.parentPath)

            int row = tableTree.getRowForPath(pathForRename)

            tableTree.selectCell(row, 0)
            ULCPopupMenuOperator popUpMenu = tableTree.callPopupOnCell(row, 0)
            assertNotNull popUpMenu
            popUpMenu.pushMenu("Tags")
//        Thread.sleep 1000

            ULCDialogOperator addTagDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('AddTagDialog'))
            assertNotNull addTagDialog

            ULCCheckBoxOperator checkBoxOperator = new ULCCheckBoxOperator(addTagDialog, new ComponentByNameChooser("WORKFLOW"))
            assertNotNull checkBoxOperator

            checkBoxOperator.clickMouse()
            assertTrue checkBoxOperator.selected

            ULCButtonOperator applyButton = new ULCButtonOperator(addTagDialog, new ComponentByNameChooser('apply'))
            assertNotNull applyButton
//
            applyButton.getFocus()
            applyButton.clickMouse()
            session.flush()
        }
        Tag.withNewSession { Session session ->
            ParameterizationDAO parameterization = ParameterizationDAO.findByName("CoreAlternativeParameters")
            assertNotNull parameterization
            //todo save changed data in database doesn't work correctly in cruise
            //            assertEquals 1, parameterization.tags.size()
        }

    }

    private def initTags() {
        new Tag(name: "NONE", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "WORKFLOW", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "PRODUCTION", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "TODO", tagType: EnumTagType.COMMENT).save()
    }

    private void removeTags() {
        Tag.findAll().each { it.delete(flush: true) }
    }

}
