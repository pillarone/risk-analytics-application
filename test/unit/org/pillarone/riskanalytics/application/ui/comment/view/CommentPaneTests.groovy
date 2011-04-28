package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.core.parameter.comment.CommentDAO
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentPaneTests extends AbstractP1RATTestCase {

    public void testView() {
//        Thread.sleep(5000)
    }

    @Override
    ULCComponent createContentPane() {
        ULCBoxPane pane = new ULCBoxPane(1, 0)
        pane.setBackground(Color.white);
        for (Comment comment: generateComments()) {
            CommentPane commentPane = new CommentPane(null, comment)
            pane.add(ULCBoxPane.BOX_EXPAND_TOP, commentPane.content)
        }
        return new ULCScrollPane(pane)
    }


    List<Comment> generateComments() {
        CommentDAO dao = new CommentDAO()
        dao.comment = ""
        dao.path = null
        dao.periodIndex = 0
        Comment comment = new Comment(dao)
        comment.metaClass.updateChangeInfo = {->}
        comment.text = "test [http://www.google.de link1] de \n test  [http://www.google.de link2] de \n text  [http://www.google.de link3] de\n[http://www.google.de link4] de"

        CommentDAO dao2 = new CommentDAO()
        dao2.comment = ""
        dao2.path = null
        dao2.periodIndex = 0
        Comment comment2 = new Comment(dao2)
        comment2.metaClass.updateChangeInfo = {->}
        comment2.text = "test:[http://www.test1.de test1] end line \n [http://www.test2.de] test\n[http://www.test3.de test3 link]"

        CommentDAO dao3 = new CommentDAO()
        dao2.comment = ""
        dao2.path = null
        dao2.periodIndex = 0
        Comment comment3 = new Comment(dao3)
        comment3.metaClass.updateChangeInfo = {->}
        comment3.text = "We can link to URL's in comments...\n\n[http://google.com google]\n\nWe can link to URL's in comments...\n[http://google.com google]\n"
        return [comment, comment2, comment3]
    }

}
