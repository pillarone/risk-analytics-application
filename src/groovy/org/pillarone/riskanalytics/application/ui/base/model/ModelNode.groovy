package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.model.Model

import static com.ulcjava.base.application.util.Font.PLAIN

@CompileStatic
class ModelNode extends DefaultMutableTableTreeNode implements INavigationTreeNode {

    private final Model model

    ModelNode(Model model) {
        super([model.name] as Object[])
        this.model = model
    }

    Model getModel() {
        return model
    }

    @Override
    String getName() {
        return model.name
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        return null
    }

    @Override
    ULCIcon getIcon() {
        return null
    }

    @Override
    Font getFont(String fontName, int fontSize) {
        return new Font(fontName, PLAIN, fontSize)
    }

    @Override
    String getToolTip() {
        return ''
    }

    @Override
    Class getItemClass() {
        model.class
    }
}