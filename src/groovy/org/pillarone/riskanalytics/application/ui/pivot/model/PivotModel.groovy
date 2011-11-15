package org.pillarone.riskanalytics.application.ui.pivot.model

import be.devijver.wikipedia.parser.ast.Pre
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory.Default
import com.ulcjava.base.application.table.DefaultTableModel

class PivotModel {
    TreeStructureModel treeStructureModel
    TreeStructureTableModel dimensionTableModel
    Map<Integer, TreeStructureTableModel> coordinateTableModels = new HashMap<Integer, TreeStructureTableModel>()

    DefaultTableTreeModel previewTableTreeModel
    PreviewNode previewRootNode

    DefaultTableModel newTableModel


    PivotModel(TreeStructureModel treeStructureModel) {
        this.treeStructureModel = treeStructureModel

        List<Dimension> dimensions = treeStructureModel.getDimensions()

        dimensionTableModel = new TreeStructureTableModel (new LinkedList<Object[]>(), (String[])["Selected", "Name"])
        for (dimension in dimensions) {
            dimensionTableModel.addRow([false, dimension.name, dimension.id].toArray())

            TreeStructureTableModel coordinateTableModel = new TreeStructureTableModel (new LinkedList<Object[]>(), (String[])["Selected", "Name"])
            for (coordinate in dimension.coordinates) {
                coordinateTableModel.addRow([false, coordinate.name, coordinate.id].toArray())
            }
            coordinateTableModels.put (dimension.id, coordinateTableModel)
        }

        previewRootNode = new PreviewNode()
        previewTableTreeModel = new DefaultTableTreeModel (previewRootNode, (String[]) ["Name", "ID"])


        newTableModel = new DefaultTableModel(new Object[0][0], (String[])["Column1", "Colum2"])
        newTableModel.addRow (["test", "bla"])
        newTableModel.addRow (["test2", "bla2"])
        newTableModel.addRow (["test3", "bla3"])
    }

    void updatePreviewTree () {
        previewRootNode.removeAllChildren()

        List<PreviewNode> pastLevelNodes = new LinkedList<PreviewNode>()
        pastLevelNodes.add (previewRootNode)

        List<PreviewNode> currentLevelNodes
        
        // Loop over all Dimensions
        for (int dimRow = 0; dimRow < dimensionTableModel.rowCount; dimRow++) {
            
            // Dimension selected?
            if (dimensionTableModel.getValueAt(dimRow, 0) == true) {

                // Add new list to the nodeListMap
                currentLevelNodes = new LinkedList<PreviewNode>()

                int dimID = dimensionTableModel.getID (dimRow)
                TreeStructureTableModel coordinateTableModel = coordinateTableModels.get (dimID)

                // Loop over all Coordinates in the current dimension
                for (int cooRow = 0; cooRow < coordinateTableModel.rowCount; cooRow++) {

                    // Coordinate selected?
                    if (coordinateTableModel.getValueAt(cooRow, 0) == true) {

                        // Add new Node to all Nodes of the past Level
                        for (PreviewNode n : pastLevelNodes) {
                            PreviewNode newNode = new PreviewNode ((Object[])[coordinateTableModel.getValueAt (cooRow, 1), coordinateTableModel.getValueAt (cooRow, 2)])

                            // Add new Node to the list with the Nodes of the current Level
                            currentLevelNodes.add (newNode)

                            // add new Node to the Node
                            n.addChild(newNode)
                        }
                    }
                }
            }

            pastLevelNodes = currentLevelNodes;
        }

        previewTableTreeModel.structureChanged()
    }
}
