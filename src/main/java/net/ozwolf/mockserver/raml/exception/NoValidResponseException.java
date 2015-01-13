package net.ozwolf.mockserver.raml.exception;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;

public class NoValidResponseException extends RamlMockServerException {
    private final static String MESSAGE = "Expectation [ %s %s ] [ %d ] has no valid matching response specification.";

    public NoValidResponseException(ApiExpectation expectation) {
        super(String.format(MESSAGE, expectation.getMethod(), expectation.getUri(), expectation.getResponseStatusCode()));
    }
}
