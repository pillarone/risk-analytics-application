package org.pillarone.riskanalytics.application.reports.gira.action

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultPathParserTests extends GroovyTestCase {

    List paths = ['GIRA:segments:subMarine:outClaimsCeded',
            'GIRA:segments:subMarine:outUnderwritingInfoCeded',
            'GIRA:segments:subMarine:outUnderwritingInfoNet',
            'GIRA:segments:subMarine:outClaimsNet',
            'GIRA:claimsGenerators:subMarine:outClaimNumber',
            'GIRA:claimsGenerators:subMarine:outClaims',
            'GIRA:claimsGenerators:subMotorSingle:outClaimNumber',
            'GIRA:claimsGenerators:subMotorSingle:outClaims',
            'GIRA:claimsGenerators:outClaims',
            'GIRA:reinsuranceContracts:outClaimsNet',
            'GIRA:reinsuranceContracts:outClaimsCeded',
            'GIRA:claimsGenerators:subMarine:outSeverityIndexApplied',
            'GIRA:claimsGenerators:subMotorSingle:outSeverityIndexApplied',
            'GIRA:reinsuranceContracts:subQuote:outClaimsCeded',
            'GIRA:reinsuranceContracts:subQuote:outClaimsGross',
            'GIRA:reinsuranceContracts:subQuote:outClaimsNet',
            'GIRA:claimsGenerators:subMotorBase:outClaimNumber',
            'GIRA:claimsGenerators:subMotorBase:outClaims',
            'GIRA:claimsGenerators:subMotorBase:outSeverityIndexApplied',
            'GIRA:reinsuranceContracts:subWxl:outClaimsCeded',
            'GIRA:reinsuranceContracts:subWxl:outClaimsGross',
            'GIRA:reinsuranceContracts:subWxl:outClaimsNet']


    public void testGetPaths() {

        ResultPathParser parser = new ResultPathParser("GIRA", paths)

        assertEquals 10, parser.getComponentPaths(PathType.CLAIMSGENERATORS).size()
    }
}
