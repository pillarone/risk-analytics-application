package org.pillarone.riskanalytics.application.ui.customtable.view;

import com.ulcjava.base.application.event.IActionListener;
import com.ulcjava.base.application.event.ActionEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement;
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableHelper;

/**
 * Class which handles the copy/paste/cut operations
 *
 * @author ivo.nussbaumer
 */
public class CustomTableCopyPasteListener implements IActionListener {

    private static final Log LOG = LogFactory.getLog(CustomTableCopyPasteListener.class);

    /**
     * Class which contains the data for a copy-operation
     */
    public class CopyCellData {
        public int origin_row;
        public int origin_col;
        public Object data;

        public CopyCellData(int origin_row, int origin_col, Object data) {
            this.origin_row = origin_row;
            this.origin_col = origin_col;
            this.data = data;
        }
    }

    public enum Mode {
        COPY,
        CUT,
        PASTE
    }

    private Mode mode;

    public CustomTableCopyPasteListener(Mode mode) {
        this.mode = mode;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof CustomTable) {
            CustomTable customTable = (CustomTable)actionEvent.getSource();

            switch (mode) {
                case COPY:
                case CUT:
                    customTable.getCopyData().clear();
                    int min_row = customTable.getSelectionModel().getMinSelectionIndex();
                    int max_row = customTable.getSelectionModel().getMaxSelectionIndex();
                    int min_col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex();
                    int max_col = customTable.getColumnModel().getSelectionModel().getMaxSelectionIndex();

                    StringBuilder excelCopyData = new StringBuilder();
                    for (int row = min_row; row <= max_row; row++) {
                        for (int col = min_col; col <= max_col; col++) {
                            // add data to the internal clipboard
                            Object data = customTable.getCustomTableModel().getDataAt(row, col);
                            if (data == null)
                                data = "";
                            customTable.getCopyData().add(new CopyCellData(row, col, data));

                            // build the string, which will be added to the Windows clipboard
                            excelCopyData.append (customTable.getCustomTableModel().getValueAt(row, col) );
                            excelCopyData.append ("\t");

                            // cut
                            if (mode == Mode.CUT) {
                                customTable.getCustomTableModel().setValueAt("", row, col);
                            }
                        }
                        // remove last TAB and add newline
                        excelCopyData.deleteCharAt (excelCopyData.length()-1);
                        excelCopyData.append ("\r\n");
                    }
                    // add data to the Windows clipboard
                    StringSelection ss = new StringSelection(excelCopyData.toString());
                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clip.setContents(ss, ss);
                    break;

                case PASTE:
                    // Check if there is data in the windows clipboard to paste
                    clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    for (DataFlavor df : clip.getAvailableDataFlavors()) {
                        try {
                            MimeType mime = new MimeType (df.getMimeType());
                            if (mime.getBaseType().equals("text/plain") && clip.getData (df) instanceof String) {
                                int row        = customTable.getSelectionModel().getMinSelectionIndex();
                                int origin_col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex();
                                String data = clip.getData (df).toString();

                                int col = origin_col;
                                for (String row_data : data.split ("\n")) {
                                    for (String col_data : row_data.split ("\t")) {
                                        customTable.getCustomTableModel().setValueAt(col_data, row, col++);
                                    }
                                    col = origin_col;
                                    row++;
                                }
                                return;
                            }
                        } catch (MimeTypeParseException e) {
                            LOG.error("Data flavor error", e);
                        } catch (UnsupportedFlavorException e) {
                            LOG.error("Data flavor error", e);
                        } catch (IOException e) {
                            LOG.error("Data flavor error", e);
                        }
                    }

                    // copy more than one value
                    if (customTable.getCopyData().size() > 1) {
                        int row = customTable.getSelectionModel().getMinSelectionIndex();
                        int col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex();

                        Integer last_origin_row = null;
                        Integer last_origin_col = null;

                        for (CopyCellData copyCellData: customTable.getCopyData()) {
                            if (last_origin_row != null && last_origin_col != null) {
                                row += copyCellData.origin_row - last_origin_row;
                                col += copyCellData.origin_col - last_origin_col;
                            }

                            // update the variables
                            Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col);

                            if (!(data instanceof String)) {
                                ((DataCellElement)data).update(customTable.getCustomTableModel());
                            }
                            customTable.getCustomTableModel().setValueAt(data, row, col);

                            last_origin_row = copyCellData.origin_row;
                            last_origin_col = copyCellData.origin_col;
                        }
                        customTable.getSelectionModel().setSelectionInterval(row, row);
                        customTable.getColumnModel().getSelectionModel().setSelectionInterval(col, col);

                    // just one cell to copy --> insert the cell, in the whole selection
                    } else if (customTable.getCopyData().size() == 1) {
                        min_row = customTable.getSelectionModel().getMinSelectionIndex();
                        max_row = customTable.getSelectionModel().getMaxSelectionIndex();
                        min_col = customTable.getColumnModel().getSelectionModel().getMinSelectionIndex();
                        max_col = customTable.getColumnModel().getSelectionModel().getMaxSelectionIndex();

                        CopyCellData copyCellData = customTable.getCopyData().get(0);

                        for (int row = min_row; row <= max_row; row++) {
                            for (int col = min_col; col <= max_col; col++) {
                                Object data = CustomTableHelper.copyData(copyCellData.data, row - copyCellData.origin_row, col - copyCellData.origin_col);
                                if (!(data instanceof String)) {
                                    ((DataCellElement)data).update(customTable.getCustomTableModel());
                                }
                                customTable.getCustomTableModel().setValueAt(data, row, col);
                            }
                        }

                        customTable.getSelectionModel().setSelectionInterval(min_row, max_row);
                        customTable.getColumnModel().getSelectionModel().setSelectionInterval(min_col, max_col);
                    }
                    break;
            }
        }
    }
}
