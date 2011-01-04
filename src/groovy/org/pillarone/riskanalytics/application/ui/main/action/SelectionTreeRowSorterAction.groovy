package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import static org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeRowSorterAction extends ResourceBasedAction {

    ITableTreeModel model
    int column
    boolean ascOrder

    public SelectionTreeRowSorterAction(ITableTreeModel model, boolean asc, int column) {
        super(asc ? "AscRowSorterAction" : "DescRowSorterAction")
        this.model = model
        this.ascOrder = asc
        this.column = column
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        model.builder.order(getComparator())
    }

    private Comparator getComparator() {
        switch (column) {
            case STATE: return { x, y -> ascOrder ? x?.item?.status?.getDisplayName() <=> y?.item?.status?.getDisplayName() : y?.item?.status?.getDisplayName() <=> x?.item?.status?.getDisplayName() } as Comparator
            case OWNER: return { x, y -> ascOrder ? x?.item?.getCreator()?.username <=> y?.item?.getCreator()?.username : y?.item?.getCreator()?.username <=> x?.item?.getCreator()?.username } as Comparator
            case LAST_UPDATER: return { x, y -> ascOrder ? x?.item?.getLastUpdater()?.username <=> y?.item?.getLastUpdater()?.username : y?.item?.getLastUpdater()?.username <=> x?.item?.getLastUpdater()?.username } as Comparator
            case CREATION_DATE: return { x, y -> ascOrder ? x?.item?.getCreationDate() <=> y?.item?.getCreationDate() : y?.item?.getCreationDate() <=> x?.item?.getCreationDate() } as Comparator
            case LAST_MODIFICATION_DATE: return { x, y -> ascOrder ? x?.item?.getModificationDate() <=> y?.item?.getModificationDate() : y?.item?.getModificationDate() <=> x?.item?.getModificationDate() } as Comparator
        }
    }

}
