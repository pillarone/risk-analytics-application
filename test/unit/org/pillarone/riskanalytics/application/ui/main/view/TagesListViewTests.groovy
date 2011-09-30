package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import com.ulcjava.testframework.operator.ULCCheckBoxOperator
import com.ulcjava.base.application.util.Color

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class TagesListViewTests extends AbstractP1RATTestCase {
    Parameterization p1 = new Parameterization("p1")
    Parameterization p2 = new Parameterization("p2")

    Tag tag1 = new Tag(name: "tag1")
    Tag tag2 = new Tag(name: "tag2")
    Tag tag3 = new Tag(name: "tag3")

    public void testView() {
//        Thread.sleep(1000)
        ULCCheckBoxOperator box1 = getCheckBoxOperator("tag1")
        assertNotNull(box1)
        assertTrue p1.getTags().contains(tag1)
        assertTrue p1.getTags().contains(tag2)
        assertTrue box1.isSelected()

        ULCCheckBoxOperator box2 = getCheckBoxOperator("tag2")
        assertNotNull(box2)
        assertTrue p2.getTags().contains(tag1)
        assertFalse p2.getTags().contains(tag2)
        assertTrue p1.getTags().contains(tag2)
        assertTrue box2.isSelected()
        box2.clickMouse()
        assertFalse p1.getTags().contains(tag2)
        assertFalse p2.getTags().contains(tag2)
        box2.clickMouse()
        assertTrue p1.getTags().contains(tag2)
        assertTrue p2.getTags().contains(tag2)

        ULCCheckBoxOperator box3 = getCheckBoxOperator("tag3")
        assertNotNull(box3)
        assertFalse box3.isSelected()

    }

    @Override
    ULCComponent createContentPane() {


        p1.setTags([tag1, tag2] as Set)
        p2.setTags([tag1] as Set)

        TagesListView tagesListView = new TagesListView([p1, p2])
        tagesListView.metaClass.getAllTags = {-> [tag1, tag2, tag3]}
        tagesListView.metaClass.getAllModellingItemTages = {-> [tag1, tag2]}

        tagesListView.init()
        return tagesListView.content
    }


}
