package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.body.RequestBodyValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.RequestHeaderParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.RequestQueryParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.RequestUriParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.security.RequestSecurityValidator;

import java.util.ArrayList;
import java.util.List;

public class RequestValidator implements Validator {
    private final ApiExpectation expectation;

    public RequestValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        if (!this.expectation.hasValidResource()) {
            errors.addMessage("Request: No resource matching URI [ %s ] found.", expectation.getUri());
            return errors;
        }

        if (!this.expectation.hasValidAction()) {
            errors.addMessage("Request: Resource for URI [ %s ] does not support method [ %s ].", expectation.getUri(), expectation.getMethod());
            return errors;
        }

        getValidators().stream().forEach(v -> errors.combineWith(v.validate()));

        return errors;
    }

    protected List<Validator> getValidators() {
        List<Validator> validators = new ArrayList<>();
        validators.add(new RequestSecurityValidator(expectation));
        validators.add(new RequestUriParametersValidator(expectation));
        validators.add(new RequestHeaderParametersValidator(expectation));
        validators.add(new RequestQueryParametersValidator(expectation));
        validators.add(new RequestBodyValidator(expectation));
        return validators;
    }
}
