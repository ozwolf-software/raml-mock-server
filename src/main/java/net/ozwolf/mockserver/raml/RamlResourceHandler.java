package net.ozwolf.mockserver.raml;

import java.io.File;

/**
 * # Remote Resource Handler
 *
 * This interface is used to pass to RAML specification file when you wish to retrieve a specification from a remote resource.
 *
 * Two default handlers are provided automatically:
 *
 * + `RamlFileHandler` - Assumes the resource is a single RAML file and will copy it to the specified target directory for further use.
 * + `ZipArchiveHandler` - Assumes the resource is a ZIP archive that needs to be unpacked then copied to the specified target directoy.  It then uses the specified specification file name to load specifications.
 *
 * @see net.ozwolf.mockserver.raml.handlers.RamlFileHandler
 * @see net.ozwolf.mockserver.raml.handlers.ZipArchiveHandler
 */
public interface RamlResourceHandler {
    /**
     * Handle the downloaded file and return a File object referencing the root RAML specification file.
     *
     * @param file The downloaded file resource
     * @return The RAML specification file
     */
    File handle(File file);
}
