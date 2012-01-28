package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IKeyListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.table.TableRowFilter
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.KeyfigureSelectionModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.StatisticsKeyfigure
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.FilterFactory
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ITableRowFilterListener
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * Toolbar-like panel for
 * <ul>
 *     <it> selecting and configuring filters to be applied to the table and </it>
 *     <it> for the selecting the period and the statistics keyfigure (incl. parameters if applicable)
 *     so that this information can be passed to the custom table in the DnD actions.</it>
 * </ul>
 * It also allows to register a listener of type ITableRowFilterListener that will be called on filter
 * selection changes.
 *
 * @author martin.melchior
 */
class OutputElementTableToolbar extends ULCBoxPane {

    private OutputElementTableModel tableModel
    private FilterFactory filterFactory
    private List<ITableRowFilterListener> filterListeners = []

    ULCBoxPane filterArea
    ULCBoxPane keyfigureSelection

    OutputElementTableToolbar(OutputElementTableModel tableModel) {
        super(false, 2)

        // keep a reference to the table model TODO: change that...
        this.tableModel = tableModel

        // instantiate the filter factory
        this.filterFactory = new FilterFactory(tableModel)

        createView()
    }

    public void registerFilterListener(ITableRowFilterListener filterListener) {
        filterListeners << filterListener
    }

    /**
     * Gets an instance of a TableRowFilter with given type name, category (associated with the column)
     * to filter and value to filter for.
     * This filter is then set with all the listeners (such as the OutputElementTable).
     * @param filterType
     * @param categoryToFilter
     * @param value
     */
    private void updateFilter(String filterType, String categoryToFilter, Object value) {
        TableRowFilter filter = filterFactory.getFilter(filterType, value, categoryToFilter)
        filterListeners.each { listener ->
            listener.setFilter(filter)
        }
    }

    /**
     * Creates the view composed of teh filter area and the keyfigure selection area.
     */
    private void createView() {
        createFilterView()
        createKeyfigureSelectionView()
        this.add(ULCBoxPane.BOX_LEFT_CENTER, filterArea);
        this.add(ULCBoxPane.BOX_EXPAND_CENTER, ULCFiller.createHorizontalGlue())
        this.add(ULCBoxPane.BOX_LEFT_CENTER, keyfigureSelection)
    }

