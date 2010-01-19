package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.NodeNameFunction
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode

class ResultFunctionTests extends GroovyTestCase {

    void testGeneralResultFunction() {
        IFunction function = new TestResultFunction()
        SimpleTableTreeNode node = new SimpleTableTreeNode("node")
        SimpleTableTreeNode child = new SimpleTableTreeNode("child")
        ResultTableTreeNode grandChild = new ResultTableTreeNode("grandchild")
        child.add(grandChild)
        node.add(child)

        assertEquals("node:child", function.getPath(grandChild))
        assertEquals("child", function.getAttributeName(grandChild))
    }



    void testNodeNameFunction() {
        IFunction function = new NodeNameFunction()
        SimpleTableTreeNode node = new SimpleTableTreeNode("test")
        SimpleTableTreeNode child = new SimpleTableTreeNode("foo")
        ResultTableTreeNode grandChild = new ResultTableTreeNode("bar")
        child.add(grandChild)
        node.add(child)
        assertEquals grandChild.displayName, function.evaluate(null, 0, grandChild)
    }

}

class TestResultFunction extends ResultFunction {

    public String getName() {
        return "test";
    }

    public String getKeyFigureName() {
        return "test";
    }

}