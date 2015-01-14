package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.mockserver.model.Header;
import org.raml.model.parameter.AbstractParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeaderParametersValidator extends ParametersValidator {
    private final static Header EMPTY_HEADER = new Header("");

    public ResponseHeaderParametersValidator(ApiExpectation expectation) {
        super("response", "header", expectation);
    }

    @Override
    protected Map<String, ? extends AbstractParam> getParameters() {
        if (!expectation().hasValidResponse())
            return new HashMap<>();

        Map<String, org.raml.model.parameter.Header> headers = expectation().getResponse().get().getHeaders();
        return headers == null ? new HashMap<>() : headers;
    }

    @Override
    protected List<String> getValues(String parameterName) {
        return expectation().getResponseHeader(parameterName)
                .orElse(EMPTY_HEADER)
                .getValues();
    }
}
