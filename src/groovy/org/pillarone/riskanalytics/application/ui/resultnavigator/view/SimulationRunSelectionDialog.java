package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.applicationframework.application.form.BeanFormDialog;
import com.ulcjava.base.application.ClientContext;
import com.ulcjava.base.application.ULCButton;
import com.ulcjava.base.application.ULCDialog;
import com.ulcjava.base.application.ULCWindow;
import com.ulcjava.base.application.event.*;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.NameBean;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.SimulationRunFormModel;
import org.pillarone.riskanalytics.core.output.SimulationRun;


public class SimulationRunSelectionDialog extends ULCDialog {
    private BeanFormDialog<SimulationRunFormModel> fBeanForm;
    private SimulationRun selectedRun;
    private ULCButton fCancel;

    public SimulationRunSelectionDialog(ULCWindow parent) {
        super(parent);
        boolean metalLookAndFeel = "Metal".equals(ClientContext.getLookAndFeelName());
        if (!metalLookAndFeel && ClientContext.getLookAndFeelSupportsWindowDecorations()) {
            setUndecorated(true);
            setWindowDecorationStyle(ULCDialog.PLAIN_DIALOG);
        }
        createBeanView();
        setTitle("Select Simulation Run");
        setLocationRelativeTo(parent);
    }

    @SuppressWarnings("serial")
    private void createBeanView() {
        SimulationRunFormModel model = new SimulationRunFormModel(new NameBean());
        SimulationRunSelectionForm form = new SimulationRunSelectionForm(model);
        fBeanForm = new BeanFormDialog<SimulationRunFormModel>(form);
        add(fBeanForm.getContentPane());

        // cancel
        fCancel = new ULCButton("Cancel");
        IActionListener cancelAction = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                fBeanForm.reset();
                setVisible(false);
            }
        };
        fCancel.addActionListener(cancelAction);
        fBeanForm.addToButtons(fCancel);

        // ok
        IActionListener action = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                selectedRun = fBeanForm.getModel().getSelected();
                setVisible(false);
            }
        };
        fBeanForm.addSaveActionListener(action);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new IWindowListener() {
            public void windowClosing(WindowEvent event) {
                fBeanForm.interceptIfDirty(new Runnable() {
                    public void run() {
                        setVisible(false);
                    }
                });
            }
        });
        pack();
    }

    public SimulationRun getSelectRun() {
        return selectedRun;
    }

    public void addSaveActionListener(IActionListener listener) {
        fBeanForm.addSaveActionListener(listener);
    }
}
