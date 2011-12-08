package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.applicationframework.application.form.BeanFormDialog;
import com.ulcjava.applicationframework.application.form.model.FormModel;
import com.ulcjava.base.application.*;
import com.ulcjava.base.application.event.ActionEvent;
import com.ulcjava.base.application.event.IActionListener;
import com.ulcjava.base.application.event.IWindowListener;
import com.ulcjava.base.application.event.WindowEvent;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.AbstractCategoryMapping;
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryColumnMapping;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.NameBean;


public class CategoryConfigurationDialog extends ULCDialog {

    AbstractCategoryMapping categoryMapping;
    ULCBoxPane contentPane;

    public CategoryConfigurationDialog(ULCWindow parent, AbstractCategoryMapping categoryMapping) {
        super(parent);

        this.categoryMapping = categoryMapping;

        createBeanView();

        boolean metalLookAndFeel = "Metal".equals(ClientContext.getLookAndFeelName());
        if (!metalLookAndFeel && ClientContext.getLookAndFeelSupportsWindowDecorations()) {
            setUndecorated(true);
            setWindowDecorationStyle(ULCDialog.PLAIN_DIALOG);
        }
        setTitle("Configure Categories");
        setLocationRelativeTo(parent);
    }

    @SuppressWarnings("serial")
    private void createBeanView() {
        ULCBoxPane contentPane = new ULCBoxPane(true);

        ULCSplitPane splitPane = new ULCSplitPane();
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane);

        ULCBoxPane categoryListPane = new ULCBoxPane();
        ULCList categoryList = new ULCList();
        IListModel categoryListModel = null; // categoryMapping.getCategories();
        categoryList.setModel(categoryListModel);
        splitPane.setLeftComponent(categoryListPane);

        ULCBoxPane categoryConfigPane = new ULCBoxPane();
        splitPane.setRightComponent(categoryConfigPane);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new IWindowListener() {
            public void windowClosing(WindowEvent event) {
            }
        });
        pack();
    }

    public void addSaveActionListener(IActionListener listener) {
    }

}
