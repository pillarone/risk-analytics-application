package org.pillarone.riskanalytics.application.reports.gira.action

import org.pillarone.riskanalytics.application.reports.gira.model.PathFilter
import org.pillarone.riskanalytics.application.reports.gira.model.IPathFilter

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

        IPathFilter claimsFilter = PathFilter.getFilter(parser.getComponentPath(PathType.CLAIMSGENERATORS), ResultPathParser.CLAIMS_SUFFIX_LIST)
        IPathFilter reinsuranceFilter = PathFilter.getFilter(parser.getComponentPath(PathType.REINSURANCE), ResultPathParser.REINSURANCE_TABLE_SUFFIX_LIST)

        List<List<String>> generators = parser.getComponentPaths(PathType.CLAIMSGENERATORS, claimsFilter)
        assertEquals 4, generators.size()
        assertEquals 4, parser.getPathsByPathType(generators, PathType.CLAIMSGENERATORS).size()

        List<List<String>> reinsurances = parser.getComponentPaths(PathType.REINSURANCE, reinsuranceFilter)
        assertEquals 3, reinsurances.size()
        assertEquals 3, parser.getPathsByPathType(reinsurances, PathType.REINSURANCE).size()

        assertTrue parser.isParentPath("GIRA:claimsGenerators:outClaims")
        assertFalse parser.isParentPath("GIRA:claimsGenerators:subMarine:outClaims")
        assertTrue parser.isParentPath("GIRA:reinsuranceContracts:outClaimsNet")
        assertFalse parser.isParentPath("GIRA:reinsuranceContracts:subWxl:outClaimsCeded")

    }
}
