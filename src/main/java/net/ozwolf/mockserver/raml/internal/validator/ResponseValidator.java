package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.body.ResponseBodyValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.ResponseHeaderParametersValidator;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ResponseValidator implements Validator {
    private final ApiExpectation expectation;

    public ResponseValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        if (!expectation.hasValidResponse()) {
            errors.addMessage("[ response ] No valid response specification exists for expectation.");
            return errors;
        }

        getValidators().stream().forEach(v -> errors.combineWith(v.validate()));

        return errors;
    }

    protected List<Validator> getValidators() {
        return newArrayList(
                new ResponseHeaderParametersValidator(expectation),
                new ResponseBodyValidator(expectation)
        );
    }
}
