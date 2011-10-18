package org.pillarone.riskanalytics.application.document;

import com.canoo.common.FileUtilities;
import com.ulcjava.base.application.ClientContext;
import org.pillarone.riskanalytics.core.FileConstants;

import java.io.File;

public class FileSystemStrategy implements IShowDocumentStrategy {

    FileSystemStrategy() {
    }

    public void showDocument(String name, byte[] content, String mimeType) {
        FileUtilities.addFileToDirectory(FileConstants.REPORT_PDF_DIRECTORY, name, content);
        ClientContext.showDocument(new File(FileConstants.REPORT_PDF_DIRECTORY, name).getAbsolutePath());
    }
}
