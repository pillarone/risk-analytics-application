package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.core.output.QuantilePerspective

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public enum QuantileFunctionType {

    PROFIT("PROFIT"), LOSS("LOSS")

    private String displayName

    private QuantileFunctionType(String displayName) {
        this.@displayName = displayName
    }

    public String toString() {
        return displayName
    }

    public QuantilePerspective getQuantilePerspective() {
        switch (this) {
            case PROFIT: return QuantilePerspective.PROFIT;
            case LOSS: return QuantilePerspective.LOSS;
            default: throw new IllegalArgumentException("function has to be profit or loss");
        }
    }

}