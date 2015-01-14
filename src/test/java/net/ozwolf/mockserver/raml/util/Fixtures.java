package net.ozwolf.mockserver.raml.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;

public class Fixtures {
    public static String fixture(String resource) throws IOException {
        return IOUtils.toString(Fixtures.class.getClassLoader().getResourceAsStream(resource))
                .replaceAll("\r\n", "\n");
    }

    public static File zipFixture(String path) throws ZipException {
        File source = new File(path);
        File target = new File("target/archives/zip-archive-test.zip");
        if (target.exists() && !target.delete())
            throw new RuntimeException("Unable to delete old zip file target");

        if (!target.getParentFile().exists() && !target.getParentFile().mkdirs())
            throw new RuntimeException("Unable to create directories for zip file target");

        ZipFile zipFile = new ZipFile(target);
        ZipParameters parameters = new ZipParameters();
        parameters.setIncludeRootFolder(false);
        zipFile.createZipFileFromFolder(source, parameters, false, -1);

        return target;
    }
}
