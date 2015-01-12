package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiParameter;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;
import org.mockserver.model.Parameter;

import java.util.List;

public class QueryParametersValidator extends ParametersValidator {
    private final static Parameter EMPTY_PARAMETER = new Parameter("");

    public QueryParametersValidator(ApiRequest request, ApiAction action) {
        super("Query Parameter", request, action);
    }

    @Override
    protected List<ApiParameter> getParameters() {
        return action().getQueryParameters();
    }

    @Override
    protected List<String> getValues(String parameterName) {
        return request().getQueryParameter(parameterName).orElse(EMPTY_PARAMETER).getValues();
    }
}
