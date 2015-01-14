package net.ozwolf.mockserver.raml.exception;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;

/**
 * # No Valid Action Exception
 *
 * Exception that is thrown whenever an method is called that requires a RAML action to match a `MockServer` expectation but one doesn't exist.
 */
public class NoValidActionException extends RamlMockServerException {
    private final static String MESSAGE = "Expectation [ %s %s ] has no valid matching action specification.";

    public NoValidActionException(ApiExpectation expectation) {
        super(String.format(MESSAGE, expectation.getMethod(), expectation.getUri()));
    }
}
