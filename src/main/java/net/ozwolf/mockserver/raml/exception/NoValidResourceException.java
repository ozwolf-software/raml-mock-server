package net.ozwolf.mockserver.raml.exception;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;

/**
 * # No Valid Resource Exception
 *
 * Exception that is thrown whenever an method is called that requires a RAML resource to match a `MockServer` expectation but one doesn't exist.
 */
public class NoValidResourceException extends RamlMockServerException {
    private final static String MESSAGE = "Expectation [ %s %s ] has no valid matching resource specification.";

    public NoValidResourceException(ApiExpectation expectation) {
        super(String.format(MESSAGE, expectation.getMethod(), expectation.getUri()));
    }
}
