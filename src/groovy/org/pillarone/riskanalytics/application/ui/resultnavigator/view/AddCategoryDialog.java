package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.applicationframework.application.form.BeanFormDialog;
import com.ulcjava.applicationframework.application.form.model.FormModel;
import com.ulcjava.base.application.ClientContext;
import com.ulcjava.base.application.ULCButton;
import com.ulcjava.base.application.ULCDialog;
import com.ulcjava.base.application.ULCWindow;
import com.ulcjava.base.application.event.ActionEvent;
import com.ulcjava.base.application.event.IActionListener;
import com.ulcjava.base.application.event.IWindowListener;
import com.ulcjava.base.application.event.WindowEvent;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.NameBean;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.SimulationRunFormModel;
import org.pillarone.riskanalytics.core.output.SimulationRun;


public class AddCategoryDialog extends ULCDialog {
    private BeanFormDialog<FormModel<NameBean>> beanForm;
    private ULCButton cancel;
    private String categoryName;

    public AddCategoryDialog(ULCWindow parent) {
        super(parent);
        boolean metalLookAndFeel = "Metal".equals(ClientContext.getLookAndFeelName());
        if (!metalLookAndFeel && ClientContext.getLookAndFeelSupportsWindowDecorations()) {
            setUndecorated(true);
            setWindowDecorationStyle(ULCDialog.PLAIN_DIALOG);
        }
        createBeanView();
        setTitle("Add Category");
        setLocationRelativeTo(parent);
    }

    @SuppressWarnings("serial")
    private void createBeanView() {
        AddCategoryForm form = new AddCategoryForm();
        beanForm = new BeanFormDialog<FormModel<NameBean>>(form);
        add(beanForm.getContentPane());

        // cancel
        cancel = new ULCButton("Cancel");
        IActionListener cancelAction = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                beanForm.reset();
                setVisible(false);
            }
        };
        cancel.addActionListener(cancelAction);
        beanForm.addToButtons(cancel);

        // ok
        IActionListener ok = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                categoryName = beanForm.getModel().getBean().getName();
                setVisible(false);
            }
        };
        beanForm.addSaveActionListener(ok);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new IWindowListener() {
            public void windowClosing(WindowEvent event) {
                beanForm.interceptIfDirty(new Runnable() {
                    public void run() {
                        setVisible(false);
                    }
                });
            }
        });
        pack();
    }

    public void addSaveActionListener(IActionListener listener) {
        beanForm.addSaveActionListener(listener);
    }

    public String getCategoryName() {
        return categoryName;
    }
}