    /**
     * Create the keyfigure selection area - based on the KeyfigureSelectionModel
     * which is attached to the OutputElementTableModel
     */
    private void createKeyfigureSelectionView() {
        keyfigureSelection = new ULCBoxPane(false, 2)
        keyfigureSelection.setBorder(BorderFactory.createTitledBorder("Selection Statistics & Period"))

        // initialize the components to be added
        KeyfigureSelectionModel model = tableModel.keyFigureSelectionModel

        ULCComboBox keyfigureSelector = new ULCComboBox(model.getKeyfigureModel())
        ULCLabel parameterLabel = new ULCLabel("Parameter: ")
        ULCTextField keyfigureParameterSelector = new ULCTextField(8)
        keyfigureParameterSelector.setEditable(false)
        keyfigureParameterSelector.setBackground(Color.lightGray)
        ULCLabel periodLabel = new ULCLabel("Period: ")
        ULCComboBox periodSelector = new ULCComboBox(model.getPeriodSelectionModel())

        // layout them
        keyfigureSelection.add(ULCBoxPane.BOX_LEFT_CENTER, keyfigureSelector)
        keyfigureSelection.add(ULCBoxPane.BOX_LEFT_CENTER, ULCFiller.createHorizontalGlue())
        keyfigureSelection.add(ULCBoxPane.BOX_LEFT_CENTER, parameterLabel)
        keyfigureSelection.add(ULCBoxPane.BOX_LEFT_CENTER, keyfigureParameterSelector)
        keyfigureSelection.add(ULCBoxPane.BOX_LEFT_CENTER, ULCFiller.createHorizontalGlue())
        keyfigureSelection.add(ULCBoxPane.BOX_LEFT_CENTER, periodLabel)
        keyfigureSelection.add(ULCBoxPane.BOX_LEFT_CENTER, periodSelector)

        // attach listeners to get the suitable behavior on actions
        keyfigureSelector.addActionListener(
                new IActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        StatisticsKeyfigure enumValue = StatisticsKeyfigure.getEnumValue( (String) keyfigureSelector.selectedItem)
                        if (enumValue.needsParameters()) {
                            keyfigureParameterSelector.setEditable(true)
                            keyfigureParameterSelector.setBackground(Color.white)
                        } else {
                            keyfigureParameterSelector.setEditable(false)
                            keyfigureParameterSelector.setBackground(Color.lightGray)
                        }
                    }
                }
        )
        keyfigureParameterSelector.addKeyListener(new IKeyListener() {
            void keyTyped(KeyEvent keyEvent) {
                String value = keyfigureParameterSelector.text
                StatisticsKeyfigure enumValue = StatisticsKeyfigure.getEnumValue( (String) keyfigureSelector.selectedItem)
                if (enumValue.equals(StatisticsKeyfigure.ITERATION)) {
                    model.keyfigureParameter = Integer.parseInt(value)
                } else {
                    model.keyfigureParameter = Double.parseDouble(value)
                }

            }
        })
    }

    private void createFilterView() {
        // initialize the components
        filterArea = new ULCBoxPane(false);
        filterArea.setBorder(BorderFactory.createTitledBorder("Filtering"))
        final ULCComboBox filterType = new ULCComboBox(FilterFactory.FILTERNAMES);
        ULCLabel categoryLabel = new ULCLabel("Category: ")
        ULCComboBox categoryToFilter = new ULCComboBox(tableModel.categories);
        categoryToFilter.setEnabled(false)
        ClientContext.setModelUpdateMode(categoryToFilter.getModel(), UlcEventConstants.SYNCHRONOUS_MODE)
        ULCLabel valueLabel = new ULCLabel("Value: ")
        final ULCTextField filterValue = new ULCTextField(10);
        filterValue.setEditable(false);
        ULCButton clear = new ULCButton(UIUtils.getIcon("delete-active.png"));
        clear.setPreferredSize(new Dimension(16, 16));
        clear.setContentAreaFilled(false);
        clear.setOpaque(false);

        // layout them
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Filter: "));
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, filterType);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, categoryLabel);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, categoryToFilter);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, valueLabel);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, filterValue);
        filterArea.add(ULCBoxPane.BOX_LEFT_CENTER, clear);

        // Attach the listeners
        // TODO: Validation of what has been entered
        filterType.addActionListener(
                new IActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        String filterName = (String) filterType.getSelectedItem();
                        if (filterName.equalsIgnoreCase(FilterFactory.NONE)) {
                            filterValue.setEditable(false);
                            categoryToFilter.setEnabled(false)
                            tableModel.isTemplateMode = false
                        } else if (filterName.equalsIgnoreCase(FilterFactory.TEMPLATES)) {
                            filterValue.setEditable(false);
                            categoryToFilter.setEnabled(false)
                            tableModel.isTemplateMode = true
                        } else if (filterName.equalsIgnoreCase(FilterFactory.EMPTY)) {
                            filterValue.setEditable(false);
                            categoryToFilter.setEnabled(true)
                            tableModel.isTemplateMode = false
                        } else {
                            filterValue.setEditable(true);
                            categoryToFilter.setEnabled(true)
                            tableModel.isTemplateMode = false
                        }
                        updateFilter((String) filterType.getSelectedItem(), (String) categoryToFilter.getSelectedItem(), filterValue.getText())
                    }
                }
        );
        clear.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                filterValue.setText("");
                filterType.setSelectedItem(FilterFactory.NONE);
                updateFilter((String) filterType.getSelectedItem(), (String) categoryToFilter.getSelectedItem(), filterValue.getText())
            }
        });
        IActionListener action = new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateFilter((String) filterType.getSelectedItem(), (String) categoryToFilter.getSelectedItem(), filterValue.getText())
            }
        };
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        filterValue.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
    }
}
