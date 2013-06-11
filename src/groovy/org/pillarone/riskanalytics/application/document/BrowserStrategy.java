package org.pillarone.riskanalytics.application.document;

import com.ulcjava.base.application.ClientContext;
import groovy.transform.CompileStatic;
import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.codehaus.groovy.grails.web.util.WebUtils;
import org.pillarone.riskanalytics.application.UserContext;

import javax.servlet.http.HttpServletRequest;

@CompileStatic
public class BrowserStrategy implements IShowDocumentStrategy {

    BrowserStrategy() {
    }

    public void showDocument(String name, byte[] content, String mimeType) {
        final GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest();
        final HttpServletRequest request = webRequest.getCurrentRequest();

        final String key = RandomStringUtils.randomAlphanumeric(10);

        request.getSession().setAttribute(key, new Document(name, content, mimeType));

        ClientContext.showDocument(UserContext.getBaseUrl() + "/document/show/" + key, "_new");
    }

    public static class Document {

        private String name;
        private byte[] content;
        private String mimeType;

        public Document(String name, byte[] content, String mimeType) {
            this.name = name;
            this.content = content;
            this.mimeType = mimeType;
        }

        public byte[] getContent() {
            return content;
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getName() {
            return name;
        }
    }
}
