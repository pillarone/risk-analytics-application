package org.pillarone.riskanalytics.application.output.structure.item


class ResultNode implements Cloneable {

    String name
    String resultPath
    LinkedList<ResultNode> childNodes = []
    ResultNode parent

    public ResultNode(String name, String resultPath) {
        this.name = name
        this.resultPath = resultPath
    }

    void addChild(ResultNode node) {
        node.parent = this
        childNodes << node
    }

    void addChild(ResultNode node, int index) {
        node.parent = this
        childNodes.add(index, node)
    }

    ResultNode getChildAt(int index) {
        return childNodes[index]
    }

    ResultNode getChildByName(String name) {
        return childNodes.find { it.name == name}
    }

    int getChildCount() {
        childNodes.size()
    }

    String getPath() {
        if (parent == null) {
            return name
        } else {
            return parent.getPath() + ":" + name
        }
    }

    String toString() {
        return "${getPath()} -> $resultPath"
    }

    int removeChild(ResultNode node) {
        int index = 0
        Iterator iterator = childNodes.iterator()
        while (iterator.hasNext()) {
            ResultNode current = iterator.next()
            if (current == node) {
                iterator.remove()
                break
            }
            index++
        }
        node.parent = null
        return index
    }

    void replaceWildcard(String wildcard, String actualName) {
        resultPath = resultPath?.replace(wildcard, actualName)

        for (ResultNode child in childNodes) {
            child.replaceWildcard(wildcard, actualName)
        }
    }

    List<String> getAllResultPaths() {
        List<String> paths = []
        if (resultPath != null) {
            paths << resultPath
        }
        for (ResultNode child in childNodes) {
            paths.addAll(child.allResultPaths)
        }
        return paths
    }

    Object clone() {
        ResultNode clone = (ResultNode) super.clone();
        clone.childNodes = []
        clone.parent = null

        for (ResultNode child in childNodes) {
            clone.addChild(child.clone())
        }
        return clone
    }


}
