package org.pillarone.riskanalytics.application.ui.base.view;

import com.ulcjava.base.application.IRendererComponent;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer;
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer;

import java.util.HashMap;

public class DelegatingCellRenderer extends DefaultTableTreeCellRenderer {

    private HashMap<Class, ITableTreeCellRenderer> renderers;

    public DelegatingCellRenderer(HashMap<Class, ITableTreeCellRenderer> renderers) {
        this.renderers = renderers;
    }

    @Override
    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree ulcTableTree, Object value,
                                                                boolean selected, boolean hasFocus, boolean expanded,
                                                                boolean leaf, Object node) {
        ITableTreeCellRenderer renderer = renderers.get(node.getClass());

        if (renderer != null) {
            return renderer.getTableTreeCellRendererComponent(ulcTableTree, value, selected, hasFocus, expanded, leaf, node);
        } else {
            return super.getTableTreeCellRendererComponent(ulcTableTree, value, selected, hasFocus, expanded, leaf, node);
        }
    }
}
