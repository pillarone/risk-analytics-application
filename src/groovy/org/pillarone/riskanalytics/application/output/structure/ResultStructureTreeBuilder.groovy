package org.pillarone.riskanalytics.application.output.structure

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultStructureTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class ResultStructureTreeBuilder {

    private Class modelClass
    private Simulation simulation
    private ResultStructure resultStructure
    private List<String> allPaths

    Map<String, String> transformedPaths = [:]


    public ResultStructureTreeBuilder(List<String> allPaths, Class modelClass, ResultStructure resultStructure, Simulation simulation) {
        this.allPaths = allPaths;
        this.modelClass = modelClass;
        this.resultStructure = resultStructure;
        this.simulation = simulation;

        transformPaths()
    }

    private void transformPaths() {
        for (Map.Entry entry in resultStructure.mappings.entrySet()) {
            String fromPath = entry.key
            String toPath = entry.value

            //non dynamic path
            if (!fromPath.contains("[%")) {
                if (allPaths.contains(fromPath)) {
                    transformedPaths.put(fromPath, toPath)
                }
            } else {
                String pattern = fromPath.substring(0, fromPath.indexOf("["))
                pattern += "(.*)"
                pattern += fromPath.substring(fromPath.indexOf("]") + 1)

                Collection matchingPaths = allPaths.findAll { it ==~ pattern}
                for (String path in matchingPaths) {
                    def matcher = path =~ pattern
                    String replacement = matcher[0][1]
                    transformedPaths.put(path, toPath.replaceAll("\\[.*\\]", replacement))
                }
            }
        }
    }

    ITableTreeNode buildTree() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        for (Map.Entry paths in transformedPaths.entrySet().sort { it.key }) {
            findOrCreatePath(paths.value, paths.key, root)
        }
        //return the model node
        SimpleTableTreeNode firstNode = root.getChildAt(0)
        firstNode?.parent = null
        return firstNode ? firstNode : root
    }

    private void findOrCreatePath(String path, String resultPath, SimpleTableTreeNode root) {
        String[] nodes = path.split(":")
        nodes.eachWithIndex {String node, int index ->
            SimpleTableTreeNode temp = root.getChildByName(node)
            if (temp == null) {
                if (index < nodes.length - 1) {
                    temp = new ResultStructureTableTreeNode(node)
                } else {
                    temp = new ResultTableTreeNode(node)
                    temp.resultPath = resultPath
                }
                root.add(temp)
            }
            root = temp
        }
    }
}
