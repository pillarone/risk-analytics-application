package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.testframework.operator.ULCCheckBoxOperator
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.pillarone.riskanalytics.application.ui.P1UnitTestMixin
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

@TestMixin(GrailsUnitTestMixin)
@Mixin(P1UnitTestMixin)
class TagsListViewTests extends AbstractSimpleStandaloneTestCase {
    Parameterization p1 = new Parameterization("p1")
    Parameterization p2 = new Parameterization("p2")

    Tag tag1 = new Tag(name: "tag1")
    Tag tag2 = new Tag(name: "tag2")
    Tag tag3 = new Tag(name: "tag3")

    void testView() {
        ULCCheckBoxOperator box1 = getCheckBoxOperator("tag1")
        assertNotNull(box1)
        assertTrue p1.tags.contains(tag1)
        assertTrue p1.tags.contains(tag2)
        assertTrue box1.selected

        ULCCheckBoxOperator box2 = getCheckBoxOperator("tag2")
        assertNotNull(box2)
        assertTrue p2.tags.contains(tag1)
        assertFalse p2.tags.contains(tag2)
        assertTrue p1.tags.contains(tag2)
        assertTrue box2.selected
        box2.clickMouse()
        assertFalse p1.tags.contains(tag2)
        assertFalse p2.tags.contains(tag2)
        box2.clickMouse()
        assertTrue p1.tags.contains(tag2)
        assertTrue p2.tags.contains(tag2)

        ULCCheckBoxOperator box3 = getCheckBoxOperator("tag3")
        assertNotNull(box3)
        assertFalse box3.selected
    }

    @Override
    void start() {
        p1.tags = [tag1, tag2] as Set
        p2.tags = [tag1] as Set
        TagsListView tagsListView = new TagsListView([p1, p2])
        tagsListView.metaClass.getAllTags = { -> [tag1, tag2, tag3] }
        tagsListView.metaClass.getAllModellingItemTages = { -> [tag1, tag2] }
        tagsListView.init()
        inTestFrame(tagsListView.content)
    }
}
