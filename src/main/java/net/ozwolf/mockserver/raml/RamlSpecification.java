package net.ozwolf.mockserver.raml;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ApiSpecification;
import net.ozwolf.mockserver.raml.internal.validator.ExpectationValidator;
import org.mockserver.client.server.MockServerClient;
import org.raml.model.Raml;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;

public abstract class RamlSpecification {
    private final String name;

    private Optional<Raml> raml;

    public RamlSpecification(String name) {
        this.name = name;
        this.raml = Optional.empty();
    }

    public String getName() {
        return name;
    }

    public void initialize() {
        this.raml = Optional.of(getRaml());
    }

    public void obeyedBy(MockServerClient client) {
        Raml raml = this.raml.orElseThrow(() -> new IllegalStateException(String.format("[ %s ] specification has not been initialized", name)));
        List<ExpectationError> errors = Arrays.asList(client.retrieveAsExpectations(null))
                .stream()
                .map(e -> {
                    ApiSpecification specification = new ApiSpecification(raml);
                    ApiExpectation expectation = new ApiExpectation(specification, e);
                    return new ExpectationValidator(expectation).validate();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        assertTrue(makeErrorMessageFrom(errors), errors.isEmpty());
    }

    private static String makeErrorMessageFrom(List<ExpectationError> errors) {
        StringBuilder builder = new StringBuilder("Expectation did not meet RAML specification requirements:");
        errors.stream()
                .forEach(e -> builder.append(String.format("\n\t- %s", e)));
        return builder.toString();
    }

    protected abstract Raml getRaml();
}
