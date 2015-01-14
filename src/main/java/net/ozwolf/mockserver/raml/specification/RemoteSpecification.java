package net.ozwolf.mockserver.raml.specification;

import net.ozwolf.mockserver.raml.RamlResourceHandler;
import net.ozwolf.mockserver.raml.RamlSpecification;
import org.apache.commons.io.FileUtils;
import org.raml.model.Raml;
import org.raml.parser.loader.FileResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * # Remote RAML Specification
 *
 * Create a RAML specification based on a remote location, using the provided handler to create a local copy of this for loading the specification.
 *
 * ## Example Usage
 *
 * ```java
 * RamlSpecification specification = new RemoteSpecification("my-service", "http://remote.site.com/apispecs.raml", RamlFileHandler.handler("target/specifications/my-service"));
 * ```
 */
public class RemoteSpecification extends RamlSpecification {
    private final URI resource;
    private final RamlResourceHandler handler;

    /**
     * Create a new remote RAML specification
     *
     * @param name    The name of the specification
     * @param url     The URL the specification can be found at.
     * @param handler A handler for converting the remote into a local specification path.
     * @see net.ozwolf.mockserver.raml.handlers.RamlFileHandler
     * @see net.ozwolf.mockserver.raml.handlers.ZipArchiveHandler
     */
    public RemoteSpecification(String name, String url, RamlResourceHandler handler) {
        super(name);
        this.resource = URI.create(url);
        this.handler = handler;
    }

    @Override
    protected Raml getRaml() {
        try {
            File file = File.createTempFile("raml-mock-server-", ".spec");
            file.deleteOnExit();
            FileUtils.copyURLToFile(this.resource.toURL(), file);
            File specificationFile = handler.handle(file);

            ResourceLoader loader = new FileResourceLoader(specificationFile.getParent());
            return new RamlDocumentBuilder(loader).build(FileUtils.readFileToString(specificationFile), specificationFile.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
