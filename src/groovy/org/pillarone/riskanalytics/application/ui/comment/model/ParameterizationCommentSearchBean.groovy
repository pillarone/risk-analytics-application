package org.pillarone.riskanalytics.application.ui.comment.model

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationCommentSearchBean extends CommentSearchBean {

    Parameterization parameterization

    public ParameterizationCommentSearchBean(Parameterization parameterization) {
        this.parameterization = parameterization
        if (this.parameterization.comments)
            commentSize = this.parameterization.comments.size()
    }

    @Override
    List<Comment> getComments() {
        return parameterization.comments
    }


}
