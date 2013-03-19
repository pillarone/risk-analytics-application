package org.pillarone.riskanalytics.application.fileimport

import org.pillarone.riskanalytics.application.output.structure.item.ResultNode
import java.util.Map.Entry


class TreeBuildingClosureDelegate {

    private static class Node {
        String name
        String resultPath

        String toString() {
            "$name -> $resultPath"
        }
    }

    private Map<Node, TreeBuildingClosureDelegate> hierarchy = new LinkedHashMap()

    public static ResultNode createStructureTree(Closure mappings) {
        TreeBuildingClosureDelegate delegate = new TreeBuildingClosureDelegate()
        mappings.delegate = delegate
        mappings.resolveStrategy = Closure.DELEGATE_FIRST
        mappings.call()

        return delegate.root
    }

    Object methodMissing(String name, Object args) {
        Object[] arguments = args as Object[]

        if (arguments.length == 0) {
            throw new IllegalArgumentException("Node $name must have at least one argument (result path or sub closure)")
        }

        if (arguments.length == 1) {
            def argument = arguments[0]
            if (argument instanceof String) {
                handleNode(name, argument, null)
            } else if (argument instanceof Closure) {
                handleNode(name, null, argument)
            } else {
                throw new IllegalArgumentException("Argument must be a String or a Closure (node $name)")
            }
        } else {
            if (arguments.length == 2) {
                assert arguments[0] instanceof String
                assert arguments[1] instanceof Closure

                handleNode(name, arguments[0], arguments[1])
            }
        }

    }

    void handleNode(String name, String resultPath, Closure subNode) {
        Node newNode = new Node(name: name, resultPath: resultPath)
        if (subNode != null) {
            TreeBuildingClosureDelegate delegate = new TreeBuildingClosureDelegate()
            hierarchy.put(newNode, delegate)
            subNode.delegate = delegate
            subNode.resolveStrategy = Closure.DELEGATE_FIRST
            subNode.call()
        } else {
            hierarchy.put(newNode, null)
        }
    }

    ResultNode getRoot() {
        assert hierarchy.size() == 1

        Entry<Node, TreeBuildingClosureDelegate> firstEntry = hierarchy.entrySet().toList()[0]
        Node rootNode = firstEntry.key
        ResultNode root = new ResultNode(rootNode.name, rootNode.resultPath)
        buildTree(root, firstEntry.value)

        return root
    }

    private void buildTree(ResultNode currentNode, TreeBuildingClosureDelegate currentDelegate) {
        if (currentDelegate != null) {
            for (Entry<Node, TreeBuildingClosureDelegate> entry in currentDelegate.hierarchy.entrySet()) {
                Node rootNode = entry.key
                ResultNode node = new ResultNode(rootNode.name, rootNode.resultPath)
                currentNode.addChild(node)

                buildTree(node, entry.value)
            }
        }
    }
}
