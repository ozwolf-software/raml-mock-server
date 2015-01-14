package net.ozwolf.mockserver.raml.handlers;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.ozwolf.mockserver.raml.RamlResourceHandler;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Paths;

/**
 * # ZIP Archive Handler
 *
 * Generally used in conjunction with a `RemoteSpecification` that needs to handle the remote resource being called upon.
 *
 * Will unpack the downloaded zip resource into a local concrete location for testing.
 *
 * ## Example Usage
 *
 * ```java
 * RamlSpecification specification = new RemoteSpecification("my-service", "http://remote.site.com/apispecs.zip", ZipArchiveHandler.handler("target/specifications/my-service", "apispecs.raml"));
 * ```
 *
 * @see net.ozwolf.mockserver.raml.specification.RemoteSpecification
 */
public class ZipArchiveHandler implements RamlResourceHandler {
    private final File targetDirectory;
    private final File specificationFile;

    /**
     * Create a new ZIP archive remote resource handler.
     *
     * @param targetDirectory   The directory to unpack the ZIP archive into.
     * @param specificationFile The root RAML specification file to expect (sans path)
     */
    public ZipArchiveHandler(File targetDirectory, File specificationFile) {
        this.targetDirectory = targetDirectory;
        this.specificationFile = specificationFile;
    }

    /**
     * Retrieve the specification file from the remote resource.
     *
     * @param file The downloaded file resource
     * @return The RAML specification file
     * @throws java.lang.IllegalAccessError       Target directory could not be created.
     * @throws java.lang.IllegalArgumentException The target directory is not a directory
     * @throws java.lang.IllegalStateException    The target directory could not be written to or the specification file was not found.
     */
    @Override
    public File handle(File file) {
        try {
            String targetPath = FilenameUtils.separatorsToUnix(targetDirectory.getPath());
            if (!targetDirectory.exists() && !targetDirectory.mkdirs())
                throw new IllegalAccessError(String.format("Could not create [ %s ] directory.", targetPath));

            if (targetDirectory.exists() && !targetDirectory.isDirectory())
                throw new IllegalArgumentException(String.format("[ %s ] is not a directory.", targetPath));

            if (targetDirectory.exists() && !targetDirectory.canWrite())
                throw new IllegalStateException(String.format("Cannot write to [ %s ].", targetPath));

            ZipFile zipFile = new ZipFile(file);
            zipFile.extractAll(targetDirectory.getPath());

            if (!specificationFile.exists() || !specificationFile.canRead())
                throw new IllegalStateException(String.format("Could not find or access [ %s ].", FilenameUtils.separatorsToUnix(specificationFile.getPath())));

            return specificationFile;
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a resource handler that expects a ZIP archive as the remote resource.  Will unpack the zip archive into the target directory and will return a File object to the named specification file.
     *
     * @param targetDirectory       The directory to unpack the ZIP archive to.
     * @param specificationFileName The name of the root RAML specification file (sans pathing)
     * @return The pointer to the RAML specification file.
     */
    public static RamlResourceHandler handler(String targetDirectory, String specificationFileName) {
        return new ZipArchiveHandler(new File(targetDirectory), Paths.get(targetDirectory, specificationFileName).toFile());
    }
}
