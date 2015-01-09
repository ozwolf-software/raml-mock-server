package net.ozwolf.mockserver.raml;

import net.ozwolf.mockserver.raml.internal.validator.ExpectationValidator;
import org.mockserver.client.server.MockServerClient;
import org.raml.model.Raml;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;

public class RamlSpecification {
    private final RamlProvider provider;

    private Optional<Raml> raml;

    public RamlSpecification(RamlProvider provider) {
        this.provider = provider;
        this.raml = Optional.empty();
    }

    public void obeyedBy(MockServerClient client) {
        List<ExpectationError> errors = Arrays.asList(client.retrieveAsExpectations(null))
                .stream()
                .map(e -> new ExpectationValidator(e, raml()).validate())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        assertTrue(makeErrorMessageFor(errors), errors.isEmpty());
    }

    public void initialize() {
        Raml raml = provider.getRaml();
        this.raml = Optional.of(raml);
    }

    public void reset() {
        this.raml = Optional.empty();
    }

    private Raml raml() {
        return raml.orElseThrow(() -> new IllegalStateException("Specification has not been initialized and RAML is not present."));
    }

    private static String makeErrorMessageFor(List<ExpectationError> errors) {
        StringBuilder builder = new StringBuilder("Expectations did not meet specifications:");
        errors.stream().forEach(e -> builder.append(String.format("\n\t- %s", e.toString())));
        return builder.toString();
    }
}
