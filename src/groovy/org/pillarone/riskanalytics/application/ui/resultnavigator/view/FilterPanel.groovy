package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCLabel
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.FilterFactory
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCButton
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.table.TableRowFilter
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ITableRowFilterListener
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryColumnMapping
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.shared.UlcEventConstants

/**
 * @author martin.melchior
 */
class FilterPanel extends ULCBoxPane {

    CategoryColumnMapping categories
    FilterFactory filterFactory
    List<ITableRowFilterListener> filterListeners = []
    ULCComboBox categoryToFilter

    FilterPanel(CategoryColumnMapping categories) {
        super(false)
        this.categories = categories
        this.filterFactory = new FilterFactory(categories)
        createView()
    }

    void registerFilterListener(ITableRowFilterListener filterListener) {
        filterListeners << filterListener
    }

    void updateFilter(String filterType, String categoryToFilter, Object value) {
        TableRowFilter filter = filterFactory.getFilter(filterType, value, categoryToFilter)
        filterListeners.each { listener ->
            listener.setFilter(filter)
        }
    }

    void createView() {
        ULCBoxPane filterArea = new ULCBoxPane(false);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Filter Type: "));
        final ULCComboBox filterType = new ULCComboBox(FilterFactory.FILTERNAMES);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, filterType);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Category: "));

        categoryToFilter = new ULCComboBox(categories.getCategories());
        ClientContext.setModelUpdateMode(categoryToFilter.getModel(), UlcEventConstants.SYNCHRONOUS_MODE)
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, categoryToFilter);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Value: "));
        final ULCTextField filterValue = new ULCTextField(10);
        filterValue.setEditable(false);
        // TODO: Validation of what has been entered
        filterType.addActionListener(
                new IActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        String filterName = (String) filterType.getSelectedItem();
                        if (filterName.equalsIgnoreCase(FilterFactory.NONE)) {
                            filterValue.setEditable(false);
                        } else {
                            filterValue.setEditable(true);
                        }
                        updateFilter((String) filterType.getSelectedItem(), (String) categoryToFilter.getSelectedItem(), filterValue.getText())
                    }
                }
        );
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, filterValue);

        ULCButton clear = new ULCButton(UIUtils.getIcon("delete-active.png"));
        clear.setPreferredSize(new Dimension(16, 16));
        clear.setContentAreaFilled(false);
        clear.setOpaque(false);
        clear.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                filterValue.setText("");
                filterType.setSelectedItem(FilterFactory.NONE);
                updateFilter((String) filterType.getSelectedItem(), (String) categoryToFilter.getSelectedItem(), filterValue.getText())
            }
        });
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, clear);

        IActionListener action = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateFilter((String) filterType.getSelectedItem(), (String) categoryToFilter.getSelectedItem(), filterValue.getText())
            }
        };
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        filterValue.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
        this.add(ULCBoxPane.BOX_LEFT_CENTER, filterArea);
    }
}
