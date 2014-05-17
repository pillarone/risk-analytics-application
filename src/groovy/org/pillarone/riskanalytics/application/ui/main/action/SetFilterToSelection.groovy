package org.pillarone.riskanalytics.application.ui.main.action
import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.search.AllFieldsFilter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
/**
 * User: bzetterstrom
 * Date: 1/5/12
 *
 * frahman 2014-01-02
 * This class represents the context menu action that copies a selected Pn node's name and version to the clipboard.
 * It is currently restricted to work on Parameterizations, unfortunately.
 *
 * Note that nevertheless it is possible to use the CTRL-C keyboard shortcut to copy the names of selected nodes
 * to the clipboard.
 *
 * For example you can paste the clipboard into a text editor later to do whatever (even for counting the items,
 * something currently lacking).
 *
 * This menu currently only appears on Pns and not on Sims because it's only used in AbstractParameterNodePopupMenu.
 */
class SetFilterToSelection extends SelectionTreeAction {
    SetFilterToSelection(ULCTableTree tree) {
        super("SetFilterToSelection", tree)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        List<ParameterizationNode> elements = getSelectedObjects(Parameterization.class)
        final StringBuilder sb = new StringBuilder();
        for (ParameterizationNode node : elements) {
            if (sb.length() > 0) {
                sb.append(AllFieldsFilter.OR_SEPARATOR)
            }
            sb.append(node.getItemNodeUIItem().getNameAndVersion())
        }
        ULCClipboard.getClipboard().content = sb.toString()
    }


}
