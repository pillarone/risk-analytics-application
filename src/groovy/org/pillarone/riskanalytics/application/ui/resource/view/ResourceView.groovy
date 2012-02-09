package org.pillarone.riskanalytics.application.ui.resource.view

import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCContainer
import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.ui.base.view.DelegatingCellEditor
import org.pillarone.riskanalytics.application.ui.base.view.DelegatingCellRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.base.view.ComponentNodeTableTreeNodeRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.action.MultiDimensionalTabStarter
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.parameterization.view.SelectionTracker
import com.ulcjava.base.application.DefaultCellEditor
import com.ulcjava.base.application.ULCTextField
import org.pillarone.riskanalytics.application.ui.parameterization.view.BasicCellEditor
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.view.ComboBoxCellComponent
import org.pillarone.riskanalytics.application.ui.parameterization.view.CheckBoxCellComponent
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor
import org.pillarone.riskanalytics.application.ui.parameterization.model.SimpleValueParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.DoubleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BooleanTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.IntegerTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.DateParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.EnumParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationClassifierTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ResourceParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ConstrainedStringParameterizationTableTreeNode
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.view.BasicCellRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalCellRenderer
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.base.action.OpenMDPAction
import org.pillarone.riskanalytics.application.ui.base.action.TableTreeCopier
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodePaster
import org.pillarone.riskanalytics.application.ui.comment.action.InsertCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterizationTableTreeNode
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCCheckBox
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView


class ResourceView extends AbstractModellingTreeView {

    CommentAndErrorView commentAndErrorView


    ResourceView(ResourceViewModel model) {
        super(model)
    }

    @Override
    protected void initComponents() {
        commentAndErrorView = new CommentAndErrorView(model)
        super.initComponents()
    }

    @Override
    protected void initTree() {
        def treeModel = model.treeModel

        int treeWidth = UIUtils.calculateTreeWidth(treeModel.root)
        def columnsWidths = Math.max(UIUtils.calculateColumnWidth(treeModel.root, 1) + 10, 150)

        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + [columnsWidths] * model.periodCount) as int[])


        tree.viewPortTableTree.name = "resourceTreeContent"
        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->

            it.setCellEditor(new DelegatingCellEditor(createEditorConfiguration()))
            it.setCellRenderer(new DelegatingCellRenderer(createRendererConfiguration(index + 1, tree.viewPortTableTree)))
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }
//        ComponentNodeTableTreeNodeRenderer renderer = new ComponentNodeTableTreeNodeRenderer(tree, model, commentAndErrorView)


        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
//            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.name = "resourceTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true

