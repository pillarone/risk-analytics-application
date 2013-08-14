package org.pillarone.riskanalytics.application.client;

import com.ulcjava.base.client.FileContents;
import com.ulcjava.base.client.ServiceException;
import com.ulcjava.base.client.UIComponent;
import com.ulcjava.base.shared.FileChooserConfig;
import com.ulcjava.base.trusted.AllPermissionsFileService;

import java.io.File;
import java.io.InputStream;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class AllPermissionsFileExtendedService extends AllPermissionsFileService {

    @Override
    public FileContents storeFile(String filePath, InputStream inputStream, boolean failIfExists) throws ServiceException {
        createDirectory(filePath);
        return super.storeFile(filePath, inputStream, failIfExists);
    }

    @Override
    public FileContents storeFile(FileChooserConfig fileChooserConfig, UIComponent parent, InputStream inputStream) throws ServiceException {
        createDirectory(fileChooserConfig.getSelectedFile());
        return super.storeFile(fileChooserConfig, parent, inputStream);
    }

    private void createDirectory(String filePath) {
        final int index = filePath.lastIndexOf(File.separator);
        if (index != -1) {
            File f = new File(filePath.substring(0, index));
            if (!f.exists()) {
                if (!f.mkdirs()) {
                    throw new RuntimeException("Failed to create directories on client.");
                }
            }
        }
    }
}
