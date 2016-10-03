package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.apache.commons.lang.StringUtils;
import org.mockserver.model.NottableString;
import org.raml.model.parameter.AbstractParam;
import org.raml.model.parameter.UriParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class RequestUriParametersValidator extends ParametersValidator {
    public RequestUriParametersValidator(ApiExpectation expectation) {
        super("request", "uri", expectation);
    }

    @Override
    protected Map<String, ? extends AbstractParam> getParameters() {
        if (!expectation().hasValidResource())
            return new HashMap<>();

        Map<String, UriParameter> parameters = expectation().getResource().get().getResolvedUriParameters();
        return parameters == null ? new HashMap<>() : parameters;
    }

    @Override
    protected List<NottableString> getValues(String parameterName) {
        String value = expectation().getUriValueOf(parameterName);
        if (StringUtils.isBlank(value))
            return new ArrayList<>();
        return newArrayList(NottableString.string(value));
    }
}
