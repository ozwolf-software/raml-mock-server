package net.ozwolf.mockserver.raml.internal.validator;

import com.google.common.annotations.VisibleForTesting;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.body.RequestBodyValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.RequestHeaderParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.RequestQueryParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.RequestUriParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.security.RequestSecurityValidator;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class RequestValidator implements Validator {
    private final ApiExpectation expectation;

    public RequestValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        if (!this.expectation.hasValidResource()) {
            errors.addMessage("[ request ] No resource matching for request found.");
            return errors;
        }

        if (!this.expectation.hasValidAction()) {
            errors.addMessage("[ request ] Resource for does not support method.");
            return errors;
        }

        getValidators().stream().forEach(v -> errors.combineWith(v.validate()));

        return errors;
    }

    @VisibleForTesting
    protected List<Validator> getValidators() {
        return newArrayList(
                new RequestSecurityValidator(expectation),
                new RequestUriParametersValidator(expectation),
                new RequestHeaderParametersValidator(expectation),
                new RequestQueryParametersValidator(expectation),
                new RequestBodyValidator(expectation)
        );
    }
}
