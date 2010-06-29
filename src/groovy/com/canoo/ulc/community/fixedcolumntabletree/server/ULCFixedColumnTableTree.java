/*
 * Copyright (c) 2005 Canoo Engineering AG, Switzerland.
 */
package com.canoo.ulc.community.fixedcolumntabletree.server;

import com.ulcjava.base.application.ULCScrollPane;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.event.ITreeSelectionListener;
import com.ulcjava.base.application.event.TreeSelectionEvent;
import com.ulcjava.base.application.tabletree.ITableTreeModel;
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn;
import com.ulcjava.base.application.tree.TreePath;
import com.ulcjava.base.application.tree.ULCTreeSelectionModel;
import com.ulcjava.base.shared.UlcEventCategories;
import com.ulcjava.base.shared.UlcEventConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>ULCFixedColumnTableTree</p>
 * <p/>
 * <p>Class that combines two ULCTableTree elements, where the first one has no scroller on its
 * columns, whereas the second one has horizontal scrollable columns. </p>
 * <p/>
 * <p>Both ULCTableTree objects must have the same model. </p>
 * <p/>
 * <p>Note: API methods of ULCFixedColumnTableTree overwrite corresponding API methods
 * of the class ULCTableTree! </p>
 */
public class ULCFixedColumnTableTree extends ULCScrollPane {

    List fOverallTreeSelectionListener = new ArrayList();
    final static int ROW_HEIGHT = 18;

    /**
     * <p>Creates a new  ULCFixedColumnTableTree element using the model, the first scrollable column,
     * the list of columns widths and the expandAll flag.
     * The expandAll parameter is used to expand all tree elements before the upload.</p>
     * <p/>
     * <p>The setRowSelectionAllowed(true) method is called as default within the constructor. </p>
     *
     * @param model
     * @param firstScrollableColumn
     * @param columnWidths
     * @param expandAll
     */


    public ULCFixedColumnTableTree(ITableTreeModel model, int firstScrollableColumn, int[] columnWidths, boolean expandAll, boolean createViewPortTreeColumns) {
        if (firstScrollableColumn > columnWidths.length) {
            throw new IllegalArgumentException("firstScrollableColumn and columnWidths length are not compatible");
        }

        if (model.getColumnCount() != columnWidths.length) {
            throw new IllegalArgumentException("model and columnWidths length are not compatible");
        }

        //see PMO-919 and PMO-1013
        ULCTableTree rowHeader = new ULCHeaderFixedTableTree();
        rowHeader.setAutoCreateColumnsFromModel(false);
        rowHeader.setModel(model);
        for (int i = 0; i < firstScrollableColumn; i++) {
            rowHeader.addColumn(new ULCTableTreeColumn(i, columnWidths[i]));
        }

        //see PMO-919 and PMO-1013
        ULCTableTree viewPort = new ULCHeaderFixedTableTree();
        viewPort.setAutoCreateColumnsFromModel(false);
        viewPort.setModel(model);
        viewPort.setEventDeliveryMode(UlcEventCategories.TREE_SELECTION_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE);
        if (createViewPortTreeColumns) {
            for (int i = firstScrollableColumn; i < columnWidths.length; i++) {
                viewPort.addColumn(new ULCTableTreeColumn(i, columnWidths[i]));
            }
        }
        rowHeader.setAutoResizeMode(ULCTableTree.AUTO_RESIZE_OFF);
        rowHeader.setRowHeight(ROW_HEIGHT);
        viewPort.setAutoResizeMode(ULCTableTree.AUTO_RESIZE_OFF);
        viewPort.setRowHeight(ROW_HEIGHT);
        rowHeader.setCellSelectionEnabled(false);
        viewPort.setCellSelectionEnabled(false);
        rowHeader.setRowSelectionAllowed(true);
        viewPort.setRowSelectionAllowed(true);
        rowHeader.getSelectionModel().setSelectionMode(ULCTreeSelectionModel.SINGLE_TREE_SELECTION);
        rowHeader.addTreeSelectionListener(new ForwardTreeSelectionListener());
        viewPort.addTreeSelectionListener(new ForwardTreeSelectionListener());
        super.setRowHeaderView(rowHeader);
        super.setViewPortView(viewPort);
        setCorner(ULCScrollPane.UPPER_LEFT_CORNER, rowHeader.getTableTreeHeader());
        if (expandAll) {
            expandAll();
        }
    }


    /**
     * <p>Overloading creator without expandAll, which is set to false
     * <p/>
     * <p>The setRowSelectionAllowed(true) method is called as default within the constructor.</p>
     *
     * @param model
     * @param firstScrollableColumn
     * @param columnWidths
     */
    public ULCFixedColumnTableTree(ITableTreeModel model, int firstScrollableColumn, int[] columnWidths) {
        this(model, firstScrollableColumn, columnWidths, false, true);
    }

    public void addTreeSelectionListener(ITreeSelectionListener selectionListener) {
        fOverallTreeSelectionListener.add(selectionListener);
    }

    public void removeTreeSelectionListener(ITreeSelectionListener selectionListener) {
        fOverallTreeSelectionListener.remove(selectionListener);
    }

