package org.pillarone.riskanalytics.application.ui.upload.queue.model
import com.ulcjava.base.application.table.AbstractTableModel
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class UploadQueueTableModel extends AbstractTableModel {

    protected final List<UploadRowModel> columnModels = []

    @Override
    int getRowCount() {
        columnModels.size()
    }

    @Override
    int getColumnCount() {
        UploadRowModel.COLUMN_COUNT
    }

    UploadRuntimeInfo getInfoAt(int index) {
        columnModels[index].object
    }

    @Override
    String getColumnName(int column) {
        getText(UploadRowModel.COLUMN_NAME_KEYS[column])
    }

    private String getText(String key) {
        UIUtils.getText(UploadQueueTableModel, key)
    }

    @Override
    String getValueAt(int row, int column) {
        columnModels[row].getValueAt(column)
    }

    void setInfos(List<UploadRuntimeInfo> infos) {
        this.columnModels.clear()
        infos.eachWithIndex { UploadRuntimeInfo info, int row ->
            this.columnModels << new UploadRowModel(row, this, info)
        }
        fireTableDataChanged()
    }

    void itemAdded(UploadRuntimeInfo item) {
        columnModels.add(0, new UploadRowModel(columnModels.size(), this, item))
        sortColumnModels()
        fireTableDataChanged()
    }

    protected void sortColumnModels() {
        columnModels.sort { it.object }
        assignRowsToColumnModels()
    }

    void itemRemoved(UploadRuntimeInfo info) {
        UploadRowModel columnModel = columnModels.find { UploadRowModel model -> model.object == info }
        if (columnModel) {
            int index = columnModels.indexOf(columnModel)
            columnModels.remove(columnModel)
            assignRowsToColumnModels()
            fireTableRowsDeleted(index, index)
        }
    }

    protected void assignRowsToColumnModels() {
        columnModels.eachWithIndex { UploadRowModel columnModel, int row ->
            columnModel.row = row
        }
    }

    void itemChanged(UploadRuntimeInfo info) {
        UploadRowModel columnModel = columnModels.find { UploadRowModel model -> model.object == info }
        if (columnModel) {
            columnModel.setObject(info)
        }
    }
}

