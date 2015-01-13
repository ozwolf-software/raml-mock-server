package net.ozwolf.mockserver.raml.exception;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;

public class NoValidActionException extends RamlMockServerException {
    private final static String MESSAGE = "Expectation [ %s %s ] has no valid matching action specification.";

    public NoValidActionException(ApiExpectation expectation) {
        super(String.format(MESSAGE, expectation.getMethod(), expectation.getUri()));
    }
}
