package net.ozwolf.mockserver.raml;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class RamlSpecificationsRule implements TestRule {
    private final List<RamlSpecification> specifications;

    public RamlSpecificationsRule() {
        this.specifications = new Vector<>();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                specifications.stream().forEach(RamlSpecification::initialize);
                base.evaluate();
            }
        };
    }

    public RamlSpecificationsRule withSpecification(RamlSpecification specification) {
        if (this.specifications.stream().filter(s -> s.getName().equals(specification.getName())).findFirst().isPresent())
            throw new IllegalArgumentException(String.format("A specification for [ %s ] has already been provided", specification.getName()));

        this.specifications.add(specification);
        return this;
    }

    public RamlSpecificationsRule withSpecifications(RamlSpecification... specifications) {
        Arrays.asList(specifications).stream().forEach(this::withSpecification);
        return this;
    }

    public RamlSpecification get(String specificationName) {
        return this.specifications.stream()
                .filter(s -> s.getName().equals(specificationName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No specification exists for [ %s ]", specificationName)));
    }
}
