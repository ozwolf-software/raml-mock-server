package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.ResponseObeyMode;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.body.ResponseBodyValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.ResponseHeaderParametersValidator;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ResponseValidator implements Validator {
    private final ApiExpectation expectation;
    private final ResponseObeyMode obeyMode;

    public ResponseValidator(ApiExpectation expectation,
                             ResponseObeyMode obeyMode) {
        this.expectation = expectation;
        this.obeyMode = obeyMode;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        boolean alwaysAllowed = obeyMode.isStatusCodeAllowed(expectation.getResponseStatusCode());
        if (!expectation.hasValidResponse() && !alwaysAllowed) {
            errors.addMessage("[ response ] No valid response specification exists for expectation.");
            return errors;
        }

        if (!expectation.hasValidResponse() && alwaysAllowed)
            return errors;

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
