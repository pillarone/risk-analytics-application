package org.pillarone.riskanalytics.application.ui.main.action

import models.application.ApplicationModel
import models.orsa.ORSAModel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.pillarone.riskanalytics.core.components.ExampleMultiMarkerConstraint
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
        ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
        ConstraintsFactory.registerConstraint(new AnnualIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new LinkRatioIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new DeterministicIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new PolicyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new PremiumIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new FrequencyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new RunOffIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new ReservesIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new PatternTableConstraints())
        ConstraintsFactory.registerConstraint(new CoverMap())
        ConstraintsFactory.registerConstraint(new MatrixStructureContraints())
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
        ConstraintsFactory.registerConstraint(new IntDateTimeDoubleConstraints())
        ConstraintsFactory.registerConstraint(new DateTimeConstraints())
        ConstraintsFactory.registerConstraint(new ClaimTypeSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new YieldCurveTableConstraints())
        ConstraintsFactory.registerConstraint(new SegmentPortion())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractContraints())
        ConstraintsFactory.registerConstraint(new PremiumSelectionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
        ConstraintsFactory.registerConstraint(new ContractConstraint())

        ConstraintsFactory.registerConstraint(new PremiumStructureReinstatementConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureAPConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureProfitCommissionConstraints())
        ConstraintsFactory.registerConstraint(new PerilPortion())
        ConstraintsFactory.registerConstraint(new ReservePortion())
        ConstraintsFactory.registerConstraint(new UnderwritingPortion())
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
    }

    void testExportModel() {
        ExcelExportHandler handler = new ExcelExportHandler(new ORSAModel())
        byte[] result = handler.exportModel()
        assert result != null
        new XSSFWorkbook(new ByteArrayInputStream(result)).write(new FileOutputStream(new File('exportresult.xlsx')))
    }
}
