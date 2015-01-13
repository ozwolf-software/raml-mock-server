package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.mockserver.model.Header;
import org.raml.model.parameter.AbstractParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaderParametersValidator extends ParametersValidator {
    private final static Header EMPTY_HEADER = new Header("");

    public RequestHeaderParametersValidator(ApiExpectation expectation) {
        super("Request Header", expectation);
    }

    @Override
    protected Map<String, ? extends AbstractParam> getParameters() {
        if (!expectation().hasValidAction())
            return new HashMap<>();

        return expectation().getAction().get().getHeaders();
    }

    @Override
    protected List<String> getValues(String parameterName) {
        return expectation().getRequestHeader(parameterName).orElse(EMPTY_HEADER).getValues();
    }

}
