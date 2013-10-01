package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import models.application.ApplicationModel
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty
import org.pillarone.riskanalytics.application.ui.parameterization.model.TreeBuilderUtil
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model

class AbstractExcelHandler {
    XSSFWorkbook workbook = new XSSFWorkbook()
    Model modelInstance
    protected static String COMPONENT_HEADER_NAME = 'Component Name'
    protected static String DISABLE_IMPORT = 'Disable Import'
    protected static final int DATA_ROW_START_INDEX = 2
    protected static final int TECHNICAL_HEADER_ROW_INDEX = 1
    protected static final int HEADER_ROW_INDEX = 0


    AbstractExcelHandler(File excelFile) {
        workbook = new XSSFWorkbook(new FileInputStream(excelFile))
    }

    AbstractExcelHandler() {
    }

    Model getModel() {
        List<CTProperty> properties = workbook.getProperties().customProperties.underlyingProperties.propertyList
        CTProperty modelProperty
        for (CTProperty p : properties) {
            if (p.getName().equals('Model')) {
                modelProperty = p
            }
        }
//        assert modelProperty.lpwstr
//        return Thread.currentThread().contextClassLoader.loadClass(modelProperty.lpwstr).newInstance() as Model
        return new ApplicationModel()
    }

    Integer findParameterColumnIndex(Sheet sheet, String name, int columnStartIndex) {
        Integer columnIndex = null
        if (!name.startsWith('parm')) {
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
        Integer columnIndex = null
        (HEADER_ROW_INDEX..TECHNICAL_HEADER_ROW_INDEX).each {
            Row row = sheet.getRow(it)
            for (int index = columnStartIndex; index < row.lastCellNum; index++) {
                Cell cell = row.getCell(index)
                if (cell.getStringCellValue().equals(name)) {
                    columnIndex = index
                }
            }
        }
        return columnIndex
    }

    protected List getAllParms(Component component) {
        TreeBuilderUtil.collectProperties(component, 'parm')
    }

}
