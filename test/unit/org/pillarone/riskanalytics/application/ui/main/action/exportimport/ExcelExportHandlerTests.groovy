package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import models.application.ApplicationModel
import models.orsa.ORSAModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ExcelExportHandler
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.discounting.YieldCurveTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.AnnualIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.DeterministicIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.LinkRatioIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PolicyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PremiumIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReservesIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.RunOffIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumSelectionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureAPConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureProfitCommissionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureReinstatementConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ContractConstraint
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixStructureContraints
import org.pillarone.riskanalytics.domain.utils.constraint.DateTimeConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.IntDateTimeDoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractContraints
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion

class ExcelExportHandlerTests extends GroovyTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        LocaleResources.setTestMode()
        ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LocaleResources.clearTestMode()
    }

    void testExportModel() {
        ExcelExportHandler handler = new ExcelExportHandler(new ApplicationModel())
        byte[] result = handler.exportModel()
        assert result != null
        XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))
        assert 7 == workbook.numberOfSheets
        assert workbook.getSheet('Global Parameter Component')
        assert workbook.getSheet('parameter Component')
        assert workbook.getSheet('hierarchy Component')
        assert workbook.getSheet('dynamic Component')
        assert workbook.getSheet('composed Component')
        assert workbook.getSheet('MDP0-ExampleResourceConstraints')
        assert workbook.getSheet('Meta-Info')
    }
}
