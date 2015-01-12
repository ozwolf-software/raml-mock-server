package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiParameter;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;
import org.mockserver.model.Header;

import java.util.List;

public class HeaderParametersValidator extends ParametersValidator {
    private final static Header EMPTY_HEADER = new Header("");

    public HeaderParametersValidator(ApiRequest request,
                                     ApiAction action) {
        super("Header", request, action);
    }

    @Override
    protected List<ApiParameter> getParameters() {
        return action().getHeaders();
    }

    @Override
    protected List<String> getValues(String parameterName) {
        return request().getHeader(parameterName).orElse(EMPTY_HEADER).getValues();
    }

}
