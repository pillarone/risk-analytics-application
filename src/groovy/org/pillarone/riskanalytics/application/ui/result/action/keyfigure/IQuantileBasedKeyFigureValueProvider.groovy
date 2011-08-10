package org.pillarone.riskanalytics.application.ui.result.action.keyfigure

import org.pillarone.riskanalytics.core.output.QuantilePerspective


public interface IQuantileBasedKeyFigureValueProvider<E> extends IParametrizedKeyFigureValueProvider<E> {

    QuantilePerspective getQuantilePerspective()

}