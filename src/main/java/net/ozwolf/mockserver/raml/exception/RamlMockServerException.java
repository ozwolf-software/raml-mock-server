package net.ozwolf.mockserver.raml.exception;

/**
 * # RAML Mock Server Exception
 *
 * General exception that is returned for known faults within the RAML Mock Server code.
 */
public class RamlMockServerException extends RuntimeException {
    public RamlMockServerException(String message) {
        super(message);
    }
}
