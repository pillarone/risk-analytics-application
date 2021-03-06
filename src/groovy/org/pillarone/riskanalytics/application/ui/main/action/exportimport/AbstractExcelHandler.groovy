package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Comment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.pillarone.riskanalytics.application.ui.parameterization.model.TreeBuilderUtil
import org.pillarone.riskanalytics.application.ui.util.I18NUtilities
import org.pillarone.riskanalytics.core.FileConstants
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.components.GlobalParameterComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.util.PropertiesUtils

abstract class AbstractExcelHandler {
    XSSFWorkbook workbook = new XSSFWorkbook()
    protected File excelFile
    protected String filename
    Model modelInstance
    protected static String COMPONENT_HEADER_NAME = 'Component Name'
    protected static String DISABLE_IMPORT = 'Disable Import'
    protected static String META_INFO_SHEET = 'Meta-Info'
    protected static String MODEL_INFO_KEY = 'Model'
    protected static String APPLICATION_VERSION_KEY = 'application-version'
    protected static final int DATA_ROW_START_INDEX = 2
    protected static final int TECHNICAL_HEADER_ROW_INDEX = 1
    protected static final int HEADER_ROW_INDEX = 0

    Model getModel() {
        try {
            return Thread.currentThread().contextClassLoader.loadClass(findModelName()).newInstance() as Model
        } catch (Exception ignored) {
            return null
        }
    }

    void loadWorkbook(InputStream is, String filename) {
        byte[] data = is.bytes
        this.filename = filename
        excelFile = File.createTempFile(filename, '', new File(FileConstants.TEMP_FILE_DIRECTORY))
        excelFile.bytes = data
        workbook = new XSSFWorkbook(new ByteArrayInputStream(data))
    }

    String findModelName() {
        XSSFSheet sheet = workbook.getSheet(META_INFO_SHEET)
        if (sheet) {
            Row modelNameRow = sheet.rowIterator().find { Row row ->
                row.getCell(0).stringCellValue == MODEL_INFO_KEY
            } as Row
            return modelNameRow?.getCell(1)?.stringCellValue
        }
        return null
    }

    Integer findParameterColumnIndex(Sheet sheet, String name, int columnStartIndex) {
        Integer columnIndex = null
        if (!name.startsWith(ComponentUtils.PARM)) {
            return null
        }
        Row row = sheet.getRow(TECHNICAL_HEADER_ROW_INDEX)
        for (int index = columnStartIndex; index < row.lastCellNum; index++) {
            Cell cell = row.getCell(index)
            if (cell.stringCellValue.startsWith('sub') && index > columnStartIndex) {
                break
            }
            if (cell.getStringCellValue().equals(name)) {
                return index
            }
        }
        return columnIndex
    }

    Integer findColumnIndex(Sheet sheet, String name, int columnStartIndex) {
        for (int i = HEADER_ROW_INDEX; i <= TECHNICAL_HEADER_ROW_INDEX; i++) {
            Row row = sheet.getRow(i)
            for (int index = columnStartIndex; index < row.lastCellNum; index++) {
                Cell cell = row.getCell(index)
                if (cell && cell.getStringCellValue().equals(name)) {
                    return index
                }
            }
        }
        return null
    }

    protected List getAllParms(Component component) {
        TreeBuilderUtil.collectProperties(component, ComponentUtils.PARM)
    }

    protected void addRow(XSSFSheet sheet, String key, String value, int rowIndex) {
        XSSFRow row = sheet.createRow(rowIndex)
        row.createCell(0).setCellValue(key)
        row.createCell(1).setCellValue(value)
    }

    protected Sheet findMdpSheet(Cell cell) {
        String mdpSheetName = getMDPSheetName(cell)
        assert mdpSheetName
        return workbook.getSheet(mdpSheetName)
    }

    protected String getMDPSheetName(Cell cell) {
        Row row = cell.sheet.getRow(HEADER_ROW_INDEX)
        Comment comment = row.getCell(cell.columnIndex).getCellComment()
        return comment.string.string
    }

    protected Sheet findSheetForComponent(Component component) {
        String sheetName = getSheetName(component)
        workbook.getSheet(sheetName)
    }

    static String getDisplayName(Component component, String name) {
        String displayName = I18NUtilities.findParameterDisplayName(component, name)
        return displayName ?: ComponentUtils.getNormalizedName(name)
    }

    static String getDisplayName(Class clazz, String name) {
        String displayName = I18NUtilities.findParameterDisplayName(clazz, name)
        return displayName ?: name
    }

    static String getSheetName(Component component) {
        return component.name
    }
}
