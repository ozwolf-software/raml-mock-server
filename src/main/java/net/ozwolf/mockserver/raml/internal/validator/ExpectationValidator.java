package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.ExpectationError;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ExpectationValidator {
    private final ApiExpectation expectation;

    public ExpectationValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    public Optional<ExpectationError> validate() {
        ValidationErrors errors = new ValidationErrors();

        errors.combineWith(new RequestValidator(expectation).validate());
        errors.combineWith(new ResponseValidator(expectation).validate());

        if (!errors.isInError())
            return empty();

        return of(new ExpectationError(expectation, errors));
    }
}
