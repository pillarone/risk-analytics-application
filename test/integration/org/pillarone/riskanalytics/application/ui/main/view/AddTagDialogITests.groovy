package org.pillarone.riskanalytics.application.ui.main.view

import javax.swing.tree.TreePath
import org.hibernate.Session
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.EnumTagType
import org.pillarone.riskanalytics.functional.P1RATAbstractStandaloneTestCase
import com.ulcjava.testframework.operator.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class AddTagDialogITests extends P1RATAbstractStandaloneTestCase {

    protected String getConfigurationResourceName() {
        return "/org/pillarone/riskanalytics/functional/main/ULCApplicationConfiguration.xml"
    }

    protected void setUp() {
        new DBCleanUpService().cleanUp()
        FileImportService.importModelsIfNeeded(["Core"])
        ModellingItemFactory.clear()
        LocaleResources.setTestMode()
        initTags()
        super.setUp();
    }



    protected void tearDown() {
        super.tearDown();
        new DBCleanUpService().cleanUp()
        removeTags()
        LocaleResources.clearTestMode()
    }

    void testAddNewTag() {
        int size = Tag.findAllByTagType(EnumTagType.PARAMETERIZATION).size()
        ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")

        ULCTableTreeOperator tableTree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("selectionTreeRowHeader"))
        assertNotNull tableTree

        TreePath pathForRename = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
        assertNotNull "path not found", pathForRename

        tableTree.doExpandRow 0
        tableTree.doExpandRow 1

        tableTree.selectCell(3, 0)
        ULCPopupMenuOperator popUpMenu = tableTree.callPopupOnCell(3, 0)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Tags")
//        Thread.sleep 1000

        ULCDialogOperator addTagDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('AddTagDialog'))
        assertNotNull addTagDialog

        ULCListOperator listOperator = new ULCListOperator(addTagDialog, new ComponentByNameChooser('tagesList'))
        assertEquals "items count not correct: ", size, listOperator.getItemCount()

        ULCTextFieldOperator newTagField = new ULCTextFieldOperator(addTagDialog, new ComponentByNameChooser('newTag'))
        assertNotNull newTagField

        newTagField.clearText()
        newTagField.typeText('Test')

        ULCButtonOperator addNewButton = new ULCButtonOperator(addTagDialog, new ComponentByNameChooser('addNew'))
        assertNotNull addNewButton
//
        addNewButton.getFocus()
        addNewButton.clickMouse()

        assertEquals "add new tag failed: ", size + 1, Tag.findAllByTagType(EnumTagType.PARAMETERIZATION).size()

        assertEquals "add new tag failed: ", size + 1, listOperator.getItemCount()

    }

    void testAddTagToParameterization() {
        Tag.withSession {Session session ->

            int size = Tag.findAllByTagType(EnumTagType.PARAMETERIZATION).size()
            ULCFrameOperator frame = new ULCFrameOperator("Risk Analytics")

            ULCTableTreeOperator tableTree = new ULCTableTreeOperator(frame, new ComponentByNameChooser("selectionTreeRowHeader"))
            assertNotNull tableTree

            TreePath pathForRename = tableTree.findPath(["Core", "Parameterization", "CoreAlternativeParameters"] as String[])
            assertNotNull "path not found", pathForRename
            int oldParametersCount = tableTree.getChildCount(pathForRename.lastPathComponent.parent)
            tableTree.doExpandRow 0
            tableTree.doExpandRow 1

            tableTree.selectCell(3, 0)
            ULCPopupMenuOperator popUpMenu = tableTree.callPopupOnCell(2, 0)
            assertNotNull popUpMenu
            popUpMenu.pushMenu("Tags")
//        Thread.sleep 1000

            ULCDialogOperator addTagDialog = new ULCDialogOperator(frame, new ComponentByNameChooser('AddTagDialog'))
            assertNotNull addTagDialog

            ULCListOperator listOperator = new ULCListOperator(addTagDialog, new ComponentByNameChooser('tagesList'))
            listOperator.selectItems([0] as int[])

            ULCButtonOperator applyButton = new ULCButtonOperator(addTagDialog, new ComponentByNameChooser('apply'))
            assertNotNull applyButton
//
            applyButton.getFocus()
            applyButton.clickMouse()
            session.flush()
        }
        Tag.withNewSession {Session session ->
            ParameterizationDAO parameterization = ParameterizationDAO.findByName("CoreAlternativeParameters")
            assertNotNull parameterization
            assertEquals 1, parameterization.tags.size()
        }

    }

    private def initTags() {
        new Tag(name: "NONE", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "WORKFLOW", tagType: EnumTagType.PARAMETERIZATION).save()
        new Tag(name: "PRODUCTION", tagType: EnumTagType.PARAMETERIZATION).save()
    }

    private void removeTags() {
        Tag.findAllByTagType(EnumTagType.PARAMETERIZATION).each {it.delete()}
    }

}
