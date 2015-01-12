package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiParameter;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;

import java.util.ArrayList;
import java.util.List;

public class UriParametersValidator extends ParametersValidator {
    public UriParametersValidator(ApiRequest request, ApiAction action) {
        super("URI Parameter", request, action);
    }

    @Override
    protected List<ApiParameter> getParameters() {
        return action().getUriParameters();
    }

    @Override
    protected List<String> getValues(String parameterName) {
        List<String> uriParts = action().getUriParts();
        List<String> values = new ArrayList<>();

        if (!request().matchesUriSize(uriParts.size()))
            throw new IllegalStateException(String.format("Path of [ %s ] does not match resource path of [ %s ]", request().getPath(), action().getUri()));

        int valueIndex = uriParts.indexOf(String.format("{%s}", parameterName));

        if (valueIndex < 0)
            throw new IllegalArgumentException(String.format("No parameter of [ %s ] could be found in resource path of [ %s ]", parameterName, action().getUri()));

        values.add(request().getUriParameter(valueIndex).get());

        return values;
    }
}
