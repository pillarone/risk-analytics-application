package org.pillarone.riskanalytics.application.ui.pivot.model

import com.ulcjava.base.application.tabletree.DefaultTableTreeModel

import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableModel
import org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator.Dimension
import org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator.TreeStructureTableModel
import org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator.TreeStructureModel
import org.pillarone.riskanalytics.application.ui.pivot.model.DataNavigator.PreviewNode

class PivotModel {
    TreeStructureModel treeStructureModel
    TreeStructureTableModel dimensionTableModel
    Map<Integer, TreeStructureTableModel> coordinateTableModels = new HashMap<Integer, TreeStructureTableModel>()

    DefaultTableTreeModel previewTableTreeModel
    PreviewNode previewRootNode

    CustomTableModel customTableModel

    Random rand = new Random()


    PivotModel(TreeStructureModel treeStructureModel) {
        this.treeStructureModel = treeStructureModel

        List<Dimension> dimensions = treeStructureModel.getDimensions()

        dimensionTableModel = new TreeStructureTableModel (new LinkedList<Object[]>(), (String[])["Selected", "Name"])
        for (dimension in dimensions) {
            dimensionTableModel.addRow([rand.nextBoolean(), dimension.name, dimension.id].toArray())

            TreeStructureTableModel coordinateTableModel = new TreeStructureTableModel (new LinkedList<Object[]>(), (String[])["Selected", "Name"])
            for (coordinate in dimension.coordinates) {
                coordinateTableModel.addRow([rand.nextBoolean(), coordinate.name, coordinate.id].toArray())
            }
            coordinateTableModels.put (dimension.id, coordinateTableModel)
        }

        previewRootNode = new PreviewNode()
        previewTableTreeModel = new DefaultTableTreeModel (previewRootNode, (String[]) ["Name", "Random Value"])
        updatePreviewTree()
        previewTableTreeModel.setRoot (previewRootNode)

        customTableModel = new CustomTableModel (new LinkedList<List<Object>>(), [])
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
                            PreviewNode newNode = new PreviewNode ((Object[])[coordinateTableModel.getValueAt (cooRow, 1), (rand.nextDouble()*10000).round(2)])

                            // Add new Node to the list with the Nodes of the current Level
                            currentLevelNodes.add (newNode)

                            // add new Node to the Node
                            n.addChild(newNode)
                        }
                    }
                }

                pastLevelNodes = currentLevelNodes;
            }
        }

        previewTableTreeModel.structureChanged()
    }
}
