package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.IParametrizedItemListener
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

class ParameterizationTableTreeModel extends AbstractParametrizedTableTreeModel {

    ParameterizationTreeBuilder builder
    Model simulationModel
    int columnCount

    public ParameterizationTableTreeModel() {
        super(null) //TODO
    }

    public ParameterizationTableTreeModel(ParameterizationTreeBuilder builder) {
        super(builder.root)
        this.builder = builder
        this.columnCount = builder.periodCount + 1
    }

    @Override
    protected List<String> getAllEditablePaths() {
        return builder.item.allEditablePaths
    }

    public int getColumnCount() {
        return columnCount
    }

    protected Object getI18NValue(ParameterizationClassifierTableTreeNode node, Object value) {
        return node.getKeyForValue(value)
    }

    void componentAdded(String path, Component component) {
        String[] pathComponents = path.substring(0, path.lastIndexOf(":")).split(":")
        ComponentTableTreeNode parent = findComponentNode(pathComponents)
        parent.component.addSubComponent(component)
        ComponentTableTreeNode node = builder.buildComponentNode(component.name, component)
        toBeInserted.add(node)
        try {
            builder.createNewComponentNode(parent, node)
            node.comments = builder.item.comments?.findAll {it.path == node.path}
            nodesWereInserted(getPath(parent), parent.getIndex(node))
        } finally {
            toBeInserted.remove(node)
        }
        notifyNodeValueChanged(node, -1)
        notifyComboBoxNodesComponentAdded(component)
        changedComments()
    }


    private void notifyComboBoxNodesComponentAdded(Component newComponent) {
        List nodes = []
        findNodes(root, nodes)
        for (ConstrainedStringParameterizationTableTreeNode node in nodes) {
            node.addComponent(newComponent)
            node.comments = builder?.item?.comments?.findAll {it.path == node.path}
            nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        }
    }

    private void notifyComboBoxNodesComponentRemoved(Component newComponent) {
        List nodes = []
        findNodes(root, nodes)
        for (ConstrainedStringParameterizationTableTreeNode node in nodes) {
            node.removeComponent(newComponent)
            nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        }
    }

    private void findNodes(ITableTreeNode current, List nodes) {
        if (current instanceof ConstrainedStringParameterizationTableTreeNode) {
            nodes << current
        }
        for (int i = 0; i < current.childCount; i++) {
            findNodes(current.getChildAt(i), nodes)
        }
    }

    void componentRemoved(String path) {
        ComponentTableTreeNode componentNode = findComponentNode(path.split(":"))
        ComponentTableTreeNode parent = componentNode.parent
        int childIndex = componentNode.parent.getIndex(componentNode)
        parent.component.removeSubComponent(componentNode.component)
        parent.remove(componentNode)
        nodesWereRemoved(getPath(parent), [childIndex] as int[], [componentNode] as Object[])
        notifyNodeValueChanged(componentNode, -1)
        notifyComboBoxNodesComponentRemoved(componentNode.component)
    }


    public String getColumnName(int column) {
        if (column == 0) {
            return "Name"
        }

        if (builder != null) {
            return builder.item.getPeriodLabel(column - 1)
        }
        return null
    }

}

interface TableTreeValueChangedListener {
    void valueChanged(Object node, int column)
}