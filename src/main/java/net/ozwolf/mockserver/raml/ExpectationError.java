package net.ozwolf.mockserver.raml;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;

import java.util.List;

/**
 * # MockServer Expectation Error
 *
 * Designed to hold validation errors for a `MockServer` expectation that fails to meet the rules of a RAML API specification.
 */
public class ExpectationError {
    private final ApiExpectation expectation;
    private final ValidationErrors errors;

    public ExpectationError(ApiExpectation expectation, ValidationErrors errors) {
        this.expectation = expectation;
        this.errors = errors;
    }

    public String getUri() {
        return expectation.getUri();
    }

    public String getMethod() {
        return expectation.getMethod();
    }

    public List<String> getMessages() {
        return errors.getMessages();
    }
}
