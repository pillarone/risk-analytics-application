package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.help.ComponentHelp
import org.pillarone.riskanalytics.application.help.MissingHelpException
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

class OpenComponentHelp extends ResourceBasedAction {
    def tree

    OpenComponentHelp(tree) {
        super("OpenComponentHelp")
        this.tree = tree
    }

    void doActionPerformed(ActionEvent event) {
        def node = tree.selectedPath.lastPathComponent
        openHelp(node)
    }

    private openHelp(SimpleTableTreeNode node) {
        // do nothing
    }

    private openHelp(ComponentTableTreeNode node) {
        String osName = ClientContext.getSystemProperty("os.name").toUpperCase()
        if ("UNIX" == osName || "LINUX" == osName || "SOLARIS" == osName) {
            throw new MissingHelpException("Help not available for your operating system [$osName]")
        }

        String helpUrl = ComponentHelp.getHelpUrl(node.component, ClientContext.locale)
        if (helpUrl != null) {
            ClientContext.showDocument(helpUrl, "help")
        }
    }

}
