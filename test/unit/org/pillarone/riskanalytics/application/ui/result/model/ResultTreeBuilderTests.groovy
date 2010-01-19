package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.util.LocaleResources

class ResultTreeBuilderTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode()
    }

    void tearDown() {
        LocaleResources.clearTestMode()
    }

    void testNothing() {

    }

    //TODO: fix with new model
    /*void testTreeStructure() {
        StructureTestModel model = new StructureTestModel()
        model.init()

        ResultTreeBuilder builder = new ResultTreeBuilder(model, "src/java/models/test/StructureTestStructure")
        ITableTreeNode root = builder.root
        assertNotNull root
        assertEquals 3, root.childCount

        ITableTreeNode globalNode = builder.root.getChildAt(1)
        assertEquals 1, globalNode.childCount
        assertEquals "GLOBAL", globalNode.name
        ITableTreeNode claimsAggregatorNode = globalNode.getChildAt(0)
        assertEquals "claimsAggregator", claimsAggregatorNode.name
        assertEquals 3, claimsAggregatorNode.childCount
        ITableTreeNode outClaimsNode = claimsAggregatorNode.getChildAt(2)

        assertEquals 1, outClaimsNode.childCount
        assertEquals "outClaimsNet", outClaimsNode.name

        ITableTreeNode ultimateNode = outClaimsNode.getChildAt(0)
        assertEquals 0, ultimateNode.childCount
        assertEquals "ultimate", ultimateNode.name
    }

    void testRemoveNonExistingPathNodes() {
        StructureTestModel model = new StructureTestModel()
        model.init()


        StubFor resultAccessorStub = new StubFor(ResultAccessor)
        resultAccessorStub.demand.getPaths {SimulationRun run -> []}
        resultAccessorStub.use {
            ResultTreeBuilder builder = new ResultTreeBuilder(model, "src/java/models/test/StructureTestStructure")
            builder.applyResultPaths()
            assertNotNull builder.root
            assertEquals 0, builder.root.childCount
        }

        resultAccessorStub = new StubFor(ResultAccessor)
        resultAccessorStub.demand.getPaths {SimulationRun run -> ["StructureTest:GLOBAL:claimsAggregator:outClaimsNet"]}
        resultAccessorStub.use {
            ResultTreeBuilder builder = new ResultTreeBuilder(model, "src/java/models/test/StructureTestStructure")
            builder.applyResultPaths()

            assertEquals 1, builder.root.childCount
            ITableTreeNode globalNode = builder.root.getChildAt(0)
            assertEquals 1, globalNode.childCount
            assertEquals "GLOBAL", globalNode.name
            ITableTreeNode claimsAggregatorNode = globalNode.getChildAt(0)
            assertEquals "claimsAggregator", claimsAggregatorNode.name
            assertEquals 1, claimsAggregatorNode.childCount
            ITableTreeNode outClaimsNode = claimsAggregatorNode.getChildAt(0)
            assertEquals 1, outClaimsNode.childCount
            assertEquals "outClaimsNet", outClaimsNode.name
        }
    }

    void testRemoveNonExistingPathNodesWithSimilarNames() {
        Model model = new ExampleCompanyModel()
        model.init()

        StubFor resultAccessorStub = new StubFor(ResultAccessor)
        resultAccessorStub.demand.getPaths {SimulationRun run -> ["ExampleCompany:mtpl:subRiProgram:claimsGross"]}
        resultAccessorStub.use {
            ResultTreeBuilder builder = new ResultTreeBuilder(model, "src/java/models/exampleCompany/ExampleCompanyStructure")
            builder.applyResultPaths()
            assertNotNull builder.root
            assertEquals 1, builder.root.childCount
            ITableTreeNode mtplNode = builder.root.getChildAt(0)
            assertEquals "subRiProgram", mtplNode.getChildAt(0).name
        }
    }

    void testPathInfo() {
        StructureTestModel model = new StructureTestModel()
        model.init()

        ResultTreeBuilder builder = new ResultTreeBuilder(model, "src/java/models/test/StructureTestStructure")

        ITableTreeNode globalNode = builder.root.getChildAt(1)
        assertEquals "StructureTest:GLOBAL", globalNode.path
        ITableTreeNode claimsAggregatorNode = globalNode.getChildAt(0)
        assertEquals "StructureTest:GLOBAL:claimsAggregator", claimsAggregatorNode.path
        ITableTreeNode outClaimsNode = claimsAggregatorNode.getChildAt(2)
        assertEquals "StructureTest:GLOBAL:claimsAggregator:outClaimsNet", outClaimsNode.path

        ITableTreeNode ultimateNode = outClaimsNode.getChildAt(0)
        assertEquals "ultimate", ultimateNode.name
        assertEquals "StructureTest:GLOBAL:claimsAggregator:outClaimsNet", ultimateNode.path

    }

    void testDynamicPathNodes() {
        MultiLineReinsuranceModel model = new MultiLineReinsuranceModel()
        model.init()


        StubFor resultAccessorStub = new StubFor(ResultAccessor)
        resultAccessorStub.demand.getPaths(2..2) {SimulationRun run ->
            [
                'MultiLineReinsurance:Reinsurance:reinsuranceProgram:subContract0:outSomething',
                'MultiLineReinsurance:Reinsurance:reinsuranceProgram:subContract1:outSomething'
            ]
        }
        resultAccessorStub.use {
            ResultTreeBuilder builder = new ResultTreeBuilder(model, "src/java/models/multiLineReinsurance/MultiLineReinsuranceStructure")
            builder.applyResultPaths()
            assertNotNull builder.root
            def lineNode = builder.root.getChildAt(0)
            def reinsuranceProgrameNode = lineNode.getChildAt(0)

            assertEquals 2, reinsuranceProgrameNode.childCount
            assertEquals 'subContract0', reinsuranceProgrameNode.getChildAt(0).name
            assertEquals 'subContract1', reinsuranceProgrameNode.getChildAt(1).name
        }

    }*/

}