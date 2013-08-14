package org.pillarone.riskanalytics.application.document;

import com.canoo.common.FileUtilities;
import com.ulcjava.base.application.ClientContext;
import groovy.transform.CompileStatic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.FileConstants;

import java.io.File;
import java.net.MalformedURLException;

@CompileStatic
public class FileSystemStrategy implements IShowDocumentStrategy {
    Log LOG = LogFactory.getLog(FileSystemStrategy.class);

    FileSystemStrategy() {
    }

    public void showDocument(String name, byte[] content, String mimeType) {
        name = name.replace(" ", "_"); //ClientContext.showDocument(fileName) does not work if the filename contains spaces
        FileUtilities.addFileToDirectory(FileConstants.TEMP_FILE_DIRECTORY, name, content);
        File file = new File(FileConstants.TEMP_FILE_DIRECTORY, name);
        String urlString = file.getAbsolutePath();
        try {
            urlString = file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            LOG.error("Cannot encode file to url.",e);
        }
        ClientContext.showDocument(urlString,null);
    }
}
