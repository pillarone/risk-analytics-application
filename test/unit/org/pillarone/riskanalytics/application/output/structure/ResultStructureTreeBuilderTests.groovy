package org.pillarone.riskanalytics.application.output.structure

import org.pillarone.riskanalytics.application.fileimport.TreeBuildingClosureDelegate
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy

class ResultStructureTreeBuilderTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp();

        CollectingModeFactory.registerStrategy(new SingleValueCollectingModeStrategy())
        CollectingModeFactory.registerStrategy(new AggregatedCollectingModeStrategy())
    }

    void testOneWildcard() {
        //multiple result path per level (test wildcard replacing - should only happen once per level)
        //mapping larger than allPaths (tests if unnecessary nodes are removed)
        //non wildcard-paths
        //out on DCC level (incl order)
        //collector injection
        Closure mapping = {
            model {
                net {
                    totalFigureX "Model:linesOfBusiness:outTotalNet:figureX"
                    "[%lob%]" {
                        premium "Model:linesOfBusiness:[%lob%]:outUnderwritingNet:premium"
                        claims "Model:linesOfBusiness:[%lob%]:outClaimsNet:ultimate"
                    }
                    totalFigureY "Model:linesOfBusiness:outTotalNet:figureY"
                }
                gross {
                    "[%lob%]" {
                        premium "Model:linesOfBusiness:[%lob%]:outUnderwritingGross:premium"
                        claims "Model:linesOfBusiness:[%lob%]:outClaimsGross:ultimate"
                    }
                }
                ceded {
                    "[%lob%]" {
                        premium "Model:linesOfBusiness:[%lob%]:outUnderwritingCeded:premium"
                        claims "Model:linesOfBusiness:[%lob%]:outClaimsCeded:ultimate"
                    }
                }
            }
        }
        ICollectingModeStrategy aggregated = CollectingModeFactory.getStrategy(AggregatedCollectingModeStrategy.IDENTIFIER)
        ICollectingModeStrategy single = CollectingModeFactory.getStrategy(SingleValueCollectingModeStrategy.IDENTIFIER)

        Map allPaths = [
                "Model:linesOfBusiness:subFire:outClaimsNet:ultimate": aggregated,
                "Model:linesOfBusiness:subFire:outClaimsGross:ultimate": aggregated,
                "Model:linesOfBusiness:subFire:outUnderwritingNet:premium": aggregated,
                "Model:linesOfBusiness:subFire:outUnderwritingGross:premium": aggregated,

                "Model:linesOfBusiness:outTotalNet:figureX": single,
                "Model:linesOfBusiness:outTotalNet:figureY": single,

                "Model:linesOfBusiness:subFlood:outClaimsNet:ultimate": aggregated,
                "Model:linesOfBusiness:subFlood:outClaimsGross:ultimate": aggregated,
                "Model:linesOfBusiness:subFlood:outUnderwritingNet:premium": aggregated,
                "Model:linesOfBusiness:subFlood:outUnderwritingGross:premium": aggregated,
        ]

        ResultStructure resultStructure = new ResultStructure("name")
        resultStructure.rootNode = TreeBuildingClosureDelegate.createStructureTree(mapping)

        ResultStructureTreeBuilder treeBuilder = new ResultStructureTreeBuilder(allPaths, null, resultStructure, null)

        SimpleTableTreeNode root = treeBuilder.buildTree()
        assertEquals "model", root.getName()

        assertEquals 2, root.childCount //ceded not collected

        SimpleTableTreeNode net = root.getChildAt(0)
        assertEquals "net", net.name
        assertEquals 4, net.childCount //figureX + 2 dynamic subcomponents + figureY

        assertEquals "totalFigureX", net.getChildAt(0).name
        assertEquals "Model:linesOfBusiness:outTotalNet", net.getChildAt(0).path
        assertEquals "figureX", net.getChildAt(0).field
        assertEquals single.identifier, net.getChildAt(0).collector

        SimpleTableTreeNode dynamicNode = net.getChildAt(1)
        assertEquals 2, dynamicNode.childCount //claims + premium

        assertEquals "premium", dynamicNode.getChildAt(0).name
        assertEquals "Model:linesOfBusiness:subFire:outUnderwritingNet", dynamicNode.getChildAt(0).path
        assertEquals "premium", dynamicNode.getChildAt(0).field
        assertEquals aggregated.identifier, dynamicNode.getChildAt(0).collector

        assertEquals "claims", dynamicNode.getChildAt(1).name
        assertEquals "Model:linesOfBusiness:subFire:outClaimsNet", dynamicNode.getChildAt(1).path
        assertEquals "ultimate", dynamicNode.getChildAt(1).field
        assertEquals aggregated.identifier, dynamicNode.getChildAt(1).collector

        dynamicNode = net.getChildAt(2)
        assertEquals 2, dynamicNode.childCount //claims + premium

        assertEquals "premium", dynamicNode.getChildAt(0).name
        assertEquals "Model:linesOfBusiness:subFlood:outUnderwritingNet", dynamicNode.getChildAt(0).path
        assertEquals "premium", dynamicNode.getChildAt(0).field
        assertEquals aggregated.identifier, dynamicNode.getChildAt(0).collector

        assertEquals "claims", dynamicNode.getChildAt(1).name
        assertEquals "Model:linesOfBusiness:subFlood:outClaimsNet", dynamicNode.getChildAt(1).path
        assertEquals "ultimate", dynamicNode.getChildAt(1).field
        assertEquals aggregated.identifier, dynamicNode.getChildAt(1).collector

        assertEquals "totalFigureY", net.getChildAt(3).name
        assertEquals "Model:linesOfBusiness:outTotalNet", net.getChildAt(3).path
        assertEquals "figureY", net.getChildAt(3).field
        assertEquals single.identifier, net.getChildAt(3).collector
    }

    void testTwoWildcards() {

        //tests nested wilcards
        //results on non-leaf level (collected & not collected)
        Closure mapping = {
            model {
                "[%reinsurance%]" "Model:contracts:[%reinsurance%]:outTotal:ultimate", {
                    claims "Model:contracts:[%reinsurance%]:claimsGenerators:outTotal:ultimate", {
                        "[%claims%]" "Model:contracts:[%reinsurance%]:claimsGenerators:[%claims%]:outClaims:ultimate"
                    }
                }
            }
        }
        ICollectingModeStrategy aggregated = CollectingModeFactory.getStrategy(AggregatedCollectingModeStrategy.IDENTIFIER)

        Map allPaths = [
                "Model:contracts:subContractA:outTotal:ultimate": aggregated,

                "Model:contracts:subContractA:claimsGenerators:subFire:outClaims:ultimate": aggregated,
                "Model:contracts:subContractA:claimsGenerators:subFlood:outClaims:ultimate": aggregated,
                "Model:contracts:subContractB:claimsGenerators:subFire:outClaims:ultimate": aggregated,
                "Model:contracts:subContractB:claimsGenerators:subFlood:outClaims:ultimate": aggregated,
        ]

        ResultStructure resultStructure = new ResultStructure("name")
        resultStructure.rootNode = TreeBuildingClosureDelegate.createStructureTree(mapping)

        ResultStructureTreeBuilder treeBuilder = new ResultStructureTreeBuilder(allPaths, null, resultStructure, null)

        SimpleTableTreeNode root = treeBuilder.buildTree()
        assertEquals "model", root.getName()

        assertEquals 2, root.childCount //contract a + b

        SimpleTableTreeNode contractA = root.getChildAt(0)
        assertEquals "Model:contracts:subContractA:outTotal", contractA.path
        assertEquals "ultimate", contractA.field
        assertEquals "subContractA", contractA.name
        assertEquals "subContractB", root.getChildAt(1).name

        SimpleTableTreeNode claimsGen = contractA.getChildAt(0)
        assertFalse "Model:contracts:subContractA:claimsGenerators:outTotal:ultimate" == claimsGen.path
        assertEquals 2, claimsGen.childCount //fire + flood

        assertEquals "subFire", claimsGen.getChildAt(0).name
        assertEquals "Model:contracts:subContractA:claimsGenerators:subFire:outClaims", claimsGen.getChildAt(0).path
        assertEquals "ultimate", claimsGen.getChildAt(0).field
    }
}