//        tree.viewPortTableTree.addActionListener(new MultiDimensionalTabStarter(this))


        tree.getRowHeaderTableTree().expandPaths([new TreePath([model.treeModel.root] as Object[])] as TreePath[], false);
        commentAndErrorView.tableTree = tree
        new SelectionTracker(tree)
    }

    private Map createEditorConfiguration() {
        DefaultCellEditor defaultEditor = new DefaultCellEditor(new ULCTextField());
        DefaultCellEditor doubleEditor = new BasicCellEditor(DataTypeFactory.getDoubleDataTypeForEdit());
        DefaultCellEditor integerEditor = new BasicCellEditor(DataTypeFactory.getIntegerDataTypeForEdit());
        DefaultCellEditor dateEditor = new BasicCellEditor(DataTypeFactory.getDateDataType());

        ComboBoxCellComponent comboBoxEditor = new ComboBoxCellComponent();
        CheckBoxCellComponent checkBoxEditor = new CheckBoxCellComponent();

        Map editors = new HashMap<Class, ITableTreeCellEditor>();
        editors.put(SimpleValueParameterizationTableTreeNode.class,
                defaultEditor);
        editors.put(DoubleTableTreeNode.class,
                doubleEditor);
        editors.put(BooleanTableTreeNode.class, checkBoxEditor);
        editors.put(IntegerTableTreeNode.class,
                integerEditor);
        editors.put(DateParameterizationTableTreeNode.class,
                dateEditor);
        editors.put(EnumParameterizationTableTreeNode.class,
                comboBoxEditor);
        editors.put(ParameterizationClassifierTableTreeNode.class,
                comboBoxEditor);
        editors.put(ResourceParameterizationTableTreeNode.class,
                comboBoxEditor);
        editors.put(ConstrainedStringParameterizationTableTreeNode.class,
                comboBoxEditor);

        return editors
    }

    private Map createRendererConfiguration(int columnIndex, ULCTableTree tree) {
        BasicCellRenderer defaultRenderer = new BasicCellRenderer(columnIndex);
        MultiDimensionalCellRenderer mdpRenderer = new MultiDimensionalCellRenderer(columnIndex);
        BasicCellRenderer doubleRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getDoubleDataTypeForNonEdit());
        BasicCellRenderer integerRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getIntegerDataTypeForNonEdit());
        BasicCellRenderer dateRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getDateDataType());
        ComboBoxCellComponent comboBoxRenderer = new ComboBoxCellComponent();
        CheckBoxCellComponent checkBoxRenderer = new CheckBoxCellComponent();

        ULCPopupMenu menu = new ULCPopupMenu();
        ULCPopupMenu mdpMenu = new ULCPopupMenu();
        mdpMenu.add(new ULCMenuItem(new OpenMDPAction(tree)))

        TableTreeCopier copier = new TableTreeCopier();
        copier.setTable(tree);
        menu.add(new ULCMenuItem(copier));
        mdpMenu.add(new ULCMenuItem(copier));
        TreeNodePaster paster = new TreeNodePaster();
        paster.setTree(tree);
        menu.add(new ULCMenuItem(paster));
        mdpMenu.add(new ULCMenuItem(paster));
        InsertCommentAction insertComment = new InsertCommentAction(tree, (columnIndex - 1) % model.periodCount)
        insertComment.addCommentListener commentAndErrorView
        ShowCommentsAction showCommentsAction = new ShowCommentsAction(tree, (columnIndex - 1) % model.periodCount, false)
        showCommentsAction.addCommentListener commentAndErrorView

        mdpMenu.addSeparator()
        mdpMenu.add(new ULCMenuItem(insertComment))
        mdpMenu.add(new ULCMenuItem(showCommentsAction))

        menu.addSeparator()
        menu.add(new ULCMenuItem(insertComment))
        menu.add(new ULCMenuItem(showCommentsAction))

        defaultRenderer.setMenu(menu)
        doubleRenderer.setMenu(menu)
        integerRenderer.setMenu(menu)
        dateRenderer.setMenu(menu)
        initComboBox(comboBoxRenderer, menu);
        initCheckBox(checkBoxRenderer, menu);
        mdpRenderer.setMenu(mdpMenu)

        Map renderers = new HashMap<Class, ITableTreeCellRenderer>();
        renderers.put(SimpleValueParameterizationTableTreeNode.class,
                defaultRenderer);
        renderers.put(DoubleTableTreeNode.class,
                doubleRenderer);
        renderers.put(BooleanTableTreeNode.class, checkBoxRenderer);
        renderers.put(IntegerTableTreeNode.class,
                integerRenderer);
        renderers.put(DateParameterizationTableTreeNode.class,
                dateRenderer);
        renderers.put(EnumParameterizationTableTreeNode.class,
                comboBoxRenderer);
        renderers.put(ParameterizationClassifierTableTreeNode.class,
                comboBoxRenderer);
        renderers.put(ConstrainedStringParameterizationTableTreeNode.class,
                comboBoxRenderer);
        renderers.put(ResourceParameterizationTableTreeNode.class,
                comboBoxRenderer);
        renderers.put(MultiDimensionalParameterizationTableTreeNode.class,
                mdpRenderer);

        return renderers
    }

    private void initComboBox(ULCComboBox renderer, ULCPopupMenu menu) {
        renderer.setComponentPopupMenu(menu);
    }

    private void initCheckBox(ULCCheckBox renderer, ULCPopupMenu menu) {
        renderer.setComponentPopupMenu(menu);
    }
}
