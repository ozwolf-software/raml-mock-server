package net.ozwolf.mockserver.raml;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RamlSpecificationsRule implements TestRule {
    private final Map<String, RamlSpecification> specifications;

    public RamlSpecificationsRule() {
        this.specifications = new ConcurrentHashMap<>();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                specifications.entrySet().stream().forEach(e -> e.getValue().initialize());
                base.evaluate();
                specifications.entrySet().stream().forEach(e -> e.getValue().reset());
            }
        };
    }

    public RamlSpecificationsRule withSpecification(String name, RamlProvider provider) {
        this.specifications.put(name, new RamlSpecification(provider));
        return this;
    }

    public RamlSpecification get(String specificationName) {
        if (!this.specifications.containsKey(specificationName))
            throw new IllegalArgumentException(String.format("No specification defined for [ %s ]", specificationName));

        return this.specifications.get(specificationName);
    }
}
