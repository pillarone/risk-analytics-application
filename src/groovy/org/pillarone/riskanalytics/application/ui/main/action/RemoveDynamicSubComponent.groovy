package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.IAction
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.UlcUtilities
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import com.ulcjava.base.application.ULCAlert
import org.pillarone.riskanalytics.core.components.ComponentUtils as CoreComponentUtils

class RemoveDynamicSubComponent extends ResourceBasedAction {

    def tree
    ParameterViewModel model
    private static final String BULLET = '\n- '
    private static final String VISUAL_PATH_SEPARATOR = ' > '


    public RemoveDynamicSubComponent(tree, ParameterViewModel model) {
        super("RemoveDynamicSubComponent")
        this.tree = tree
        this.model = model
        putValue(IAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));
    }

    public void doActionPerformed(ActionEvent event) {
        def node = tree.selectedPath.lastPathComponent
        if (node && ComponentUtils.isDynamicComposedSubComponentNode(node)) {
            String path = getPathName(node.parent, "${node.name}")
            // todo(sku): build i18n path
            List<String> referencingPaths = ParameterHolderFactory.referencingParametersPaths(model.builder.item, path)
            if (referencingPaths.size() == 0) {
                tree.model.removeComponentNode(node)
            }
            else {
                StringBuilder message = new StringBuilder()
                for (String refPath : referencingPaths) {
                    message.append(BULLET)
                    message.append(model.findNodeForPath(refPath).getDisplayPath())
                }
                ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(tree), "RemainingReferences",
                        [ message.toString() ] as List<String>)
                alert.show()
            }
        }
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
}
