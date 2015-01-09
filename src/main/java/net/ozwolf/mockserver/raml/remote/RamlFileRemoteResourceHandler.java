package net.ozwolf.mockserver.raml.remote;

import net.ozwolf.mockserver.raml.RemoteResourceHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * # RAML File Remote Resource Handler
 *
 * This handler expects to find a single RAML specification file at the given resource.  It will download it and expose it via the target directory.
 *
 * ## Example Usage
 *
 * ```java
 * RemoteResourceHandler handler = RamlFileRemoteResourceHandler.handler("target/specifications/remote-service");
 *
 * RamlSpecification specification = RamlSpecification.fromRemotePath("http://remote.site.com/apispecs.raml", handler);
 * ```
 */
public class RamlFileRemoteResourceHandler implements RemoteResourceHandler {
    private final File targetDirectory;

    /**
     * Create a new RAML remote resource handler.
     * @param targetDirectory The target directory to copy the downloaded RAML file to.
     */
    public RamlFileRemoteResourceHandler(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    /**
     * Retrieve the specification file from the remote resource.
     * @param file The downloaded file resource
     * @return The RAML specification file
     * @throws java.lang.IllegalAccessError Target directory could not be created.
     * @throws java.lang.IllegalArgumentException The target directory is not a directory
     * @throws java.lang.IllegalStateException The target directory could not be written to or the specification file was not found.
     */
    @Override
    public File handle(File file) {
        try {
            if (!targetDirectory.exists() && !targetDirectory.mkdirs())
                throw new IllegalAccessError(String.format("Could not create [ %s ] directory.", targetDirectory.getPath()));

            if (targetDirectory.exists() && !targetDirectory.isDirectory())
                throw new IllegalArgumentException(String.format("[ %s ] is not a directory.", targetDirectory.getPath()));

            if (targetDirectory.exists() && !targetDirectory.canWrite())
                throw new IllegalStateException(String.format("Cannot write to [ %s ].", targetDirectory.getPath()));

            FileUtils.copyFileToDirectory(file, targetDirectory);

            File specificationFile = Paths.get(targetDirectory.getPath(), file.getName()).toFile();
            if (!specificationFile.exists() || !specificationFile.canRead())
                throw new IllegalStateException(String.format("Could not find or access [ %s ].", specificationFile.getPath()));

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
    public static RemoteResourceHandler handler(String targetDirectory) {
        return new RamlFileRemoteResourceHandler(new File(targetDirectory));
    }
}
