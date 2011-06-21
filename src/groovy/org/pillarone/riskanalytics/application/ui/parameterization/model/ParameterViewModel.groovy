package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.base.model.PropertiesViewModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import org.pillarone.riskanalytics.application.ui.comment.view.TabbedPaneChangeListener
import org.pillarone.riskanalytics.application.ui.main.action.SaveAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterInjector
import org.pillarone.riskanalytics.core.parameterization.ParameterWriter
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ParameterViewModel extends AbstractCommentableItemModel {

    ParameterizationTableTreeModel paramterTableTreeModel
    PropertiesViewModel propertiesViewModel

    List<ParameterValidation> validationErrors = []
    public RiskAnalyticsMainModel mainModel

    public ParameterViewModel(Model model, Parameterization parameterization, ModelStructure structure) {
        super(model, parameterization, structure);
        propertiesViewModel = new PropertiesViewModel(parameterization)
    }

    protected ITableTreeModel buildTree() {
        builder = new ParameterizationTreeBuilder(model, structure, item)
        periodCount = builder.periodCount
        paramterTableTreeModel = new ParameterizationTableTreeModel(builder)
        paramterTableTreeModel.simulationModel = model
        paramterTableTreeModel.addValueChangedListener(
                [valueChanged: {Object node, int column -> item.changed = true}] as TableTreeValueChangedListener)
        paramterTableTreeModel.readOnly = !item.isEditable()
        return paramterTableTreeModel
    }


    @Override
    IActionListener getSaveAction(ULCComponent parent) {
        return new SaveAction(parent, mainModel, mainModel?.getAbstractUIItem(item))
    }



    void save() {
        ParameterWriter writer = new ParameterWriter()
        File file = new File("${modellingFileName}.groovy")

        File tmpFile = File.createTempFile("tmpParam", ".groovy")
        writer.write(builder.parameterConfigObject, tmpFile.newWriter())
        try {
            new ParameterInjector(tmpFile.absolutePath - ".groovy")
        } catch (Exception e) {
            throw new IllegalArgumentException("Error in parameter. Can not write parameter file:${file.name}. original message: ${e.message}")
        }


        writer.write(builder.parameterConfigObject, file.newWriter())
    }

    SimpleTableTreeNode findNodeForPath(String path) {
        String[] pathElements = path.split(":")
        SimpleTableTreeNode currentNode = paramterTableTreeModel.root
        for (String p in pathElements) {
            currentNode = currentNode?.getChildByName(p)
        }
        return currentNode
    }

    void addErrors(List<ParameterValidation> validationErrors) {
        for (ParameterValidation error in validationErrors) {
            ParameterizationTableTreeNode node = findNodeForPath(error.getPath())
            node.errorMessage = error.getLocalizedMessage(LocaleResources.getLocale())
            paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
        }
        for (ParameterValidation previousError in this.validationErrors) {
            ParameterValidation currentError = validationErrors.find { it.path == previousError.path}
            //Error is resolved now
            if (currentError == null) {
                ParameterizationTableTreeNode node = findNodeForPath(previousError.getPath())
                //if node is null, the error node was removed
                if (node != null) {
                    node.errorMessage = null
                    paramterTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), 0)
                }
            }
        }
        this.validationErrors = validationErrors
    }

    void setReadOnly(boolean value) {
        paramterTableTreeModel.readOnly = value
    }

    public boolean isReadOnly() {
        return paramterTableTreeModel.readOnly
    }

    void tabbedPaneChanged(CommentFilter filter) {
        tabbedPaneChangeListeners.each {TabbedPaneChangeListener listener ->
            listener.tabbedPaneChanged(filter)
        }
    }

    ITableTreeModel getTableTreeModel() {
        return paramterTableTreeModel
    }

}


