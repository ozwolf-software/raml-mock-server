package net.ozwolf.mockserver.raml.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class Fixtures {
    public static String fixture(String resource) throws IOException {
        return IOUtils.toString(Fixtures.class.getClassLoader().getResourceAsStream(resource));
    }
}
