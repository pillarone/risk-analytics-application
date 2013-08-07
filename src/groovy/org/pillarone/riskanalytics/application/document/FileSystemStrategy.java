package org.pillarone.riskanalytics.application.document;

import com.canoo.common.FileUtilities;
import com.ulcjava.base.application.ClientContext;
import groovy.transform.CompileStatic;
import org.pillarone.riskanalytics.core.FileConstants;

import java.io.File;

@CompileStatic
public class FileSystemStrategy implements IShowDocumentStrategy {

    FileSystemStrategy() {
    }

    public void showDocument(String name, byte[] content, String mimeType) {
        name = name.replace(" ", "_"); //ClientContext.showDocument(fileName) does not work if the filename contains spaces
        FileUtilities.addFileToDirectory(FileConstants.TEMP_FILE_DIRECTORY, name, content);
        ClientContext.showDocument(new File(FileConstants.TEMP_FILE_DIRECTORY, name).getAbsolutePath());
    }
}
