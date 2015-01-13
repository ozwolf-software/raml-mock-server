package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.mockserver.model.Header;
import org.raml.model.parameter.AbstractParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeadersValidator extends ParametersValidator {
    private final static Header EMPTY_HEADER = new Header("");

    public ResponseHeadersValidator(ApiExpectation expectation) {
        super("Response Header", expectation);
    }

    @Override
    protected Map<String, ? extends AbstractParam> getParameters() {
        if (!expectation().hasValidResponse())
            return new HashMap<>();

        return expectation().getResponse().get().getHeaders();
    }

    @Override
    protected List<String> getValues(String parameterName) {
        return expectation().getResponseHeader(parameterName)
                .orElse(EMPTY_HEADER)
                .getValues();
    }
}
