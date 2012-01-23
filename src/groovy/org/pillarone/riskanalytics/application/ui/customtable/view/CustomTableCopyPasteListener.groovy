package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Clipboard
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import javax.activation.MimeType
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper

/**
 * Class which handles the copy/paste/cut operations
 *
 * @author ivo.nussbaumer
 */
public class CustomTableCopyPasteListener implements IActionListener {

    /**
     * Class which contains the data for a copy-operation
     */
    public class CopyCellData {
        public int origin_row
        public int origin_col
        public Object data

        public CopyCellData(int origin_row, int origin_col, Object data) {
            this.origin_row = origin_row
            this.origin_col = origin_col
            this.data = data
        }
    }

    public enum Mode {
        COPY,
        CUT,
        PASTE
    }

    private Mode mode

    public CustomTableCopyPasteListener(Mode mode) {
        this.mode = mode
    }

    void actionPerformed(ActionEvent actionEvent) {
        CustomTable customTable
        if (actionEvent.source instanceof CustomTable)
            customTable = actionEvent.source

        switch (mode) {
            case Mode.COPY:
            case Mode.CUT:
                customTable.copyData.clear()
                int min_row = customTable.getSelectionModel().getMinSelectionIndex()
                int max_row = customTable.getSelectionModel().getMaxSelectionIndex()
                int min_col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex()
                int max_col = customTable.getColumnModel().getSelectionModel().getMaxSelectionIndex()

                StringBuilder excelCopyData = new StringBuilder()
                for (int row = min_row; row <= max_row; row++) {
                    for (int col = min_col; col <= max_col; col++) {
                        // add data to the internal clipboard
                        Object data = customTable.customTableModel.getDataAt(row, col)
                        if (data == null)
                            data = ""
                        customTable.copyData.add(new CopyCellData(row, col, data))

                        // build the string, which will be added to the Windows clipboard
                        excelCopyData.append (customTable.customTableModel.getValueAt(row, col) )
                        excelCopyData.append ("\t")

                        // cut
                        if (mode == Mode.CUT) {
                            customTable.customTableModel.setValueAt("", row, col)
                        }
                    }
                    // remove last TAB and add newline
                    excelCopyData.deleteCharAt (excelCopyData.length()-1)
                    excelCopyData.append ("\r\n")
                }
                // add data to the Windows clipboard
                StringSelection ss = new StringSelection(excelCopyData.toString())
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard()
                clip.setContents(ss, ss)
                break

            case Mode.PASTE:
                // Check if there is data in the windows clipboard to paste
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard()
                for (DataFlavor df : clip.getAvailableDataFlavors()) {
                    MimeType mime = new MimeType (df.mimeType)
                    if (mime.baseType == "text/plain" && clip.getData (df) instanceof String) {
                        int row        = customTable.getSelectionModel().getMinSelectionIndex()
                        int origin_col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex()
                        String data = clip.getData (df)

                        int col = origin_col
                        for (String row_data : data.split ("\n")) {
                            for (String col_data : row_data.split ("\t")) {
                                customTable.customTableModel.setValueAt(col_data, row, col++)
                            }
                            col = origin_col
                            row++
                        }
                        return
                    }
                }

                // copy more than one value
                if (customTable.copyData.size() > 1) {
                    int row = customTable.getSelectionModel().getMinSelectionIndex()
                    int col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex()

                    int last_origin_row = null
                    int last_origin_col = null

                    for (CopyCellData copyCellData: customTable.copyData) {
                        if (last_origin_row != null && last_origin_col != null) {
                            row += copyCellData.origin_row - last_origin_row
                            col += copyCellData.origin_col - last_origin_col
                        }

                        // update the variables
                        Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col)

                        if ((data instanceof String) == false) {
                            ((DataCellElement)data).update(customTable.customTableModel)
                        }
                        customTable.customTableModel.setValueAt(data, row, col)

                        last_origin_row = copyCellData.origin_row
                        last_origin_col = copyCellData.origin_col
                    }
                    customTable.selectionModel.setSelectionInterval(row, row)
                    customTable.getColumnModel().selectionModel.setSelectionInterval(col, col)

                // just one cell to copy --> insert the cell, in the whole selection
                } else if (customTable.copyData.size() == 1) {
                    int min_row = customTable.getSelectionModel().getMinSelectionIndex()
                    int max_row = customTable.getSelectionModel().getMaxSelectionIndex()
                    int min_col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex()
                    int max_col = customTable.getColumnModel().getSelectionModel().getMaxSelectionIndex()

                    CopyCellData copyCellData = customTable.copyData[0]

                    for (int row = min_row; row <= max_row; row++) {
                        for (int col = min_col; col <= max_col; col++) {
                            Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col)
                            if ((data instanceof String) == false) {
                                ((DataCellElement)data).update(customTable.customTableModel)
                            }
                            customTable.customTableModel.setValueAt(data, row, col)
                        }
                    }

                    customTable.selectionModel.setSelectionInterval(min_row, max_row)
                    customTable.getColumnModel().selectionModel.setSelectionInterval(min_col, max_col)
                }
                break
        }
    }
}
