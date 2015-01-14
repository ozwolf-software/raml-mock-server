package net.ozwolf.mockserver.raml.handlers;

import net.ozwolf.mockserver.raml.RamlResourceHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * # RAML File Handler
 *
 * Generally used in conjunction with a `RemoteSpecification` that needs to handle the remote resource being called upon.
 *
 * Expected to create a concrete version of the remote specification in a location it can then be loaded for testing.
 *
 * ## Example Usage
 *
 * ```java
 * RamlSpecification specification = new RemoteSpecification("my-service", "http://remote.site.com/apispecs.raml", RamlFileHandler.handler("target/specifications/my-service"));
 * ```
 *
 * @see net.ozwolf.mockserver.raml.specification.RemoteSpecification
 */
public class RamlFileHandler implements RamlResourceHandler {
    private final File targetDirectory;

    /**
     * Create a new RAML remote resource handler.
     *
     * @param targetDirectory The target directory to copy the downloaded RAML file to.
     */
    public RamlFileHandler(File targetDirectory) {
        this.targetDirectory = targetDirectory;
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

            FileUtils.copyFileToDirectory(file, targetDirectory);

            File specificationFile = Paths.get(targetDirectory.getPath(), file.getName()).toFile();
            String specificationPath = FilenameUtils.separatorsToUnix(specificationFile.getPath());
            if (!specificationFile.exists() || !specificationFile.canRead())
                throw new IllegalStateException(String.format("Could not find or access [ %s ].", specificationPath));

            return specificationFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a resource handler that expects the remote resource file to be a single RAML specification file.  Will copy the file into the specified target directory and return a pointer.
     *
     * **Example Usage**
     *
     * ```java
     * RemoteResourceHandler handler = RemoteResourceHandler.ramlFileHandler("target/specifications/remote-service");
     * ```
     *
     * @param targetDirectory The target directory to place the downloaded resource into.
     * @return The pointer to the RAML specification file.
     */
    public static RamlResourceHandler handler(String targetDirectory) {
        return new RamlFileHandler(new File(targetDirectory));
    }
}
