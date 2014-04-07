package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.testframework.operator.*
import grails.util.Holders
import org.hibernate.Session
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.parameter.ParameterizationTag
import org.pillarone.riskanalytics.core.parameter.comment.Tag
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

    void setUp() {
        FileImportService.importModelsIfNeeded(["Core"])
        Holders.grailsApplication.mainContext.cacheItemSearchService.refresh()
        ModellingItemFactory.clear()
        LocaleResources.testMode = true
        removeTags()
        initTags()
        super.setUp()
    }

    void tearDown() {
        super.tearDown();
        removeTags()
        LocaleResources.testMode = false
        BatchUIItem
    }

    void testAddNewTag() {
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

        ULCDialogOperator addTagDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('AddTagDialog'))
        assertNotNull addTagDialog


        ULCTextFieldOperator newTagField = new ULCTextFieldOperator(addTagDialog, new ComponentByNameChooser('newTag'))
        assertNotNull newTagField

        newTagField.clearText()
        newTagField.typeText('Test')

        ULCButtonOperator addNewButton = new ULCButtonOperator(addTagDialog, new ComponentByNameChooser('addNew'))
        assertNotNull addNewButton

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

            ULCDialogOperator addTagDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('AddTagDialog'))
            assertNotNull addTagDialog

            ULCCheckBoxOperator checkBoxOperator = new ULCCheckBoxOperator(addTagDialog, new ComponentByNameChooser("WORKFLOW"))
            assertNotNull checkBoxOperator

            checkBoxOperator.clickMouse()
            assertTrue checkBoxOperator.selected

            ULCButtonOperator applyButton = new ULCButtonOperator(addTagDialog, new ComponentByNameChooser('apply'))
            assertNotNull applyButton

            applyButton.getFocus()
            applyButton.clickMouse()
            session.flush()
        }
        Tag.withNewSession { Session session ->
            ParameterizationDAO parameterization = ParameterizationDAO.findByName("CoreAlternativeParameters")
            assertNotNull parameterization
            assertEquals 1, parameterization.tags.size()
        }

    }

    private def initTags() {
        new Tag(name: "NONE", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "WORKFLOW", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "PRODUCTION", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "TODO", tagType: EnumTagType.COMMENT).save()
    }

    private void removeTags() {
        Tag.withNewSession { def session ->
            ParameterizationTag.list().each { it.delete(flush: true) }
            def tags = Tag.list().findAll { it.name != Tag.LOCKED_TAG }
            tags.each { it.delete(flush: true) }
        }
    }
}
