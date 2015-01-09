package net.ozwolf.mockserver.raml.provider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import net.ozwolf.mockserver.raml.RamlProvider;
import net.ozwolf.mockserver.raml.RemoteResourceHandler;
import org.raml.model.Raml;

import java.io.File;
import java.net.URI;

public class RemoteProvider implements RamlProvider {
    private final Client client;
    private final URI uri;
    private final RemoteResourceHandler handler;

    public RemoteProvider(String url, RemoteResourceHandler handler) {
        this.client = Client.create(new DefaultClientConfig());
        this.uri = URI.create(url);
        this.handler = handler;
    }

    @Override
    public Raml getRaml() {
        File file = client.resource(uri).get(File.class);
        File specificationFile = handler.handle(file);
        return new FilePathProvider(specificationFile.getPath()).getRaml();
    }
}
