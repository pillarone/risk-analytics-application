package org.pillarone.riskanalytics.application.ui.parameterization.view

import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalTable
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCFiller
import org.pillarone.riskanalytics.core.parameterization.PeriodMatrixMultiDimensionalParameter
import com.ulcjava.base.application.ULCCheckBoxMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.Component
import com.ulcjava.base.application.event.TableModelEvent
import com.ulcjava.base.application.event.ITableModelListener
import com.ulcjava.base.application.table.ULCTableColumn
import org.pillarone.riskanalytics.application.ui.table.view.MultiDimensionalParameterTableCellRenderer
import org.pillarone.riskanalytics.application.ui.base.action.TableCopier
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.KeyEvent
import org.pillarone.riskanalytics.application.ui.base.action.TablePaster
import org.pillarone.riskanalytics.application.ui.base.action.TableSelectionFiller
import org.pillarone.riskanalytics.core.components.ComponentUtils

class PeriodMultiDimensionalParameterView extends AbstractMultiDimensionalParameterView {
    static Log LOG = LogFactory.getLog(PeriodMultiDimensionalParameterView)
    ULCButton applyButton, selectComponentButton
    ULCTextField periodTextField
    ULCLabel path
    ULCPopupMenu componentPopupMenu
    private Map<String, String> components

    PeriodMultiDimensionalParameterView(MultiDimensionalParameterModel model) {
        super(model)
    }

    @Override
    protected void attachListeners() {
        model.tableModel.addTableModelListener([tableChanged: {TableModelEvent event ->
            if (event.firstRow == -1 || event.column == -1) {
                setRendererAndEditors()
            }
        }
        ] as ITableModelListener)
        table.registerKeyboardAction(new TableCopier(table: table), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false), 0)
        table.registerKeyboardAction(new TablePaster(table: table, model: table.model), KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, false), 0)
        table.registerKeyboardAction(new TableSelectionFiller(table: table, model: table.model), KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK, false), 0)
    }

    @Override
    protected void initComponents() {
        path = new ULCLabel(model.getPathAsString())
        content = new ULCBoxPane(1, 0)
        this.table = new MultiDimensionalTable(this, model.getTableModel())
        table.setName("multiDimTable")
        setRendererAndEditors()
        //set table header height
        this.table.getTableHeader().setPreferredSize(new Dimension(80, 10))
        this.table.cellSelectionEnabled = true
    }


    private ULCBoxPane getSelectionPane() {
        ULCBoxPane bp = new ULCBoxPane(0, 1, 5, 5)
        bp.add(new ULCFiller(10, 10))
        //Create selectButton - bit of a hack..
        selectComponentButton = new ULCButton(getText('parameters'))
        selectComponentButton.setToolTipText(getText('parametersTooltip'))
        selectComponentButton.setName("selectComponents")
        //Create the popupmenu
        componentPopupMenu = new ULCPopupMenu()
        componentPopupMenu.setName("componentPopupMenu")
        //get the selected components from the model
        Set<String> selectedFromModel = getSelectedComponentsFromModel()
        components = getPossibleComponents(model.multiDimensionalParameter)
        components.each {String key, String value ->
            boolean isSelected = selectedFromModel.contains(key)
            ULCCheckBoxMenuItem item = new ULCCheckBoxMenuItem(key, isSelected)
            item.addValueChangedListener(new IValueChangedListener() {
                @Override
                void valueChanged(ValueChangedEvent valueChangedEvent) {
                    //just set the applybutton enabled...
                    applyButton.setEnabled(true)
                }
            })
            componentPopupMenu.add(item)
        }
        selectComponentButton.setComponentPopupMenu(componentPopupMenu)
        selectComponentButton.addActionListener(new IActionListener() {
            @Override
            void actionPerformed(ActionEvent actionEvent) {
                selectComponentButton.getComponentPopupMenu().setVisible(true)
            }
        })
        bp.add(ULCBoxPane.BOX_LEFT_BOTTOM, selectComponentButton)
        bp.add(new ULCFiller(10, 10))

        //Period selection textfield
        int periods = getSelectedNumberPeriodsFromModel()
        periodTextField = new ULCTextField(3)
        periodTextField.setToolTipText(getText('periodTooltip'))
        periodTextField.name = 'periodTextField'
        periodTextField.setValue(periods.toInteger())
        bp.add(ULCBoxPane.BOX_LEFT_BOTTOM, periodTextField)
        bp.add(new ULCFiller(20, 10))

        //add the applybutton
        applyButton = new ULCButton(getText('apply'))
        applyButton.name = 'applyButton'
        applyButton.addActionListener(new IActionListener() {
            @Override
            void actionPerformed(ActionEvent actionEvent) {
                int period
                try {
                    period = periodTextField.getText().toInteger()
                } catch (Exception e) {
                    LOG.error("Invalid period..", e)
                    return
                }
                List<String> components = getSelectedComponents()
                applyStuff(components, period)
            }
        })
        bp.add(ULCBoxPane.BOX_EXPAND_BOTTOM, applyButton)
        return bp
    }

    private List<String> getSelectedComponents() {
        List<String> selected = []
        componentPopupMenu.getComponents().each {ULCCheckBoxMenuItem item ->
            if (item.isSelected()) {
                selected << components.get(item.getText())
            }
        }
        return selected
    }


    @Override
    protected void layoutComponents() {
        ULCBoxPane controlPane = getSelectionPane()
        content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller(10, 10))
        content.add(ULCBoxPane.BOX_EXPAND_TOP, path)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller(10, 10))
        content.add(ULCBoxPane.BOX_LEFT_CENTER, controlPane)
        table.setAutoResizeMode(ULCTable.AUTO_RESIZE_OFF)
        //Add the table...
        ULCScrollPane scrollPane = new ULCScrollPane(table)
        scrollPane.setHorizontalScrollBarPolicy(ULCScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        scrollPane.setVerticalScrollBarPolicy(ULCScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane)
    }


    @Override
    boolean isMatrix() {
        return model.matrix  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void updateCount(boolean isRow, int x) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Set<String> getSelectedComponentsFromModel() {
        Set<String> result = new HashSet<String>()
        PeriodMatrixMultiDimensionalParameter pmmdp = (PeriodMatrixMultiDimensionalParameter) model.multiDimensionalParameter
        List titles = pmmdp.getTitles()
        if (titles.get(0) instanceof List) {
            titles.get(0).each {String title ->
                result.add(title)
            }
        }
        return result
    }


    private int getSelectedNumberPeriodsFromModel() {
        PeriodMatrixMultiDimensionalParameter pmmdp = (PeriodMatrixMultiDimensionalParameter) model.multiDimensionalParameter
        return pmmdp.getMaxPeriod()
    }

    private void applyStuff(List<String> selectedComps, int periods) {
        PeriodMatrixMultiDimensionalParameter pmmdp = (PeriodMatrixMultiDimensionalParameter) model.multiDimensionalParameter
        pmmdp.updateTable(periods, selectedComps)
        model.tableModel.fireTableStructureChanged()
    }


    private Map<String, String> getPossibleComponents(PeriodMatrixMultiDimensionalParameter param) {
        Map<String, String> res = new TreeMap<String, String>()
        param.getSimulationModel().getMarkedComponents(param.getMarkerClass()).sort().each {Component c ->
//            res << c.getName()
            res.put(ComponentUtils.getNormalizedName(c.name), c.name);
        }
        return res
    }
}