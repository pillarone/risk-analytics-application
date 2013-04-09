package org.pillarone.riskanalytics.application.ui.customtable.model;

import org.nfunk.jep.ParseException;

/**
*   author simon.parten @ art-allianz . com
 */
public interface IMathParser {

    public org.nfunk.jep.Node parseExpression(String formula);

    public Object getValue() throws ParseException;
}