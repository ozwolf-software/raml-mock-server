package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.raml.model.parameter.AbstractParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestUriParametersValidator extends ParametersValidator {
    public RequestUriParametersValidator(ApiExpectation expectation) {
        super("Request URI Parameter", expectation);
    }

    @Override
    protected Map<String, ? extends AbstractParam> getParameters() {
        if (!expectation().hasValidResource())
            return new HashMap<>();

        return expectation().getResource().get().getResolvedUriParameters();
    }

    @Override
    protected List<String> getValues(String parameterName) {
        String value = expectation().getUriValueOf(parameterName);
        return Arrays.asList(value);
    }
}
