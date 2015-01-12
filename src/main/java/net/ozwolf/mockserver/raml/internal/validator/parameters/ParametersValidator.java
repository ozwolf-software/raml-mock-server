package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiParameter;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;
import org.raml.model.parameter.AbstractParam;

import java.util.ArrayList;
import java.util.List;

public abstract class ParametersValidator {
    private final String parameterType;
    private final ApiRequest request;
    private final ApiAction action;

    protected ParametersValidator(String parameterType,
                                  ApiRequest request,
                                  ApiAction action) {
        this.parameterType = parameterType;
        this.request = request;
        this.action = action;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        List<ApiParameter> parameters = getParameters();
        if (parameters == null || parameters.isEmpty())
            return errors;

        parameters.stream().forEach(p -> {
            List<String> values = getValues(p.getName());

            AbstractParam parameter = p.getParameter();

            if (!parameter.isRequired() && values.isEmpty())
                return;

            if (parameter.isRequired() && values.isEmpty()) {
                errors.add(String.format("%s [ %s ]: Parameter is compulsory but no value(s) provided.", parameterType, p.getName()));
                return;
            }

            if (!parameter.isRepeat() && values.size() > 1)
                errors.add(String.format("%s [ %s ]: Only one value allowed but multiple values provided.", parameterType, p.getName()));

            values.stream()
                    .forEach(v -> {
                        if (!parameter.validate(v))
                            errors.add(String.format("%s [ %s ]: Value of [ %s ] does not meet API requirements.", parameterType, p.getName(), v));
                    });
        });

        return errors;
    }

    protected ApiRequest request() {
        return request;
    }

    protected ApiAction action() {
        return action;
    }

    protected abstract List<ApiParameter> getParameters();

    protected abstract List<String> getValues(String parameterName);
}
