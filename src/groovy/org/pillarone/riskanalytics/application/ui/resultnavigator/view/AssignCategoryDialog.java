package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.base.application.*;
import com.ulcjava.base.application.border.ULCAbstractBorder;
import com.ulcjava.base.application.event.*;
import com.ulcjava.base.application.util.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class AssignCategoryDialog extends ULCDialog {

    private ULCBoxPane buttonPane;
    private ULCBoxPane contentPane;
    private ULCButton saveButton;
    private ArrayList<IActionListener> saveActions;
    private ULCComboBox categorySelection;
    private ULCTextField memberAssignment;
    private List<String> categories;


    public AssignCategoryDialog(ULCWindow parent, List<String> categories) {
        super(parent);
        this.categories = categories;

        saveActions = new ArrayList<IActionListener>();
        boolean metalLookAndFeel = "Metal".equals(ClientContext.getLookAndFeelName());
        if (!metalLookAndFeel && ClientContext.getLookAndFeelSupportsWindowDecorations()) {
            setUndecorated(true);
            setWindowDecorationStyle(ULCDialog.PLAIN_DIALOG);
        }
        createView();
        setTitle("Assign Category");
        setLocationRelativeTo(parent);
    }

    @SuppressWarnings("serial")
    private void createView() {
        contentPane = new ULCBoxPane(true);
        contentPane.setBorder(createBorder(10, 10, 10, 10));

        ULCBoxPane assignment = new ULCBoxPane(false);
        assignment.setBorder(createBorder(0, 10, 0, 10));
        categorySelection = new ULCComboBox(categories);
        categorySelection.setEditable(false);
        assignment.add(ULCBoxPane.BOX_LEFT_EXPAND, categorySelection);
        assignment.add(ULCBoxPane.BOX_EXPAND_TOP, ULCFiller.createHorizontalGlue());

        memberAssignment = new ULCTextField(15);
        memberAssignment.setEditable(true);
        assignment.add(ULCBoxPane.BOX_RIGHT_EXPAND, memberAssignment);

        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, assignment);

        ULCBoxPane buttonRow = new ULCBoxPane(false);
        buttonRow.add(ULCBoxPane.BOX_EXPAND_TOP, ULCFiller.createHorizontalGlue());
        buttonPane = createButtonPane();
        buttonRow.add(ULCBoxPane.BOX_RIGHT_EXPAND, buttonPane);
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, buttonRow);

        this.add(contentPane);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new IWindowListener() {
            public void windowClosing(WindowEvent event) {
                setVisible(false);
            }
        });
        pack();
    }

    @SuppressWarnings("serial")
    private ULCBoxPane createButtonPane() {
        ULCButton cancelButton = new ULCButton("cancel");
        cancelButton.addActionListener(
                new IActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        AssignCategoryDialog.this.setVisible(false);
                    }
                }
        );
        ULCButton saveButton = new ULCButton("ok");
        setDefaultButton(saveButton);
        saveButton.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
                for (IActionListener listener : saveActions) {
                    listener.actionPerformed(event);
                }
            }
        });

        ULCBoxPane boxPane1 = new ULCBoxPane(false);
        boxPane1.setBorder(createBorder(0, 10, 0, 10));
        boxPane1.add(ULCBoxPane.BOX_RIGHT_EXPAND, saveButton);

        ULCBoxPane boxPane2 = new ULCBoxPane(false);
        boxPane2.setBorder(createBorder(0, 10, 0, 10));
        boxPane2.add(ULCBoxPane.BOX_LEFT_EXPAND, cancelButton);

        ULCBoxPane boxPane = new ULCBoxPane(false);
        boxPane.setBorder(createBorder(0, 10, 0, 10));
        boxPane.add(ULCBoxPane.BOX_LEFT_EXPAND, boxPane2);
        boxPane.add(ULCBoxPane.BOX_EXPAND_TOP, ULCFiller.createHorizontalGlue());
        boxPane.add(ULCBoxPane.BOX_RIGHT_EXPAND, boxPane1);

        return boxPane;
    }

    private ULCAbstractBorder createBorder(int top, int left, int buttom, int right) {
        return BorderFactory.createEmptyBorder(top, left, buttom, right);
    }

    public void addSaveActionListener(IActionListener saveActionListener) {
        saveActions.add(saveActionListener);
    }

    public String[] getAssignment() {
        return new String[]{categorySelection.getSelectedItem().toString(), memberAssignment.getText()};
    }

    public IListDataListener getCategoryComboBox() {
        return categorySelection;
    }
}
