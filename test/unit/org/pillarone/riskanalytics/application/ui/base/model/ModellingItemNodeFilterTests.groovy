package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.joda.time.DateTime

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingItemNodeFilterTests extends GroovyTestCase {


    public void testIsAcceptedByTags() {
        ModellingItemNodeFilter tagFilter = new ModellingItemNodeFilter(["WORKFLOW"], ModellingInformationTableTreeModel.TAGS)
        Parameterization parameterization = new Parameterization("test")
        ParameterizationNode node = new ParameterizationNode(parameterization)

        node.values[ModellingInformationTableTreeModel.TAGS] = "WORKFLOW,NONE,PRODUCTION"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "WORKFLOW"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "NONE,WORKFLOW,PRODUCTION"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "NONE,PRODUCTION,WORKFLOW"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "NONE,PRODUCTION,WORKFLOW2"
        assertFalse tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "NO_TAG"
        assertFalse tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = ""
        assertFalse tagFilter.internalAcceptNode(node)

        tagFilter = new ModellingItemNodeFilter(["WORKFLOW", "NONE"], ModellingInformationTableTreeModel.TAGS)

        node.values[ModellingInformationTableTreeModel.TAGS] = "WORKFLOW"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "NONE"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "NONE,PRODUCTION,WORKFLOW"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "NONE,PRODUCTION"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.TAGS] = "PRODUCTION"
        assertFalse tagFilter.internalAcceptNode(node)

    }

    public void testIsAcceptedByDate() {
        ModellingItemNodeFilter tagFilter = new ModellingItemNodeFilter(["01.01.2001"], ModellingInformationTableTreeModel.CREATION_DATE)
        Parameterization parameterization = new Parameterization("test")
        ParameterizationNode node = new ParameterizationNode(parameterization)

        DateTime date = ModellingInformationTableTreeModel.simpleDateFormat.parseDateTime("01.01.2001")
        node.values[ModellingInformationTableTreeModel.CREATION_DATE] = date
        assertTrue tagFilter.internalAcceptNode(node)

        date = ModellingInformationTableTreeModel.simpleDateFormat.parseDateTime("02.01.2001")
        node.values[ModellingInformationTableTreeModel.CREATION_DATE] = date
        assertFalse tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.CREATION_DATE] = null
        assertFalse tagFilter.internalAcceptNode(node)

    }

    public void testIsAcceptedByString() {
        ModellingItemNodeFilter tagFilter = new ModellingItemNodeFilter(["author"], ModellingInformationTableTreeModel.OWNER)
        Parameterization parameterization = new Parameterization("test")
        ParameterizationNode node = new ParameterizationNode(parameterization)

        node.values[ModellingInformationTableTreeModel.OWNER] = "author"
        assertTrue tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.OWNER] = "NO"
        assertFalse tagFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.OWNER] = null
        assertFalse tagFilter.internalAcceptNode(node)

    }

    public void testIsAcceptedByCount() {
        ModellingItemNodeFilter allCommentFilter = new ModellingItemNodeFilter([ModellingItemNodeFilter.ALL], ModellingInformationTableTreeModel.COMMENTS)
        ModellingItemNodeFilter withoutCommentFilter = new ModellingItemNodeFilter([ModellingItemNodeFilter.WITHOUT_COMMENTS], ModellingInformationTableTreeModel.COMMENTS)
        ModellingItemNodeFilter withCommentFilter = new ModellingItemNodeFilter([ModellingItemNodeFilter.WITH_COMMENTS], ModellingInformationTableTreeModel.COMMENTS)
        Parameterization parameterization = new Parameterization("test")
        ParameterizationNode node = new ParameterizationNode(parameterization)

        node.values[ModellingInformationTableTreeModel.COMMENTS] = 1
        assertTrue allCommentFilter.internalAcceptNode(node)
        assertTrue withCommentFilter.internalAcceptNode(node)
        assertFalse withoutCommentFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.COMMENTS] = 0
        assertTrue allCommentFilter.internalAcceptNode(node)
        assertFalse withCommentFilter.internalAcceptNode(node)
        assertTrue withoutCommentFilter.internalAcceptNode(node)

        node.values[ModellingInformationTableTreeModel.COMMENTS] = 2
        assertTrue allCommentFilter.internalAcceptNode(node)
        assertTrue withCommentFilter.internalAcceptNode(node)
        assertFalse withoutCommentFilter.internalAcceptNode(node)

    }
}
