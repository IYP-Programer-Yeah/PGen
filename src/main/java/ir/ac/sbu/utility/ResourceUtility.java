package ir.ac.sbu.utility;

import ir.ac.sbu.exception.ResourceNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class ResourceUtility {
    private ResourceUtility() {
    }

    public static URL getResource(String path) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found: " + path);
        } else {
            return resource;
        }
    }

    public static InputStream getResourceAsStream(String path) {
        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (resource == null) {
            throw new ResourceNotFoundException("Resource not found: " + path);
        } else {
            return resource;
        }
    }
}
