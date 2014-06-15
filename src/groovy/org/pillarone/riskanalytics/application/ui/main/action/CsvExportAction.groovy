package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.util.IFileStoreHandler
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CsvExportAction extends ExportItemAction {
    Log LOG = LogFactory.getLog(CsvExportAction)

    public CsvExportAction(ULCTableTree tree) {
        super(tree, "CsvExportAction", 'csv')
    }

    protected void exportItem(Simulation item, int itemCount, filePaths, ULCWindow ancestor) {
        // Quick way to have a transaction ? (SingleValueResult table is empty.)
        //
        SingleValueResult.withTransaction { trx ->
            String selectedFile = itemCount > 1 ? "${filePaths[0]}/${getFileName(item)}" : filePaths[0]

            IFileStoreHandler fileStoreHandler = [
                prepareFile: { OutputStream stream ->
                    try {
                        item.load()
                        def simulationRun = item.simulationRun
                        String fileName = ResultAccessor.exportCsv(simulationRun) //Was major bottleneck (writing CSV on serverside)
                        if (fileName) {
                            long t = System.currentTimeMillis()
                            stream.write( UIUtils.toCSV(getSimulationSettings(simulationRun)).getBytes() )
                            stream.flush()
                            LOG.info("Timed ${System.currentTimeMillis() - t} ms: Wrote settings to clientside CSV for Sim '${simulationRun.name}'");

                            FileInputStream fis = new FileInputStream(fileName)
                            stream.write("\nITERATION,PERIOD,PATH,FIELD,VALUE,COLLECTOR,DATE\n".bytes)
                            // PMO-2676 ? Probably heap exceed here.  If so, could loop and do transfer in chunks.
                            //
                            LOG.info("About to stream ${fileName} to clientside")
                            t = System.currentTimeMillis()
                            final SIZE = 1024 * 1024;
                            byte[] chunk = new byte[SIZE];
                            int nRead = -1;
                            long nWrote = 0;
                            while ( (nRead=fis.read( chunk, 0, SIZE )) != -1 ) {
                                stream.write( chunk, 0, nRead );
                                nWrote += nRead
//                              LOG.info("Wrote chunk of $nRead bytes to clientside stream")
                            }
                            chunk = null // give gc chance asap
                            stream.flush()
                            LOG.info("Timed ${System.currentTimeMillis() - t} ms: streaming ${nWrote}-bytes of iters for sim: '${simulationRun.name}'");
                        } else {
                            showAlert("exportError")
                        }
                    } catch (Throwable t) {
                        LOG.error("Export failed: " + t.message, t)
                        showAlert("exportError")
                    } finally {
                        stream.close()
                    }
                },
                onSuccess: { path, name ->
                   // NOP
                },
                onFailure: { reason, description ->
                    LOG.error description
                    showAlert("exportError")
                }
            ] as IFileStoreHandler

            ClientContext.storeFile(fileStoreHandler, selectedFile, Long.MAX_VALUE, false)
        }

    }

    // Maybe the validation in class ExportAction is for Excel's benefit ?
    //
    protected boolean validate(List items) {
        return true
    }
}
