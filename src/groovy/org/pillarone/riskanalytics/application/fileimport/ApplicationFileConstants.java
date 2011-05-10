package org.pillarone.riskanalytics.application.fileimport;

import org.pillarone.riskanalytics.core.FileConstants;

import java.io.File;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class ApplicationFileConstants extends FileConstants {

    public static final String COMMENT_FILE_DIRECTORY;

    static {
        COMMENT_FILE_DIRECTORY = BASE_DATA_DIRECTORY + File.separatorChar + "commentFiles";
        File file = new File(COMMENT_FILE_DIRECTORY);
        file.mkdirs();
        assert file.exists();

    }
}
