package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.ExpectationError;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ExpectationValidator {
    private final ApiExpectation expectation;
    private final List<Validator> validators;

    public ExpectationValidator(ApiExpectation expectation,
                                Validator... validators) {
        this.expectation = expectation;
        this.validators = Arrays.asList(validators);
    }

    public Optional<ExpectationError> validate() {
        ValidationErrors errors = new ValidationErrors();

        validators.stream().forEach(v -> errors.combineWith(v.validate()));

        if (!errors.isInError())
            return empty();

        return of(new ExpectationError(expectation, errors));
    }
}