    /**
     * <p>Sets the view component to be displayed in the viewport.</p>
     * <p>Performs the check that the viewPortView has the same model than the current rowHeaderView.  </p>
     *
     * @param viewPortView the component to display in the scroll pane's viewport
     */
    public void setViewPortView(ULCTableTree viewPortView) {
        if (viewPortView != null) {
            if (((ULCTableTree) getRowHeaderView()).getModel() != ((ULCTableTree) viewPortView).getModel()) {
                throw new IllegalArgumentException(
                        "the row header table tree and the view port table tree must have the same model");
            }
            super.setViewPortView(viewPortView);
        } else {
            throw new IllegalArgumentException("the argument must be a ULCTableTree");
        }
    }

    /**
     * <p>Sets the row header view component to be displayed in the row header viewport.</p>
     * <p>Performs the check that the rowHeaderView has the same model than the current viewPortView. </p>
     *
     * @param rowHeaderView the component to display in the scroll pane's row header viewport
     */
    public void setRowHeaderView(ULCTableTree rowHeaderView) {
        if (rowHeaderView != null) {
            if (((ULCTableTree) getViewPortView()).getModel() != ((ULCTableTree) rowHeaderView).getModel()) {
                throw new IllegalArgumentException(
                        "the row header table tree and the view port table tree must have the same model");
            }
            super.setRowHeaderView(rowHeaderView);
        } else {
            throw new IllegalArgumentException("the argument must be a ULCTableTree");
        }
    }

    /**
     * <p>Overwrites the corresponding setting of the contained ULCTableTree.  </p>
     * <p>It allows to synchronize both rowHeaderView and viewPortView. </p>
     *
     * @param rowSelectionAllowed
     */
    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
        ((ULCTableTree) getViewPortView()).setCellSelectionEnabled(false);
        ((ULCTableTree) getRowHeaderView()).setCellSelectionEnabled(false);
        ((ULCTableTree) getViewPortView()).setRowSelectionAllowed(rowSelectionAllowed);
        ((ULCTableTree) getRowHeaderView()).setRowSelectionAllowed(rowSelectionAllowed);
    }

    /**
     * <p>Overwrites the corresponding setting of the contained ULCTableTree. </p>
     * <p>It allows to synchronize both rowHeaderView and viewPortView.</p>
     *
     * @param columnSelectionAllowed
     */
    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
        ((ULCTableTree) getViewPortView()).setCellSelectionEnabled(false);
        ((ULCTableTree) getRowHeaderView()).setCellSelectionEnabled(false);
        ((ULCTableTree) getViewPortView()).setColumnSelectionAllowed(columnSelectionAllowed);
        ((ULCTableTree) getRowHeaderView()).setColumnSelectionAllowed(columnSelectionAllowed);
    }

    /**
     * <p>Overwrites the corresponding setting of the contained ULCTableTree.</p>
     * <p>It allows to synchronize both rowHeaderView and viewPortView.</p>
     *
     * @param cellSelectionEnabled
     */
    public void setCellSelectionEnabled(boolean cellSelectionEnabled) {
        ((ULCTableTree) getViewPortView()).setCellSelectionEnabled(cellSelectionEnabled);
        ((ULCTableTree) getRowHeaderView()).setCellSelectionEnabled(cellSelectionEnabled);
    }

    public ULCTableTree getRowHeaderTableTree() {
        return (ULCTableTree) getRowHeaderView();
    }

    public ULCTableTree getViewPortTableTree() {
        return (ULCTableTree) getViewPortView();
    }

    /*
    *  Not an API method, bacause can be called only before uploading.
    */

    public void expandAll() {
        ULCTableTree rowView = (ULCTableTree) getRowHeaderView();
        expand(rowView);
        ULCTableTree viewPortView = (ULCTableTree) getViewPortView();
        expand(viewPortView);
    }

    public void expandPath(TreePath path) {
        getRowHeaderTableTree().expandPath(path);
        getViewPortTableTree().expandPath(path);
    }

    public void expandPaths(TreePath[] paths, boolean includingDescendants) {
        getRowHeaderTableTree().expandPaths(paths, includingDescendants);
        getViewPortTableTree().expandPaths(paths, includingDescendants);
    }

    public TreePath[] getSelectedPaths() {
        return getRowHeaderTableTree().getSelectedPaths();
    }

    private void expand(ULCTableTree singleTableTree) {
        singleTableTree.expandAll();
    }

    protected String getPropertyPrefix() {
        return "panel";
    }

    protected String typeString() {
        return "com.canoo.ulc.community.fixedcolumntabletree.client.UIFixedColumnTableTree";
    }

    private class ForwardTreeSelectionListener implements ITreeSelectionListener {

        public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
            Iterator iter = fOverallTreeSelectionListener.iterator();
            while (iter.hasNext()) {
                boolean[] isAddedPaths = new boolean[treeSelectionEvent.getPaths().length];
                for (int i = 0; i < isAddedPaths.length; i++) {
                    isAddedPaths[i] = treeSelectionEvent.isAddedPath(treeSelectionEvent.getPaths()[i]);
                }
                TreeSelectionEvent overallTreeSelectionEvent = new TreeSelectionEvent(ULCFixedColumnTableTree.this, treeSelectionEvent.getPaths(), isAddedPaths);
                ITreeSelectionListener overallSelectionListener = (ITreeSelectionListener) iter.next();
                overallSelectionListener.valueChanged(overallTreeSelectionEvent);
            }
        }
    }
}


