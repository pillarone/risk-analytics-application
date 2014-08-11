package org.pillarone.riskanalytics.application.ui.upload.queue.model

import com.ulcjava.base.application.table.AbstractTableModel
import grails.util.Holders
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.AbstractTableRowModel
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.IResourceBundleResolver
import org.pillarone.riskanalytics.core.upload.UploadRuntimeInfo
import org.pillarone.riskanalytics.core.upload.UploadState

import static org.pillarone.riskanalytics.application.UserContext.hasCurrentUser

class UploadRowModel extends AbstractTableRowModel<UploadRuntimeInfo> {

    static final Map<Integer, String> COLUMN_NAME_KEYS = hasCurrentUser() ? [
            0: 'simulation',
            1: 'batch',
            2: 'p14n',
            3: 'template',
            4: 'iterations',
            5: 'priority',
            6: 'configuredAt',
            7: 'configuredBy',
            8: 'uploadState',
    ] : [
            0: 'simulation',
            1: 'batch',
            2: 'p14n',
            3: 'template',
            4: 'iterations',
            5: 'priority',
            6: 'configuredAt',
            7: 'uploadState',
    ] as Map<Integer, String>

    private static final Map<Integer, Closure<String>> COLUMN_VALUE_FACTORIES = hasCurrentUser() ? [
            0: { UploadRuntimeInfo info -> info.simulation?.nameAndVersion },
            1: { UploadRuntimeInfo info -> info.simulation?.batch?.name },
            2: { UploadRuntimeInfo info -> info.parameterization?.nameAndVersion },
            3: { UploadRuntimeInfo info -> info.resultConfiguration?.nameAndVersion },
            4: { UploadRuntimeInfo info -> info.iterations?.toString() },
            5: { UploadRuntimeInfo info -> info.priority?.toString() },
            6: { UploadRuntimeInfo info -> info.configuredAt ? DateFormatUtils.getDateFormat("yyyy.MM.dd HH:mm:ss").print(new DateTime(info.configuredAt.time)) : '' },
            7: { UploadRuntimeInfo info -> info.offeredBy?.username },
            8: { UploadRuntimeInfo info -> getDisplayText(info.uploadState) },
    ] : [
            0: { UploadRuntimeInfo info -> info.simulation?.nameAndVersion },
            1: { UploadRuntimeInfo info -> info.simulation?.batch?.name },
            2: { UploadRuntimeInfo info -> info.parameterization?.nameAndVersion },
            3: { UploadRuntimeInfo info -> info.resultConfiguration?.nameAndVersion },
            4: { UploadRuntimeInfo info -> info.iterations?.toString() },
            5: { UploadRuntimeInfo info -> info.priority?.toString() },
            6: { UploadRuntimeInfo info -> info.configuredAt ? DateFormatUtils.getDateFormat("yyyy.MM.dd HH:mm:ss").print(new DateTime(info.configuredAt.time)) : '' },
            7: { UploadRuntimeInfo info -> getDisplayText(info.uploadState) },
    ]

    private static IResourceBundleResolver getResolver() {
        Holders.grailsApplication.mainContext.getBean('resourceBundleResolver')
    }

    private static String getDisplayText(UploadState uploadState) {
//        resolver.getText(UploadRowModel, uploadState.toString())
        'TODO'
    }

    static final int COLUMN_COUNT = hasCurrentUser() ? 9 : 8

    UploadRowModel(int row, AbstractTableModel tableModel, UploadRuntimeInfo info) {
        super(row, tableModel, info, COLUMN_COUNT)
    }

    @Override
    Closure<String> getValueFactory(int index) {
        COLUMN_VALUE_FACTORIES[index]
    }
}
