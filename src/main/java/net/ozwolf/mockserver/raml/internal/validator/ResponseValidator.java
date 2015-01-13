package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.body.ResponseBodyValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.ResponseHeadersValidator;

import java.util.ArrayList;
import java.util.List;

public class ResponseValidator implements Validator {
    private final ApiExpectation expectation;

    public ResponseValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        if (!expectation.hasValidResponse()) {
            errors.addMessage("Response [ %d %s ]: Specification does not allow response of this content type for this status code.", expectation.getResponseStatusCode(), expectation.getResponseContentType());
            return errors;
        }

        getValidators().stream().forEach(v -> errors.combineWith(v.validate()));

        return errors;
    }

    protected List<Validator> getValidators() {
        List<Validator> validators = new ArrayList<>();
        validators.add(new ResponseHeadersValidator(expectation));
        validators.add(new ResponseBodyValidator(expectation));
        return validators;
    }
}
