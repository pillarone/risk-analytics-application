package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.base.application.*;
import com.ulcjava.base.application.event.*;
import com.ulcjava.base.application.util.Color;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.AbstractCategoryMapping;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryMatcher;

import java.util.Collection;


public class CategoryConfigurationDialog extends ULCDialog {

    AbstractCategoryMapping categoryMapping;
    ULCBoxPane contentPane;
    ULCBoxPane categoryConfigPane;
    ICategoryMatcher selectedMatcher;

    public CategoryConfigurationDialog(ULCWindow parent) {
        super(parent);
        boolean metalLookAndFeel = "Metal".equals(ClientContext.getLookAndFeelName());
        if (!metalLookAndFeel && ClientContext.getLookAndFeelSupportsWindowDecorations()) {
            setUndecorated(true);
            setWindowDecorationStyle(ULCDialog.PLAIN_DIALOG);
        }
        setTitle("Edit Categories");
        setLocationRelativeTo(parent);
    }

    @SuppressWarnings("serial")
    public void createContent(AbstractCategoryMapping categoryMapping) {
        this.categoryMapping = categoryMapping;

        final AbstractCategoryMapping mapping = categoryMapping;
        contentPane = new ULCBoxPane(true);

        ULCBoxPane categoryListPane = new ULCBoxPane(true);
        categoryListPane.setBorder(BorderFactory.createTitledBorder("Categories"));
        ULCList categoryList = new ULCList(); //categoryMapping.getCategories());
        categoryList.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION);
        categoryList.setSelectionBackground(Color.green);
        CategoryListModel categoryListModel = new CategoryListModel();
        categoryListModel.addAll(categoryMapping.getCategories());
        categoryList.setModel(categoryListModel);
        categoryListPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, categoryList);
        categoryList.setSelectedIndex(0);

        selectedMatcher = categoryMapping.getMatcher(categoryMapping.getCategories().get(0));
        categoryConfigPane = new ULCBoxPane(true);
        categoryConfigPane.setBorder(BorderFactory.createTitledBorder("How to Match Members"));

        ULCSplitPane splitPane = new ULCSplitPane(ULCSplitPane.HORIZONTAL_SPLIT, categoryListPane, categoryConfigPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.4);
        splitPane.setDividerSize(10);
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane);

        this.add(contentPane);
        pack();
        
        categoryList.addListSelectionListener(new IListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                String selectedCategory = mapping.getCategories().get(event.getFirstIndex());
                selectedMatcher = mapping.getMatcher(selectedCategory);
                showSelectedMatcher();
            }
        });
        showSelectedMatcher();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new IWindowListener() {
            public void windowClosing(WindowEvent event) {
                setVisible(false);
            }
        });

    }

    public void showSelectedMatcher() {
        try {
            categoryConfigPane.removeAll();
        } catch(Exception ex) {
            //
        }
        MatcherTree categoryMatcherTree = new MatcherTree(selectedMatcher);
        ULCScrollPane scrollPane = new ULCScrollPane(categoryMatcherTree);
        categoryConfigPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane);
    }

    public void addSaveActionListener(IActionListener listener) {
    }

    private class CategoryListModel extends AbstractListModel implements IMutableListModel {

        public void add(Object o) {
            categoryMapping.addCategory((String) o);
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
