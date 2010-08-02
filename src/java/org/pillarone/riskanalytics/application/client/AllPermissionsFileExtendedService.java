package org.pillarone.riskanalytics.application.client;

import com.ulcjava.base.client.ServiceException;
import com.ulcjava.base.trusted.AllPermissionsFileService;

import java.io.File;
import java.io.InputStream;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class AllPermissionsFileExtendedService extends AllPermissionsFileService {


    @Override
    public com.ulcjava.base.client.FileContents storeFile(String filePath, InputStream in) throws ServiceException {
        try {
            File f = new File(filePath);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdir();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.storeFile(filePath, in);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
