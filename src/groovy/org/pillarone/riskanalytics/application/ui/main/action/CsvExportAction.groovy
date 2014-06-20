package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.util.IFileStoreHandler
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CsvExportAction extends ExportItemAction {
    static Log LOG = LogFactory.getLog(CsvExportAction)

    public CsvExportAction(ULCTableTree tree) {
        super(tree, "CsvExportAction", 'csv')
    }

    protected void exportItem(Simulation item, int itemCount, filePaths, ULCWindow ancestor) {

        try{
            SimulationRun simulationRun = item.simulationRun
            String fileName = ResultAccessor.exportCsv(simulationRun, ONE_MEG, EXPORT_TRANSFER_MAX_BYTES) //Was major bottleneck (writing CSV on serverside)
            if (fileName) {
                SingleValueResult.withTransaction { trx ->
                    String selectedFile = itemCount > 1 ? "${filePaths[0]}/${getFileName(item)}" : filePaths[0]

                    IFileStoreHandler fileStoreHandler = [
                            prepareFile: { OutputStream stream ->
                                try {
                                    FileInputStream fis = new FileInputStream(fileName)

                                    stream.write( UIUtils.toCSV(getSimulationSettings(simulationRun, '\t')).getBytes() )
                                    stream.write("\nITERATION,PERIOD,PATH,FIELD,VALUE,COLLECTOR,DATE\n".bytes)
                                    LOG.info("About to stream ${fileName} to clientside")
                                    long t = System.currentTimeMillis()
                                    final int CHUNK_SIZE = ONE_MEG;
                                    byte[] chunk = new byte[CHUNK_SIZE];
                                    int nRead = -1;
                                    long nWrote = 0;
                                    while ( (nRead=fis.read( chunk, 0, CHUNK_SIZE )) != -1 ) {
                                        stream.write( chunk, 0, nRead );
                                        nWrote += nRead
                                    }
                                    chunk = null // give gc chance asap
                                    stream.flush()
                                    LOG.info("Timed ${System.currentTimeMillis() - t} ms: Wrote ${nWrote}-bytes iter data to clientside CSV for Sim: '${simulationRun.name}'");
                                } finally {
                                    stream.close() // should be safe even if ULC tries to close it again (InputStream.close() required to be idempotent)
                                }
                            },
                            onSuccess: { path, name ->
                                showInfoAlert("Successfully exported CSV file", "Filename: $name saved in folder:\n $path", true)
                            },
                            onFailure: { reason, description ->
                                LOG.error description
                                showAlert("exportError")
                            }
                    ] as IFileStoreHandler

                    ClientContext.storeFile(fileStoreHandler, selectedFile, EXPORT_TRANSFER_MAX_BYTES, false)
                }
            } else {
                showAlert("exportError")
            }
        } catch( IllegalStateException e){
            if(e.message?.startsWith("CSV ")){
                showInfoAlert("CSV file exceeds limits", "Sorry, " + e.message)
            } else {
                LOG.error("Export failed: " + t.message, t)
                showAlert("exportError")
            }
        }
        catch (Throwable t) {
            LOG.error("Export failed: " + t.message, t)
            showAlert("exportError")
        }
    }

    // Maybe the validation in class ExportAction is for Excel's benefit ?
    //
    protected boolean validate(List items) {
        return true
    }
}
