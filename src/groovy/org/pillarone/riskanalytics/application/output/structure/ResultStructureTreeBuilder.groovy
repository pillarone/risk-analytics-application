package org.pillarone.riskanalytics.application.output.structure

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultStructureTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.application.output.structure.item.ResultNode

class ResultStructureTreeBuilder {

    private static class NodeReplacement implements Comparable {
        String path
        String wildcard
        List<String> replacements

        int compareTo(Object o) {
            NodeReplacement nodeReplacement = o
            int wildcardIndex = path.indexOf(wildcard)
            int otherWildcardIndex = nodeReplacement.path.indexOf(nodeReplacement.wildcard)

            return wildcardIndex > otherWildcardIndex ? -1 : 1
        }

    }

    private Class modelClass
    private Simulation simulation
    private ResultStructure resultStructure
    private Map<String, ICollectingModeStrategy> allPaths

    Map<String, String> transformedPaths = [:]

    private ResultNode existingPathsRoot
    private List<NodeReplacement> nodeReplacements = []


    public ResultStructureTreeBuilder(Map<String, ICollectingModeStrategy> allPaths, Class modelClass, ResultStructure resultStructure, Simulation simulation) {
        this.allPaths = allPaths;
        this.modelClass = modelClass;
        this.resultStructure = resultStructure;
        this.simulation = simulation;

        initTree()
    }

    private void initTree() {
        buildAllPathsTree()
        List leafs = []
        findAllLeafNodes(resultStructure.rootNode, leafs)
        obtainReplacements(leafs)
        transformTree()
    }

    private void buildAllPathsTree() {
        for (String path in allPaths.keySet()) {
            if (existingPathsRoot == null) {
                existingPathsRoot = new ResultNode(path.split(":")[0], null)
            }
            findOrCreateNode(path, existingPathsRoot)
        }
    }

    private void findOrCreateNode(String path, ResultNode resultNode) {
        String[] nodes = path.split(":")
        for (int i = 1; i < nodes.length; i++) {
            String node = nodes[i]
            ResultNode temp = resultNode.getChildByName(node)
            if (temp == null) {
                temp = new ResultNode(node, null)
                resultNode.addChild(temp)
            }
            resultNode = temp
        }
    }

    private void findAllLeafNodes(ResultNode currentNode, List<ResultNode> leafs) {
        if (currentNode.childCount == 0) {
            leafs << currentNode
        } else {
            for (ResultNode child in currentNode.childNodes) {
                findAllLeafNodes child, leafs
            }
        }
    }


    private void obtainReplacements(List<ResultNode> leafs) {
        for (ResultNode leaf in leafs) {
            if (leaf.resultPath.contains("[%")) {
                String[] leafNodes = leaf.resultPath.split(":")
                ResultNode currentNode = existingPathsRoot
                for (int i = 1; i < leafNodes.length; i++) {
                    String currentNodeName = leafNodes[i]
                    if (currentNodeName.startsWith("[%")) {
                        List<String> replacements = []
                        for (ResultNode child in currentNode.childNodes) {
                            if (child.name.startsWith("sub")) {
                                replacements << child.name
                            }
                        }
                        nodeReplacements << new NodeReplacement(path: leaf.path, wildcard: currentNodeName, replacements: replacements)
                        currentNode = currentNode.getChildAt(0) //first dynamic subcomponent (all are the same)
                    } else {
                        currentNode = currentNode.getChildByName(currentNodeName)
                        if (currentNode == null) { // path not collected
                            break
                        }
                    }

                }
            }
        }
    }

    private transformTree() {
        for (NodeReplacement nodeReplacement in nodeReplacements.sort()) {
            ResultNode currentNode = resultStructure.rootNode
            String[] pathElements = nodeReplacement.path.split(":")
            for (int i = 1; i < pathElements.length; i++) {
                String pathElement = pathElements[i]
                currentNode = currentNode.getChildByName(pathElement)
                if (pathElement == nodeReplacement.wildcard) {
                    break
                }
            }
            if (currentNode == null) {
                continue //this wildcard was already handled by another replacement
            }
            ResultNode parent = currentNode.parent
            int insertIndex = parent.removeChild(currentNode)
            for (String newName in nodeReplacement.replacements.sort()) {
                ResultNode newNode = currentNode.clone()
                newNode.name = newName
                parent.addChild(newNode, insertIndex++)
                newNode.replaceWildcard(nodeReplacement.wildcard, newName)
            }
        }
        cleanupTree(resultStructure.rootNode)
    }

    private void cleanupTree(ResultNode node) {
        Iterator<ResultNode> iterator = node.childNodes.iterator()
        while (iterator.hasNext()) {
            ResultNode child = iterator.next()
            List<String> allResultPaths = child.allResultPaths
            if (allResultPaths.any { allPaths.keySet().contains(it) }) {
                cleanupTree(child)
            } else {
                iterator.remove()
            }
        }
    }

    ITableTreeNode buildTree() {
        SimpleTableTreeNode root = new SimpleTableTreeNode(resultStructure.rootNode.name)
        createTree(resultStructure.rootNode, root)
        return root
    }

    private void createTree(ResultNode node, SimpleTableTreeNode parent) {
        for (ResultNode child in node.childNodes) {
            SimpleTableTreeNode newNode
            if (child.resultPath == null || allPaths.get(child.resultPath) == null) {
                newNode = new ResultStructureTableTreeNode(child.name)
            } else {
                newNode = new ResultTableTreeNode(child.name)
                newNode.resultPath = child.resultPath
                newNode.collector = allPaths.get(child.resultPath).getIdentifier()
            }
            parent.add(newNode)
            createTree(child, newNode)
        }
    }
}
