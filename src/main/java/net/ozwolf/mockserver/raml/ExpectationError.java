package net.ozwolf.mockserver.raml;

import org.apache.commons.lang.StringUtils;
import org.mockserver.mock.Expectation;

import java.util.ArrayList;
import java.util.List;

public class ExpectationError {
    private final Expectation expectation;
    private final List<String> errors;

    public ExpectationError(Expectation expectation) {
        this.expectation = expectation;
        this.errors = new ArrayList<>();
    }

    public ExpectationError withError(String error){
        this.errors.add(error);
        return this;
    }

    public boolean isInError() {
        return !this.errors.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s %s: [ %s ]", expectation.getHttpRequest().getMethod(), expectation.getHttpRequest().getPath(), StringUtils.join(this.errors, " ] [ "));
    }
}
