package net.ozwolf.mockserver.raml.specification;

import net.ozwolf.mockserver.raml.RamlSpecification;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.io.InputStream;

/**
 * # ClassPath RAML Specification
 *
 * Create a RAML specification based on a class path location.
 *
 * ## Example Usage
 *
 * ```java
 * RamlSpecification specification = new ClassPathSpecification("my-service", "apispecs/apispecs.raml");
 * ```
 *
 * @see net.ozwolf.mockserver.raml.RamlSpecification
 */
public class ClassPathSpecification extends RamlSpecification {
    private final String resource;

    /**
     * Create a new RAML specification
     *
     * @param name     The name of specification
     * @param resource The ClassPath resource to load
     */
    public ClassPathSpecification(String name, String resource) {
        super(name);
        this.resource = resource;
    }

    @Override
    protected Raml getRaml() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        if (stream == null)
            throw new IllegalStateException(String.format("[ %s ] resource does not exist", resource));

        return new RamlDocumentBuilder().build(resource);
    }
}
