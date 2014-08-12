package org.pillarone.riskanalytics.application.ui.upload.finished.model

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.upload.queue.model.UploadQueueTableModel
import org.pillarone.riskanalytics.application.ui.upload.queue.model.UploadRowModel
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedUploadsTableModel extends UploadQueueTableModel {

    @Override
    protected void sortColumnModels() {}

    void removeAt(int[] selected) {
        List<UploadRowModel> toRemove = selected.collect {
            columnModels[it]
        }
        columnModels.removeAll(toRemove)
        assignRowsToColumnModels()
        List<Integer> selectedAsList = selected.toList()
        fireTableRowsDeleted(selectedAsList.min(), selectedAsList.max())
    }
}
