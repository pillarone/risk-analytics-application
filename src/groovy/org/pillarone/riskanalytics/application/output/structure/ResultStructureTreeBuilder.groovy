package org.pillarone.riskanalytics.application.output.structure

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultStructureTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.model.Model

/**
 * This class process the ResultTree file replacing variables with p14n specific values. The user may define any
 * variables in a sub component context. They have to be wrapped with [%varname%]. For other contexts there exists two
 * predefined variables: one for periods [%period%] and another for [%split%].
 * Period is especially useful for triangle like data. See also AggregateSplitByInceptionDateCollectingModeStrategy in the
 * pc-cashflow plugin.
 */
class ResultStructureTreeBuilder {

    private static class NodeReplacement implements Comparable {
        /** full path */
        String path
        /** variable name */
        String wildcard
        /** component names replacing the variable */
        Set<String> replacements

        int compareTo(Object o) {
            NodeReplacement nodeReplacement = o
            int wildcardIndex = path.indexOf(wildcard)
            int otherWildcardIndex = nodeReplacement.path.indexOf(nodeReplacement.wildcard)

            return wildcardIndex == otherWildcardIndex ? 0 : wildcardIndex > otherWildcardIndex ? -1 : 1
        }
    }

    private Model model
    private Simulation simulation
    private ResultStructure resultStructure
    private Map<String, ICollectingModeStrategy> allPaths
    private static String PERIOD_VARIABLE = '[%period%]'
    private static String SPLIT_VARIABLE = '[%split%]'

    Map<String, String> transformedPaths = [:]

    private ResultNode existingPathsRoot
    private List<NodeReplacement> nodeReplacements = []


    public ResultStructureTreeBuilder(Map<String, ICollectingModeStrategy> allPaths, Model model,
                                      ResultStructure resultStructure, Simulation simulation) {
        this.allPaths = allPaths;
        this.model = model;
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
                obtainReplacementsRecursive(existingPathsRoot, leaf.resultPath.split(":"), 1, leaf.path)
            }
        }
    }

    private obtainReplacementsRecursive(ResultNode currentNode, String[] leafNodes, int level, String leafPath) {
        if (level == leafNodes.length) {
            return
        }

        String currentNodeName = leafNodes[level]
        if (currentNodeName.startsWith("[%")) {
            // collect all possible replacements
            List<String> replacements = []
            if (currentNodeName.equals(PERIOD_VARIABLE)) {
                for (ResultNode child in currentNode.childNodes) {
                    // The child name contains the calendar year, reading it from the period labels is not sufficient
                    // as they don't contain reserve years.
                    replacements << child.name
                }
            }
            else if (currentNodeName.equals(SPLIT_VARIABLE)) {
                for (ResultNode child in currentNode.childNodes) {
                    replacements << child.name
                }
            }
            else {
                for (ResultNode child in currentNode.childNodes) {
                    if (child.name.startsWith("sub")) {
                        replacements << child.name
                    }
                }
            }
            NodeReplacement existingReplacement = nodeReplacements.find { it.path == leafPath && it.wildcard == currentNodeName}
            if (existingReplacement == null) {
                // add leafPath and currentNodeName to nodeReplacements only if it is not yet part of the nodeReplacements list 
                nodeReplacements << new NodeReplacement(path: leafPath, wildcard: currentNodeName, replacements: replacements)
            } else {
                // complete the existingReplacement list if there is already an existingReplacement
                existingReplacement.replacements.addAll(replacements)
            }
            // step one level down of on a variable node
            level++
            for (ResultNode child in currentNode.childNodes) {
                obtainReplacementsRecursive(child, leafNodes, level, leafPath)
            }
        } 
        else {
            if (currentNode == null) return
            currentNode = currentNode.getChildByName(currentNodeName)
            if (currentNode != null) { // path collected
                // step one level down of on a fix node 
                obtainReplacementsRecursive(currentNode, leafNodes, ++level, leafPath)
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
                newNode = new ResultStructureTableTreeNode(child.name, model.class)
            } else {
                newNode = new ResultTableTreeNode(child.name, model.class)
                newNode.resultPath = child.resultPath
                newNode.collector = allPaths.get(child.resultPath).getIdentifier()
            }
            parent.add(newNode)
            createTree(child, newNode)
        }
    }

}
