package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import com.ulcjava.base.application.*

class PostSimulationCalculationPane {

    ULCBoxPane content

    PostSimulationCalculationPaneModel model
    private ULCCheckBox standardDeviation
    private ListSelection percentileLoss, percentileProfit, varLoss, varProfit, tvarLoss, tvarProfit, pdf

    PostSimulationCalculationPane(PostSimulationCalculationPaneModel model) {
        this.model = model

        initComponents()
        attachListeners()
    }

    protected void initComponents() {
        content = new ULCBoxPane(2, 0)

        content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel("Standard deviation"))
        standardDeviation = new ULCCheckBox("", model.standardDeviation)
        standardDeviation.name = "standardDeviation"
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, standardDeviation)

        percentileLoss = createListSelectionPane("Loss percentile", "lossPercentile", model.percentileLoss)
        percentileProfit = createListSelectionPane("Profit percentile", "profitPercentile", model.percentileProfit)
        varLoss = createListSelectionPane("Loss VaR", "lossVaR", model.varLoss)
        varProfit = createListSelectionPane("Profit VaR", "profitVaR", model.varProfit)
        tvarLoss = createListSelectionPane("Loss TVaR", "lossTVaR", model.tvarLoss)
        tvarProfit = createListSelectionPane("Profit TVaR", "profitTVaR", model.tvarProfit)
        pdf = createListSelectionPane("PDF", "pdf", model.pdf)

    }

    private ListSelection createListSelectionPane(String title, String name, List<Double> values) {
        ListSelection listSelection = new ListSelection(values, name)
        content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(title))
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, listSelection.content)
        return listSelection
    }

    protected void attachListeners() {
        standardDeviation.addValueChangedListener(new IValueChangedListener() {
            @Override
            void valueChanged(ValueChangedEvent valueChangedEvent) {
                model.standardDeviation = standardDeviation.selected
            }
        })
        attachListSelectionListener(percentileLoss, model.percentileLoss)
        attachListSelectionListener(percentileProfit, model.percentileProfit)
        attachListSelectionListener(varLoss, model.varLoss)
        attachListSelectionListener(varProfit, model.varProfit)
        attachListSelectionListener(tvarLoss, model.tvarLoss)
        attachListSelectionListener(tvarProfit, model.tvarProfit)
        attachListSelectionListener(pdf, model.pdf)
    }

    private void attachListSelectionListener(ListSelection selection, List<Double> values) {
        selection.addListener(new IListSelectionChangeListener() {
            @Override
            void selectionChanged(List<Double> newSelection) {
                values.clear()
                values.addAll(newSelection)
            }
        })
    }

    private interface IListSelectionChangeListener {
        void selectionChanged(List<Double> newSelection)
    }

    private static class ListSelection {

        ULCBoxPane content
        String name

        private ULCList list
        private DefaultListModel listModel
        private ULCButton addButton
        private ULCTextField valueField
        private ULCButton removeButton

        private SortedSet<Double> values

        private List<IListSelectionChangeListener> listeners = []

        ListSelection(List<Double> values, String name) {
            this.name = name
            this.values = new TreeSet<Double>(values)
            initComponents()
        }

        private void initComponents() {
            content = new ULCBoxPane(3, 0)

            listModel = new DefaultListModel(values.toArray())
            list = new ULCList(listModel)
            list.visibleRowCount = 4
            list.name = name + "-list"
            addButton = new ULCButton("Add")
            addButton.name = name + "-add"
            addButton.addActionListener(new IActionListener() {
                @Override
                void actionPerformed(ActionEvent actionEvent) {
                    Double value = valueField.value
                    if (!listModel.contains(value)) {
                        values.add(value)
                        listModel.add(values.toList().indexOf(value), value)
                        fireValueChanged()
                    }
                }
            })
            valueField = new ULCTextField()
            valueField.name = name + "-value"
            valueField.dataType = DataTypeFactory.getDoubleDataType()
            valueField.preferredSize = new Dimension(60, 25)
            removeButton = new ULCButton("Remove")
            removeButton.name = name + "-remove"
            removeButton.addActionListener(new IActionListener() {
                @Override
                void actionPerformed(ActionEvent actionEvent) {
                    List<Integer> selectedIndices = list.getSelectedIndices().toList().sort().reverse()
                    for (int selectedIndex in selectedIndices) {
                        Double oldValue = listModel.getElementAt(selectedIndex)
                        listModel.remove(selectedIndex)
                        values.remove(oldValue)
                        fireValueChanged()
                    }
                }
            })

            content.add(ULCBoxPane.BOX_RIGHT_TOP, valueField)
            content.add(ULCBoxPane.BOX_LEFT_TOP, addButton)
            content.set(2, 0, 1, 2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(list))
            content.set(1, 1, 1, 1, ULCBoxPane.BOX_RIGHT_BOTTOM, removeButton)
        }

        protected List<Double> getAllSelectedValues() {
            List<Double> result = []

            for (int i = 0; i < listModel.size; i++) {
                result << listModel.getElementAt(i)
            }

            return result
        }

        protected fireValueChanged() {
            List<Double> values = getAllSelectedValues()
            for (IListSelectionChangeListener listener in listeners) {
                listener.selectionChanged(values)
            }
        }

        void addListener(IListSelectionChangeListener listener) {
            listeners << listener
        }

        void removeListener(IListSelectionChangeListener listener) {
            listeners.remove(listener)
        }
    }
}
