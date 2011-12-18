package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.base.application.*;
import com.ulcjava.base.application.event.*;
import com.ulcjava.base.application.tree.TreePath;
import com.ulcjava.base.application.util.Color;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMatcherFactory;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryChangeListener;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryMatcher;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.MatcherTreeNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CategoryConfigurationDialog extends ULCDialog {

    CategoryMapping categoryMapping;
    ULCBoxPane contentPane;
    ULCCardPane matcherTreePane;
    ULCCardPane matcherConfigPane;
    ICategoryMatcher selectedCategoryTreeRoot;
    Map<String,ULCScrollPane> matcherTreeCache;
    Map<ICategoryMatcher,ULCBoxPane> matcherCache;


    public CategoryConfigurationDialog(ULCWindow parent) {
        super(parent);
        boolean metalLookAndFeel = "Metal".equals(ClientContext.getLookAndFeelName());
        if (!metalLookAndFeel && ClientContext.getLookAndFeelSupportsWindowDecorations()) {
            setUndecorated(false);
            setWindowDecorationStyle(ULCDialog.INFORMATION_DIALOG);
        }
        setTitle("Configure Categories");
        setLocationRelativeTo(parent);
        setSize(700, 500);
        matcherTreeCache = new HashMap<String,ULCScrollPane>();
        matcherCache = new HashMap<ICategoryMatcher,ULCBoxPane>();
    }

    @SuppressWarnings("serial")
    public void createContent(final CategoryMapping categoryMapping) {
        this.categoryMapping = categoryMapping;

        contentPane = new ULCBoxPane(true);

        ULCBoxPane categoryListPane = new ULCBoxPane(true);
        categoryListPane.setBorder(BorderFactory.createTitledBorder("Categories"));
        CategoryList categoryList = new CategoryList();
        CategoryListModel categoryListModel = new CategoryListModel();
        categoryListModel.addAll(categoryMapping.getCategories());
        categoryList.setModel(categoryListModel);
        categoryList.setSelectedIndex(0);
        categoryListPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, categoryList);
        categoryMapping.addCategoryChangeListener(categoryList);

        ULCBoxPane addCategoryPane = new ULCBoxPane(false);
        addCategoryPane.setBorder(BorderFactory.createTitledBorder("Add Category"));
        final ULCTextField categorySpec = new ULCTextField(40);
        categorySpec.setEditable(true);
        addCategoryPane.add(ULCBoxPane.BOX_LEFT_BOTTOM, categorySpec);
        ULCButton addCategoryButton = new ULCButton("Add");
        addCategoryPane.add(ULCBoxPane.BOX_LEFT_BOTTOM, addCategoryButton);
        addCategoryButton.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String newCategory = categorySpec.getText();
                categoryMapping.addCategory(newCategory);
            }
        });
        categoryListPane.add(ULCBoxPane.BOX_LEFT_BOTTOM,addCategoryPane);


        selectedCategoryTreeRoot = categoryMapping.getCategoryMatcher(categoryMapping.getCategories().get(0));
        matcherTreePane = new ULCCardPane();
        matcherTreePane.setBorder(BorderFactory.createTitledBorder("How to Match Members"));
        matcherConfigPane = new ULCCardPane();
        matcherConfigPane.setBorder(BorderFactory.createTitledBorder("Matcher Specification"));

        ULCSplitPane matcherEditArea = new ULCSplitPane(ULCSplitPane.VERTICAL_SPLIT, matcherTreePane, matcherConfigPane);
        matcherEditArea.setOneTouchExpandable(true);
        matcherEditArea.setDividerLocation(0.6);
        matcherEditArea.setDividerSize(10);

        ULCSplitPane splitPane = new ULCSplitPane(ULCSplitPane.HORIZONTAL_SPLIT, categoryListPane, matcherEditArea);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.4);
        splitPane.setDividerSize(10);
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane);

        this.add(contentPane);
        pack();
        
        categoryList.addListSelectionListener(new IListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int index = ((ULCListSelectionModel) event.getSource()).getSelectedIndices()[0];
                String selectedCategory = categoryMapping.getCategories().get(index);
                showSelectedMatcherTree(selectedCategory);
            }
        });
        String selectedCategory = categoryMapping.getCategories().get(0);
        showSelectedMatcherTree(selectedCategory);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new IWindowListener() {
            public void windowClosing(WindowEvent event) {
                setVisible(false);
            }
        });

    }

    public void showSelectedMatcherTree(String category) {
        selectedCategoryTreeRoot = categoryMapping.getCategoryMatcher(category);
        if (!matcherTreeCache.containsKey(category)){
            MatcherTree categoryMatcherTree = new MatcherTree(selectedCategoryTreeRoot);
            ULCScrollPane scrollPane = new ULCScrollPane(categoryMatcherTree);
            matcherTreePane.addCard(category, scrollPane);
            matcherTreeCache.put(category, scrollPane);
        }
        ULCScrollPane newPane = matcherTreeCache.get(category);
        final ULCTree tree = (ULCTree) newPane.getViewPortView();
        tree.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                TreePath selection = tree.getSelectionPath();
                Object o = selection.getLastPathComponent();
                if (o instanceof MatcherTreeNode) {
                    showMatcher(((MatcherTreeNode) o).getMatcher());
                }
            }
        });
        matcherTreePane.setSelectedComponent(newPane);
    }

    public void showMatcher(ICategoryMatcher matcher) {
        if (!matcherCache.containsKey(matcher)) {
            ULCBoxPane view = CategoryMatcherFactory.getMatcherView(matcher);
            if (view != null) {
                matcherConfigPane.addCard(matcher.toString(), view);
                matcherCache.put(matcher, view);
            }
        }
        ULCBoxPane view = matcherCache.get(matcher);
        if (view != null) {
            matcherConfigPane.setSelectedComponent(view);
        }
    }

    public void addSaveActionListener(IActionListener listener) {
    }

    private class CategoryList extends ULCList implements ICategoryChangeListener {

        CategoryList() {
            super();
            setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION);
            setSelectionBackground(Color.green);
        }

        public void categoryAdded(String category) {
        }

        public void categoryRemoved(String category) {
        }
    }

    private class CategoryListModel extends AbstractListModel implements IMutableListModel {

        public void add(Object o) {
            categoryMapping.addCategory((String) o);
            // fire change events ??
        }

        public void add(int i, Object o) {
            categoryMapping.addCategory((String) o);
        }

        public void addAll(Object[] objects) {
            for (Object o : objects) {
                categoryMapping.addCategory((String) o);
            }
        }

        public void addAll(Collection collection) {
            for (Object o : collection) {
                categoryMapping.addCategory((String) o);
            }
        }

        public void addAll(int i, Object[] objects) {
            for (Object o : objects) {
                categoryMapping.addCategory((String) o);
            }
        }

        public void addAll(int i, Collection collection) {
            for (Object o : collection) {
                categoryMapping.addCategory((String) o);
            }
        }

        public void clear() {
            categoryMapping.getMatcherMap().clear();
        }

        public Object remove(int i) {
            String category = categoryMapping.getCategories().get(i);
            boolean removed = categoryMapping.removeCategory(category);
            return removed ? category : null;
        }

        public boolean remove(Object o) {
            return categoryMapping.removeCategory((String)o);
        }

        public boolean removeAll(Object[] objects) {
            boolean test = true;
            for (Object o : objects) {
                test = test && remove(o);
            }
            return test;
        }

        public boolean removeAll(Collection collection) {
            boolean test = true;
            for (Object o : collection) {
                test = test && remove(o);
            }
            return test;
        }

        public Object set(int i, Object o) {
            categoryMapping.addCategory((String)o);
            return o;
        }

        public int getSize() {
            return categoryMapping.getNumberOfCategories();
        }

        public Object getElementAt(int i) {
            return categoryMapping.getCategories().get(i);
        }
    }
}
