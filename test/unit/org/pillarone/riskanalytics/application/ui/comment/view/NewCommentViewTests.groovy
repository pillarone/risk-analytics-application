package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCFileChooserOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.comment.model.ItemListModel
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.CommentFile

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewCommentViewTests extends AbstractP1RATTestCase {

    public void testView() {
        ULCButtonOperator removeFile = getButtonOperator("removeFileButton")
        assertNotNull removeFile

        removeFile.getFocus()
        removeFile.clickMouse()

        removeFile = getButtonOperator("removeFileButton")
        assertNotNull removeFile

        removeFile.getFocus()
        removeFile.clickMouse()

        boolean notExist = false
        try {
            removeFile = getButtonOperator("removeFileButton")
        } catch (Exception ex) {
            notExist = true
        }
        assertTrue notExist

        ULCButtonOperator addFile = getButtonOperator("addFileButton")
        assertNotNull addFile

        addFile.getFocus()
        addFile.clickMouse()



        ULCFileChooserOperator fileChooserOperator = ULCFileChooserOperator.findULCFileChooser()
        assertNotNull(fileChooserOperator)

        ULCButtonOperator button = fileChooserOperator.getCancelButton()

        button.getFocus()
        button.clickMouse()


    }

    ULCComponent createContentPane() {
        NewCommentView newCommentView = new NewCommentView()
        def allTags = [new Tag(name: "test")]

        newCommentView.tagListModel = new ItemListModel<Tag>(allTags?.collect {it.name}.toArray(), ["test"])
        newCommentView.path = "Podra:test"
        newCommentView.periodIndex = 0
        newCommentView.metaClass.getDisplayPath = {-> return "path"}
        newCommentView.init()
        newCommentView.fileAdded(new CommentFile("file1", new File("file1")))
        newCommentView.fileAdded(new CommentFile("file2", new File("file2")))
        newCommentView.content
    }
}
