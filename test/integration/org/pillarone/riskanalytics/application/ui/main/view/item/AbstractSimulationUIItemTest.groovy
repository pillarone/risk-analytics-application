package org.pillarone.riskanalytics.application.ui.main.view.item

import models.application.ApplicationModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.*
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCToggleButtonOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCButtonOperator
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultTransferObject
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultWriter
import org.pillarone.riskanalytics.core.simulation.engine.grid.output.ResultDescriptor

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractSimulationUIItemTest extends AbstractUIItemTest {

    PathMapping path1
    PathMapping path2
    FieldMapping field
    CollectorMapping collector1

    ResultWriter resultWriter

    public SimulationRun createResults(String name, int index) {
        SimulationRun run = createSimulationRun(name)
        resultWriter = new ResultWriter(run.id)
        writeResult new SingleValueResult(simulationRun: run, path: path1, collector: collector1, field: field, period: 0, iteration: 1, value: 1000)
        writeResult new SingleValueResult(simulationRun: run, path: path2, collector: collector1, field: field, period: 0, iteration: 1, value: 1000)
        writeResult new SingleValueResult(simulationRun: run, path: path1, collector: collector1, field: field, period: 0, iteration: 2, value: 500)
        writeResult new SingleValueResult(simulationRun: run, path: path2, collector: collector1, field: field, period: 0, iteration: 2, value: 1)

        assertNotNull new PostSimulationCalculation(run: run, keyFigure: PostSimulationCalculation.MEAN, collector: collector1, path: path1, field: field, period: 0, result: 11.1 * index).save()
        assertNotNull new PostSimulationCalculation(run: run, keyFigure: PostSimulationCalculation.MEAN, collector: collector1, path: path1, field: field, period: 0, result: 22.2 * index).save()
        assertNotNull new PostSimulationCalculation(run: run, keyFigure: PostSimulationCalculation.MEAN, collector: collector1, path: path2, field: field, period: 0, result: 33.3 * index).save()
        assertNotNull new PostSimulationCalculation(run: run, keyFigure: PostSimulationCalculation.MEAN, collector: collector1, path: path2, field: field, period: 0, result: 44.4 * index).save()


        return run
    }

    public SimulationRun createSimulationRun(String name) {

        SimulationRun run = new SimulationRun(periodCount: 1, name: "testRun")
        run.name = name
        run.parameterization = ParameterizationDAO.list()[0]
        run.resultConfiguration = ResultConfigurationDAO.list()[0]
        run.model = "${ApplicationModel.class.name}"
        run.periodCount = 1
        run.iterations = 2
        run.startTime = new DateTime()
        run.endTime = new DateTime()
        run.save()
        return run
    }

    public void init() {
        path1 = PathMapping.findByPathName("Application:composedComponent:subDynamicComponent:outValue1")
        if (path1 == null) {
            path1 = new PathMapping(pathName: 'Application:composedComponent:subDynamicComponent:outValue1').save()
        }

        path2 = PathMapping.findByPathName('Application:composedComponent:subDynamicComponent:subComponent:outFirstValue')
        if (path2 == null) {
            path2 = new PathMapping(pathName: 'Application:composedComponent:subDynamicComponent:subComponent:outFirstValue').save()
        }

        field = FieldMapping.findByFieldName('value')
        if (field == null) {
            field = new FieldMapping(fieldName: 'value').save()
        }

        collector1 = CollectorMapping.findByCollectorName(AggregatedCollectingModeStrategy.IDENTIFIER)
        if (collector1 == null) {
            collector1 = new CollectorMapping(collectorName: AggregatedCollectingModeStrategy.IDENTIFIER).save()
        }
    }

    protected void addResultFunction(ULCFrameOperator frameOperator, String buttonName) {
        ULCButtonOperator buttonOperator = new ULCButtonOperator(frameOperator, new ComponentByNameChooser(buttonName))
        assertNotNull buttonOperator

        buttonOperator.getFocus()

        buttonOperator.clickMouse()

        Thread.sleep(1000)
    }

    protected void addFunction(ULCFrameOperator frameOperator, String buttonName) {
        ULCToggleButtonOperator toggleButtonOperator = new ULCToggleButtonOperator(frameOperator, new ComponentByNameChooser(buttonName))
        assertNotNull toggleButtonOperator

        toggleButtonOperator.getFocus()

        toggleButtonOperator.clickMouse()

        Thread.sleep(1000)
    }

    private void writeResult(SingleValueResult result) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(result.iteration);
        dos.writeInt(1);
        dos.writeDouble(result.value);
        dos.writeLong(0);

        resultWriter.writeResult(new ResultTransferObject(new ResultDescriptor(result.field.id, result.path.id, result.collector.id, result.period), null, bos.toByteArray(), 0));
    }

}
