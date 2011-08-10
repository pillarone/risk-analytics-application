package org.pillarone.riskanalytics.application.ui.comment.model

import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public interface CommentFilter {

    boolean accept(Comment comment)

    boolean accept(ParameterValidation error)

}