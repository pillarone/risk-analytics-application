package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.core.simulation.item.Resource
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.parameter.ResourceParameterHolder
import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNodeFactory
import org.pillarone.riskanalytics.core.components.ResourceModelAdapter
import org.pillarone.riskanalytics.core.components.IResource

class ResourceTreeBuilderClosureDelegate {

    private static class Node {
        String name

        String toString() {
            name
        }
    }

    private Resource item
    private IResource resourceInstance

    ResourceTreeBuilderClosureDelegate(Resource item) {
        this.item = item
        resourceInstance = item.modelClass.newInstance()
    }

    private Map<Node, ResourceTreeBuilderClosureDelegate> hierarchy = new LinkedHashMap()

    Object methodMissing(String name, Object args) {
        Object[] arguments = args as Object[]

        if (arguments.length == 0) {
            handleNode(name, null)
        } else if (arguments.length == 1) {
            def argument = arguments[0]
            if (argument instanceof Closure) {
                handleNode(name, argument)
            } else {
                throw new IllegalArgumentException("Argument must be a Closure (node $name)")
            }
        } else {
            if (arguments.length > 1) {
                throw new IllegalArgumentException("Node $name must not have more than one argument.")

            }
        }
        return null
    }

    void handleNode(String name, Closure subNode) {
        Node newNode = new Node(name: name)
        if (subNode != null) {
            ResourceTreeBuilderClosureDelegate delegate = new ResourceTreeBuilderClosureDelegate(item)
            hierarchy.put(newNode, delegate)
            subNode.delegate = delegate
            subNode.resolveStrategy = Closure.DELEGATE_FIRST
            subNode.call()
        } else {
            hierarchy.put(newNode, null)
        }
    }

    ITableTreeNode getRoot() {
        ITableTreeNode root = new SimpleTableTreeNode(new ResourceParameterHolder.NameVersionPair(item.name, item.versionNumber.toString()).toString())
        for (Map.Entry<Node, ResourceTreeBuilderClosureDelegate> entry in hierarchy.entrySet()) {
            buildTree(root, entry.value, entry.key)
        }

        return root
    }

    private void buildTree(IMutableTableTreeNode currentNode, ResourceTreeBuilderClosureDelegate currentDelegate, Node node) {
        if (currentDelegate != null) {
            def newNode = new SimpleTableTreeNode(node.name)
            currentNode.insert(newNode, currentNode.childCount)
            for (Map.Entry<Node, ResourceTreeBuilderClosureDelegate> entry in currentDelegate.hierarchy.entrySet()) {
                buildTree(newNode, entry.value, entry.key)
            }
        } else {
            currentNode.insert(ParameterizationNodeFactory.getNode(node.name, item, new ResourceModelAdapter(resourceInstance)), currentNode.childCount)
        }
    }
}

