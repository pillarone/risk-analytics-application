package org.pillarone.riskanalytics.application.output.structure

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultStructureTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy

class ResultStructureTreeBuilder {

    private Class modelClass
    private Simulation simulation
    private ResultStructure resultStructure
    private Map<String, ICollectingModeStrategy> allPaths

    Map<String, String> transformedPaths = [:]


    public ResultStructureTreeBuilder(Map<String, ICollectingModeStrategy> allPaths, Class modelClass, ResultStructure resultStructure, Simulation simulation) {
        this.allPaths = allPaths;
        this.modelClass = modelClass;
        this.resultStructure = resultStructure;
        this.simulation = simulation;

        transformPaths()
    }

    private void transformPaths() {
        for (Map.Entry entry in resultStructure.mappings.entrySet()) {
            String fromPath = entry.value
            String toPath = entry.key

            //non dynamic path
            if (!fromPath.contains("[%")) {
                if (allPaths.keySet().contains(fromPath)) {
                    transformedPaths.put(toPath, fromPath)
                }
            } else {
                String toPattern = fromPath.replaceAll("\\[%.*?%\\]", "(.*)")

                Collection matchingPaths = allPaths.keySet().findAll { it ==~ toPattern && it.count(":") == toPattern.count(":")}
                for (String path in matchingPaths) {
                    def toMatcher = path =~ toPattern
                    def fromMatcher = fromPath =~ toPattern
                    List toResults = toMatcher[0]
                    List fromResults = fromMatcher[0]
                    String updatedToPath = toPath
                    for (int j = 1; j < toResults.size(); j++) {
                        String replacement = fromResults[j].replace("[", "\\[")
                        updatedToPath = updatedToPath.replaceFirst(replacement, toResults[j])
                    }
                    transformedPaths.put(updatedToPath, path)
                }
            }
        }
    }

    ITableTreeNode buildTree() {
        SimpleTableTreeNode root = new SimpleTableTreeNode("root")
        for (Map.Entry paths in transformedPaths.entrySet().sort { it.key }) {
            findOrCreatePath(paths.key, paths.value, root)
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
                    temp.collector = allPaths.get(resultPath).getIdentifier()
                }
                root.add(temp)
            }
            root = temp
        }
    }
}
