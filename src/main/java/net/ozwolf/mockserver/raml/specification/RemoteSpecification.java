package net.ozwolf.mockserver.raml.specification;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import net.ozwolf.mockserver.raml.RamlSpecification;
import net.ozwolf.mockserver.raml.RemoteResourceHandler;
import net.ozwolf.mockserver.raml.provider.FilePathProvider;
import org.raml.model.Raml;

import java.io.File;
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
        File file = client.resource(resource).get(File.class);
        File specificationFile = handler.handle(file);
        return new FilePathProvider(specificationFile.getPath()).getRaml();
    }
}
