package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import models.application.ApplicationModel
import models.core.CoreModel
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

class ExcelImportHandlerTests extends GroovyTestCase {
    File exportFile

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        LocaleResources.setTestMode()
        ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
        exportFile = File.createTempFile('excel', '.xlsx')
        exportFile.bytes = new ExcelExportHandler(new ApplicationModel()).exportModel()
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LocaleResources.clearTestMode()
    }

    void testIncorrectModelClass() {
        ExcelImportHandler handler = new ExcelImportHandler()
        handler.loadWorkbook(new FileInputStream(exportFile), "test.xlsx")
        handler.workbook.getSheet(AbstractExcelHandler.META_INFO_SHEET).getRow(1).getCell(1).setCellValue(CoreModel.class.name)
        List<ImportResult> result = handler.validate(new ApplicationModel())
        assert 1 == result.size()
    }

    void testModelInfoNotFound() {
        ExcelImportHandler handler = new ExcelImportHandler()
        handler.loadWorkbook(new FileInputStream(exportFile), "test.xlsx")
        handler.workbook.getSheet(AbstractExcelHandler.META_INFO_SHEET).getRow(1).getCell(1).setCellValue(null as String)
        List<ImportResult> result = handler.validate(new ApplicationModel())
        assert 1 == result.size()
    }

    void testMissingMetaInfoSheet() {
        ExcelImportHandler handler = new ExcelImportHandler()
        handler.loadWorkbook(new FileInputStream(exportFile), "test.xlsx")
        int sheetIndex = handler.workbook.getSheetIndex(AbstractExcelHandler.META_INFO_SHEET)
        handler.workbook.removeSheetAt(sheetIndex)
        List<ImportResult> result = handler.validate(new ApplicationModel())
        assert 1 == result.size()
    }

    void testMissingMDPSheet() {
        ExcelImportHandler handler = new ExcelImportHandler()
        handler.loadWorkbook(new FileInputStream(exportFile), "test.xlsx")
        XSSFSheet parmComponentSheet = handler.workbook.getSheet('parameter Component')
        XSSFRow dataRow = parmComponentSheet.createRow(2)
        dataRow.createCell(2).setCellValue('RESOURCE')
        dataRow.createCell(10).setCellValue('tableName')
        int sheetIndex = handler.workbook.getSheetIndex('MDP0-ExampleResourceConstraints')
        handler.workbook.removeSheetAt(sheetIndex)
        List<ImportResult> result = handler.validate(new ApplicationModel())
        assert 1 == result.size()
    }

    void testMissingMDPTableReference() {
        ExcelImportHandler handler = new ExcelImportHandler()
        handler.loadWorkbook(new FileInputStream(exportFile), "test.xlsx")
        XSSFSheet parmComponentSheet = handler.workbook.getSheet('parameter Component')
        XSSFRow dataRow = parmComponentSheet.createRow(2)
        dataRow.createCell(2).setCellValue('RESOURCE')
        dataRow.createCell(10).setCellValue('tableName')
        List<ImportResult> result = handler.validate(new ApplicationModel())
        assert 1 == result.size()
    }

    void testMissingMDPTableIdentifier() {
        ExcelImportHandler handler = new ExcelImportHandler()
        handler.loadWorkbook(new FileInputStream(exportFile), "test.xlsx")
        XSSFSheet parmComponentSheet = handler.workbook.getSheet('parameter Component')
        XSSFRow dataRow = parmComponentSheet.createRow(2)
        dataRow.createCell(2).setCellValue('RESOURCE')
        dataRow.createCell(10).setCellValue('')
        List<ImportResult> result = handler.validate(new ApplicationModel())
        assert 1 == result.size()
    }

    void testIncorrectCellData() {
        ExcelImportHandler handler = new ExcelImportHandler()
        handler.loadWorkbook(new FileInputStream(exportFile), "test.xlsx")
        XSSFSheet sheet = handler.workbook.getSheet('parameter Component')
        XSSFRow dataRow = sheet.createRow(2)
        dataRow.createCell(1).setCellValue('DUMMY')
        List<ImportResult> result = handler.validate(new ApplicationModel())
        assert 1 == result.size()
    }
}
