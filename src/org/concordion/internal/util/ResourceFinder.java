package org.concordion.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class ResourceFinder {
    private Class baseClass;

    public ResourceFinder(Class baseClass) {
        this.baseClass = baseClass;
    }

    public String getResourceAsString(String relativeResourcePathOfXmlFile) {
        URL resourceUrl;
        if (relativeResourcePathOfXmlFile.startsWith("/")) {
            resourceUrl = baseClass.getResource(relativeResourcePathOfXmlFile);
        } else {
            String trailingPart = relativeResourcePathOfXmlFile;
            String leadingPart = "/" + baseClass.getName().replace('.', '/');
            leadingPart = leadingPart.substring(0, leadingPart.lastIndexOf('.'));
            while (true) {
                if (trailingPart.startsWith("./")) {
                    trailingPart = trailingPart.substring(2);
                } else if (trailingPart.startsWith("../")) {
                    trailingPart = trailingPart.substring(3);
                    if (leadingPart.length() > 1) {
                        int secondSlash = leadingPart.indexOf('/', 1);
                        if (secondSlash != -1) {
                            leadingPart = leadingPart.substring(secondSlash);
                        }
                    }
                } else {
                    break;
                }
            }
            resourceUrl = baseClass.getResource(leadingPart + '/' + trailingPart);
        }

        StringBuffer result = new StringBuffer();
        try {
            InputStream stream = null;
            stream = resourceUrl.openStream();
            InputStreamReader reader = new InputStreamReader(stream);
            char[] buf = new char[4096];
            int charsRead = -1;
            while ((charsRead = reader.read(buf)) > 0) {
                result.append(buf, 0, charsRead);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading test resource " + relativeResourcePathOfXmlFile, e);
        }

        return result.toString();
    }

}
