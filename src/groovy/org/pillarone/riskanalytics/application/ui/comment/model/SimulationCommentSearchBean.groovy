package org.pillarone.riskanalytics.application.ui.comment.model

import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SimulationCommentSearchBean extends CommentSearchBean {

    Simulation simulation

    public SimulationCommentSearchBean(Simulation simulation) {
        this.simulation = simulation
        if (this.simulation.comments)
            commentSize = this.simulation.comments.size()
    }

    @Override
    List<Comment> getComments() {
        return this.simulation.comments
    }


}
