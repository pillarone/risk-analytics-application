package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import org.pillarone.riskanalytics.application.ui.result.action.AddIterationToResultViewAction
import org.pillarone.riskanalytics.application.ui.result.action.ShowIterationSingleValueAction
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.IParametrizedKeyFigureValueProvider
import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class IterationResultTableRenderer extends DefaultTableCellRenderer {
    ResultIterationDataViewModel model
    ULCNumberDataType numberDataType

    public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean selected, boolean hasFocus, int column) {
        IRendererComponent component = super.getTableCellRendererComponent(table, value, selected, hasFocus, column)
        ULCPopupMenu menu = new ULCPopupMenu()
        menu.add(new ULCMenuItem(new AddIterationToResultViewAction(new IterationTableValueProvider(table), model.resultView.model, model.resultView.tree.viewPortTableTree)))
        if (model.isSingle() && (value instanceof Integer))
            menu.add(new ULCMenuItem(new ShowIterationSingleValueAction(model, (int) value)))
        component.setComponentPopupMenu(menu)
        setDataType getLocalNumberDataType()
        setHorizontalAlignment(ULCLabel.CENTER)
        return component
    }

    protected ULCNumberDataType getLocalNumberDataType() {
        if (!numberDataType) {
            numberDataType = DataTypeFactory.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setClassType Integer
        }
        return numberDataType
    }


}

class IterationTableValueProvider implements IParametrizedKeyFigureValueProvider<List<Integer>> {

    private ULCTable iterationTable

    IterationTableValueProvider(ULCTable iterationTable) {
        this.iterationTable = iterationTable
    }

    List<Integer> getValue() {

        List<Integer> result = []

        iterationTable.selectedRows.each {
            result << iterationTable.model.getValueAt(it, 0)
        }

        return result
    }


}
