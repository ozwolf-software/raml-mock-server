package net.ozwolf.mockserver.raml.provider;

import net.ozwolf.mockserver.raml.RamlProvider;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.io.InputStream;

public class ClassPathProvider implements RamlProvider {
    private final String resource;

    public ClassPathProvider(String resource) {
        this.resource = resource;
    }

    @Override
    public Raml getRaml() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        if (stream == null)
            throw new IllegalStateException(String.format("[ %s ] resource does not exist", resource));

        return new RamlDocumentBuilder().build(resource);
    }
}
