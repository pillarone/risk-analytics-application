package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

class RemoveDynamicSubComponentAction extends ResourceBasedAction {

    def tree
    ParameterViewModel model
    private static final String BULLET = '\n- '
    private static final String VISUAL_PATH_SEPARATOR = ' > '


    public RemoveDynamicSubComponentAction(tree, ParameterViewModel model) {
        super("RemoveDynamicSubComponent")
        this.tree = tree
        this.model = model
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        def node = tree.selectedPath.lastPathComponent
        if (node && ComponentUtils.isDynamicComposedSubComponentNode(node) && isEditable(node)) {
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "RemoveDynamicSubComponentAlert")
            alert.addWindowListener([windowClosing: { WindowEvent e ->
                if (isOKPressed(alert)) removeSubComponent(node)
            }] as IWindowListener)
            alert.show()
        }
    }

    protected removeSubComponent(def node) {
        String path = getPathName(node.parent, "${node.name}")
        // todo(sku): build i18n path
        List<String> referencingPaths = ParameterHolderFactory.referencingParametersPaths(model.builder.item, path, node.component)
        if (referencingPaths.size() == 0) {
            trace("Remove node ${node.name}")
            model.parametrizedItem.removeComponent(node.path)
        } else {
            trace("Remove failed due to References ${referencingPaths} on ${node.name}")
            StringBuilder message = new StringBuilder()
            for (String refPath : referencingPaths) {
                message.append(BULLET)
                message.append(model.findNodeForPath(refPath).getDisplayPath())
            }
            ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "RemainingReferences", [message.toString()])
            alert.show()
        }
    }

    static boolean isOKPressed(def alert) {
        alert.value?.equals(alert.firstButtonLabel)
    }

    public boolean isEnabled() {
        return super.isEnabled() && !model.paramterTableTreeModel.readOnly;
    }

    protected String getPathName(ITableTreeNode node, String name) {
        String pathName = node.path
        if (pathName.startsWith(model.model.name)) {
            pathName = pathName.substring(pathName.indexOf(":") + 1, pathName.length())
        }
        return pathName + ":${name}"
    }

    private boolean isEditable(def node) {
        def model = model.paramterTableTreeModel
        !model.readOnly && model.isNodeInEditablePaths(node)
    }
}
