package org.concordion.internal;

import java.io.IOException;
import java.io.InputStream;

import org.concordion.api.Resource;
import org.concordion.api.Source;

public class ClassPathSource implements Source {

    public InputStream createInputStream(Resource resource) throws IOException {
        InputStream inputStream = getResourceAsStream(resource);
        if (inputStream == null) {
            throw new IOException("Resource '" + resource.getPath() + "' not found");
        }
        return inputStream;
    }

    private InputStream getResourceAsStream(Resource resource) {
        return getClass().getResourceAsStream(resource.getPath());
    }

    public boolean canFind(Resource resource) {
        InputStream stream = getResourceAsStream(resource);
        if (stream == null) {
            return false;
        }
        try {
            stream.close();
        } catch (IOException e) {
            // Ignore
        }
        return true;
    }
}
