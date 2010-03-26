package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.util.ULCIcon
import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterTableModel
import org.pillarone.riskanalytics.application.ui.util.NumberParser
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources

class TablePasterTests extends GroovyTestCase {

    void testPasteContent() {

        def model = new MockFor(MultiDimensionalParameterTableModel)
        model.demand.startBulkChange {}
        demandEditable(model, 0, 0, true)
        demandSetValueAt(model, 0, 0, 1)

        demandEditable(model, 0, 1, true)
        demandSetValueAt(model, 0, 1, 2)

        demandEditable(model, 1, 0, true)
        demandSetValueAt(model, 1, 0, 3)

        demandEditable(model, 1, 1, true)
        demandSetValueAt(model, 1, 1, 4)

        StubFor uiUltils = new StubFor(UIUtils)
        uiUltils.demand.getNumberParser {-> new NumberParser(Locale.defaultLocale)}
        uiUltils.demand.getIcon {String s -> new ULCIcon()}
        model.demand.stopBulkChange {}

        uiUltils.use {
            model.use {
                def treeModel = new MultiDimensionalParameterTableModel()
                new TablePaster(model: treeModel, columnCount: 2, rowCount: 2).pasteContent("1\t2\n3\t4", 0, 0)
            }
        }
    }

    void testPasteTooManyRows() {
        def model = new MockFor(MultiDimensionalParameterTableModel)
        model.demand.startBulkChange {}
        demandEditable(model, 0, 0, true)
        demandSetValueAt(model, 0, 0, 1)

        demandEditable(model, 0, 1, true)
        demandSetValueAt(model, 0, 1, 2)

        demandEditable(model, 1, 0, true)
        demandSetValueAt(model, 1, 0, 3)

        demandEditable(model, 1, 1, true)
        demandSetValueAt(model, 1, 1, 4)

        StubFor uiUltils = new StubFor(UIUtils)
        uiUltils.demand.getNumberParser {-> new NumberParser(Locale.defaultLocale)}
        uiUltils.demand.getIcon {String s -> new ULCIcon()}
        model.demand.stopBulkChange {}

        uiUltils.use {
            model.use {
                def treeModel = new MultiDimensionalParameterTableModel()
                new TablePaster(model: treeModel, columnCount: 2, rowCount: 1).pasteContent("1\t2\n3\t4", 0, 0)
            }
        }
    }

    void testPasteTooManyColumns() {
        LocaleResources.setTestMode()
        def model = new MockFor(MultiDimensionalParameterTableModel)
        model.demand.startBulkChange {}

        StubFor tablePaster = new StubFor(TablePaster)
        tablePaster.demand.showAlert {-> return null}
        StubFor uiUltils = new StubFor(UIUtils)
        uiUltils.demand.getNumberParser {-> new NumberParser(Locale.defaultLocale)}
        uiUltils.demand.getIcon {String s -> new ULCIcon()}
        model.demand.stopBulkChange {}

        uiUltils.use {
            model.use {
                def treeModel = new MultiDimensionalParameterTableModel()
                new TablePaster(model: treeModel, columnCount: 1, rowCount: 2).pasteContent("1\t2\n3\t4", 0, 0)
            }
        }
        LocaleResources.clearTestMode()
    }

    private void demandEditable(MockFor mock, int expectedRow, int expectedColumn, boolean value) {
        mock.demand.isCellEditable(1..1) {int actualRow, int actualColumn ->
            assertEquals expectedRow, actualRow
            assertEquals expectedColumn, actualColumn

            value
        }
    }

    private void demandSetValueAt(MockFor mock, int expectedRow, int expectedColumn, def expectedValue) {
        mock.demand.setValueAt(1..1) {Object actualValue, int actualRow, int actualColumn ->
            assertEquals expectedRow, actualRow
            assertEquals expectedColumn, actualColumn
            assertEquals expectedValue, actualValue
        }
    }

}