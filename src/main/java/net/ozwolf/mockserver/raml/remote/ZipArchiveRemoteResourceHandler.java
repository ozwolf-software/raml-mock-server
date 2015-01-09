package net.ozwolf.mockserver.raml.remote;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.ozwolf.mockserver.raml.RemoteResourceHandler;

import java.io.File;
import java.nio.file.Paths;

/**
 * # ZIP Archive Remote Resource Handler
 *
 * This handler assumes that the remote resource is a ZIP archive resource.  It will unpack the resource into the target directory and return a File object referencing the expected specification RAML file.
 *
 * ## Example Usage
 *
 * ```java
 * RemoteResourceHandler handler = ZipArchiveRemoteResourceHandler.handler("target/specifications/remote-service", "apispecs.raml");
 *
 * RamlSpecification specification = RamlSpecification.fromRemotePath("http://remote.site.com/apispecs.zip", handler);
 *```
 */
public class ZipArchiveRemoteResourceHandler implements RemoteResourceHandler {
    private final File targetDirectory;
    private final File specificationFile;

    /**
     * Create a new ZIP archive remote resource handler.
     * @param targetDirectory The directory to unpack the ZIP archive into.
     * @param specificationFile The root RAML specification file to expect (sans path)
     */
    public ZipArchiveRemoteResourceHandler(File targetDirectory, File specificationFile) {
        this.targetDirectory = targetDirectory;
        this.specificationFile = specificationFile;
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

            ZipFile zipFile = new ZipFile(file);
            zipFile.extractAll(targetDirectory.getPath());

            if (!specificationFile.exists() || !specificationFile.canRead())
                throw new IllegalStateException(String.format("Could not find or access [ %s ].", specificationFile.getPath()));

            return specificationFile;
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a resource handler that expects a ZIP archive as the remote resource.  Will unpack the zip archive into the target directory and will return a File object to the named specification file.
     *
     * **Example Usage**
     *
     * ```java
     * RemoteResourceHandler handler = RemoteResourceHandler.archiveHandler("target/specifications/remote-service", "apispecs.raml");
     * ```
     *
     * @param targetDirectory       The directory to unpack the ZIP archive to.
     * @param specificationFileName The name of the root RAML specification file (sans pathing)
     * @return The pointer to the RAML specification file.
     */
    public static RemoteResourceHandler handler(String targetDirectory, String specificationFileName) {
        return new ZipArchiveRemoteResourceHandler(new File(targetDirectory), Paths.get(targetDirectory, specificationFileName).toFile());
    }
}
