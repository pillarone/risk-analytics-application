package org.pillarone.riskanalytics.functional;


import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public abstract class RiskAnalyticsAbstractStandaloneTestCase extends AbstractStandaloneTestCase {

    protected void setUp() throws Exception {
        handleConfiguration()
        super.setUp()
    }
}
