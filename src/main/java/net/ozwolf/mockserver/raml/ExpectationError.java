package net.ozwolf.mockserver.raml;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ExpectationError {
    private final ApiExpectation expectation;
    private final ValidationErrors errors;

    public ExpectationError(ApiExpectation expectation, ValidationErrors errors) {
        this.expectation = expectation;
        this.errors = errors;
    }

    public List<String> getMessages() {
        return errors.getMessages();
    }

    @Override
    public String toString() {
        return String.format("%s %s: [ %s ]", expectation.getMethod(), expectation.getUri(), StringUtils.join(this.errors.getMessages(), " ] [ "));
    }
}
