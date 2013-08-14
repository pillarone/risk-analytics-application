package org.pillarone.riskanalytics.application.document;

import com.ulcjava.base.application.ClientContext;
import com.ulcjava.base.application.util.IFileStoreHandler;
import groovy.transform.CompileStatic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.OutputStream;

@CompileStatic
public class StoreFileStrategy implements IShowDocumentStrategy {

    private static Log LOG = LogFactory.getLog(StoreFileStrategy.class);

    public void showDocument(String name, final byte[] content, final String mimeType) {
        name = name.replace(" ", "_"); //ClientContext.showDocument(fileName) does not work if the filename contains spaces
        final String userHome = ClientContext.getSystemProperty("user.home");
        final String fileSeparator = ClientContext.getSystemProperty("file.separator");
        try {
            final String fileName = new StringBuilder(userHome).append(fileSeparator).
                    append(".pillarone").append(fileSeparator).
                    append("client").append(fileSeparator).
                    append("temp").append(fileSeparator).
                    append(name).toString();

            ClientContext.storeFile(new IFileStoreHandler() {

                public void prepareFile(OutputStream outputStream) throws Exception {
                    outputStream.write(content);
                }

                public void onSuccess(String s, String s1) {
                    LOG.info("Successfully stored " + s);
                }

                public void onFailure(int i, String s) {
                    throw new RuntimeException("Failed to store file: " + s);
                }

            }, fileName);
            ClientContext.showDocument(new File(fileName).toURI().toURL().toString(),null);
        } catch (Exception e) {
            throw new RuntimeException("Error storing file", e);
        }
    }
}
