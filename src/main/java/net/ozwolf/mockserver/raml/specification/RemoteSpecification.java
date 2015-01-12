package net.ozwolf.mockserver.raml.specification;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import net.ozwolf.mockserver.raml.RamlSpecification;
import net.ozwolf.mockserver.raml.RemoteResourceHandler;
import org.apache.commons.io.FileUtils;
import org.raml.model.Raml;
import org.raml.parser.loader.FileResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class RemoteSpecification extends RamlSpecification {
    private final Client client;
    private final URI resource;
    private final RemoteResourceHandler handler;

    public RemoteSpecification(String name, String resource, RemoteResourceHandler handler) {
        super(name);
        this.client = Client.create(new DefaultClientConfig());
        this.resource = URI.create(resource);
        this.handler = handler;
    }

    @Override
    protected Raml getRaml() {
        try {
            File file = client.resource(resource).get(File.class);
            File specificationFile = handler.handle(file);

            ResourceLoader loader = new FileResourceLoader(specificationFile.getParent());
            return new RamlDocumentBuilder(loader).build(FileUtils.readFileToString(specificationFile), specificationFile.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
