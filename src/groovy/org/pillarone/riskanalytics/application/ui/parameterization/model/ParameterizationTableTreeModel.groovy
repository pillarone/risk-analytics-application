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

    public void addComponentNode(ComponentTableTreeNode parent, Component component) {
        parent.component.addSubComponent(component)
        ComponentTableTreeNode node = builder.createNewComponentNode(parent, component)
        node.comments = builder?.item?.comments?.findAll {it.path == node.path}
        nodesWereInserted(getPath(parent), parent.getIndex(node))
        notifyNodeValueChanged(node, -1)
        notifyComboBoxNodesComponentAdded(component)
        changedComments()
    }

    void componentAdded(String path, Component component) {
        String[] pathComponents = path.substring(0, path.lastIndexOf(":")).split(":")
        ComponentTableTreeNode parent = findComponentNode(pathComponents)
        parent.component.addSubComponent(component)
        ComponentTableTreeNode node = builder.createNewComponentNode(parent, component)
        node.comments = builder.item.comments?.findAll {it.path == node.path}
        nodesWereInserted(getPath(parent), parent.getIndex(node))
        notifyNodeValueChanged(node, -1)
        notifyComboBoxNodesComponentAdded(component)
        changedComments()
    }

    protected ComponentTableTreeNode findComponentNode(String[] pathComponents) {
        SimpleTableTreeNode node = findNode(pathComponents)
        if (node instanceof ComponentTableTreeNode) {
            return node
        }
        throw new IllegalArgumentException("No component found at ${pathComponents.join(":")}")
    }

    protected SimpleTableTreeNode findNode(String[] pathComponents) {
        SimpleTableTreeNode current = getRoot() as SimpleTableTreeNode
        for (String currentElement in pathComponents) {
            current = current.getChildByName(currentElement)
            if (current == null) {
                throw new IllegalArgumentException("Node with name $currentElement not found in path ${pathComponents.join(":")}")
            }
        }
        return current
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

    public void removeComponentNode(ComponentTableTreeNode componentNode) {
        builder.removeParameterFromNodes(componentNode)
        int childIndex = componentNode.parent.getIndex(componentNode)
        ComponentTableTreeNode parent = componentNode.parent
        parent.component.removeSubComponent(componentNode.component)
        parent.remove(componentNode)
        nodesWereRemoved(getPath(parent), [childIndex] as int[], [componentNode] as Object[])
        notifyNodeValueChanged(componentNode, -1)
        notifyComboBoxNodesComponentRemoved(componentNode.component)
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

    void parameterValuesChanged(List<String> paths) {
        for (SimpleTableTreeNode node in paths.collect { findNode(it.split(":")) }) {
            nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]))
        }
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