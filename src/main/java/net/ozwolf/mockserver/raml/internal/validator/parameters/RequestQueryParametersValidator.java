package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.mockserver.model.Parameter;
import org.raml.model.parameter.AbstractParam;
import org.raml.model.parameter.QueryParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestQueryParametersValidator extends ParametersValidator {
    private final static Parameter EMPTY_PARAMETER = new Parameter("");

    public RequestQueryParametersValidator(ApiExpectation expectation) {
        super("request", "query", expectation);
    }

    @Override
    protected Map<String, ? extends AbstractParam> getParameters() {
        if (!expectation().hasValidAction())
            return new HashMap<>();

        Map<String, QueryParameter> parameters = expectation().getAction().get().getQueryParameters();
        return parameters == null ? new HashMap<>() : parameters;
    }

    @Override
    protected List<String> getValues(String parameterName) {
        return expectation().getQueryParameter(parameterName)
                .orElse(EMPTY_PARAMETER)
                .getValues();
    }
}
