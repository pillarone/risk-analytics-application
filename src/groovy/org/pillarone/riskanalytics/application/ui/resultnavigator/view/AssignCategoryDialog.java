package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.base.application.*;
import com.ulcjava.base.application.event.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog that allows specify a category value for a given category (column) in the OutputElementTable
 */
public class AssignCategoryDialog extends ULCDialog {

    private List<String> categories;

    private ULCComboBox categorySelection;
    private ULCTextField assignValueField;

    private ArrayList<IActionListener> saveActionsListeners;

    public AssignCategoryDialog(ULCWindow parent, List<String> categories) {
        super(parent);
        this.categories = categories;
        saveActionsListeners = new ArrayList<IActionListener>();

        // some look&feel, title, ...etc.
        boolean metalLookAndFeel = "Metal".equals(ClientContext.getLookAndFeelName());
        if (!metalLookAndFeel && ClientContext.getLookAndFeelSupportsWindowDecorations()) {
            setUndecorated(true);
            setWindowDecorationStyle(ULCDialog.PLAIN_DIALOG);
        }
        setTitle("Assign Category");
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new IWindowListener() {
            public void windowClosing(WindowEvent event) {
                setVisible(false);
            }
        });

        // create view
        createView();
    }

    @SuppressWarnings("serial")
    private void createView() {
        // initialize components
        ULCBoxPane contentPane = new ULCBoxPane(true);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ULCBoxPane assignValuePane = new ULCBoxPane(false);
        assignValuePane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        categorySelection = new ULCComboBox(categories);
        categorySelection.setEditable(false);
        assignValueField = new ULCTextField(15);
        assignValueField.setEditable(true);
        ULCBoxPane buttonRow = new ULCBoxPane(false);
        ULCBoxPane buttonPane = createButtonPane();

        // layout components
        assignValuePane.add(ULCBoxPane.BOX_LEFT_EXPAND, categorySelection);
        assignValuePane.add(ULCBoxPane.BOX_EXPAND_TOP, ULCFiller.createHorizontalGlue());
        assignValuePane.add(ULCBoxPane.BOX_RIGHT_EXPAND, assignValueField);
        buttonRow.add(ULCBoxPane.BOX_EXPAND_TOP, ULCFiller.createHorizontalGlue());
        buttonRow.add(ULCBoxPane.BOX_RIGHT_EXPAND, buttonPane);
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, assignValuePane);
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, buttonRow);

        this.add(contentPane);

        pack();
    }


    @SuppressWarnings("serial")
    private ULCBoxPane createButtonPane() {
        ULCButton cancelButton = new ULCButton("cancel");
        ULCButton saveButton = new ULCButton("ok");
        ULCBoxPane boxPane = new ULCBoxPane(false);
        boxPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        ULCBoxPane boxPane1 = new ULCBoxPane(false);
        boxPane1.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        ULCBoxPane boxPane2 = new ULCBoxPane(false);
        boxPane2.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        setDefaultButton(saveButton);

        // layout components
        boxPane1.add(ULCBoxPane.BOX_RIGHT_EXPAND, saveButton);
        boxPane2.add(ULCBoxPane.BOX_LEFT_EXPAND, cancelButton);
        boxPane.add(ULCBoxPane.BOX_LEFT_EXPAND, boxPane2);
        boxPane.add(ULCBoxPane.BOX_EXPAND_TOP, ULCFiller.createHorizontalGlue());
        boxPane.add(ULCBoxPane.BOX_RIGHT_EXPAND, boxPane1);

        // attach listeners
        cancelButton.addActionListener(
                new IActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        AssignCategoryDialog.this.setVisible(false);
                    }
                }
        );
        saveButton.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
                for (IActionListener listener : saveActionsListeners) {
                    listener.actionPerformed(event);
                }
            }
        });

        return boxPane;
    }

    public void addSaveActionListener(IActionListener saveActionListener) {
        saveActionsListeners.add(saveActionListener);
    }

    public String[] getAssignedValue() {
        return new String[]{categorySelection.getSelectedItem().toString(), assignValueField.getText()};
    }

    public IListDataListener getCategoryComboBox() {
        return categorySelection;
    }
}
