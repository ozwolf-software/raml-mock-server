package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.raml.model.parameter.AbstractParam;

import java.util.List;
import java.util.Map;

public abstract class ParametersValidator implements Validator {
    private final String actionType;
    private final String parameterType;
    private final ApiExpectation expectation;

    protected ParametersValidator(String actionType, String parameterType, ApiExpectation expectation) {
        this.actionType = actionType;
        this.parameterType = parameterType;
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        if (!this.expectation.hasValidAction())
            throw new NoValidActionException(expectation);

        Map<String, ? extends AbstractParam> parameters = getParameters();
        if (parameters.isEmpty()) return errors;

        parameters.entrySet().stream()
                .forEach(p -> {
                    String name = p.getKey();
                    AbstractParam parameter = p.getValue();
                    List<String> values = getValues(p.getKey());

                    if (!parameter.isRequired() && values.isEmpty())
                        return;

                    if (parameter.isRequired() && values.isEmpty()) {
                        errors.addMessage("[ %s ] [ %s ] [ %s ] Parameter is compulsory but no value(s) provided.", actionType, parameterType, name);
                        return;
                    }

                    if (!parameter.isRepeat() && values.size() > 1)
                        errors.addMessage("[ %s ] [ %s ] [ %s ] Only one value allowed but multiple values provided.", actionType, parameterType, name);

                    values.stream()
                            .forEach(v -> {
                                if (!parameter.validate(v))
                                    errors.addMessage("[ %s ] [ %s ] [ %s ] Value of [ %s ] does not meet API requirements.", actionType, parameterType, name, v);
                            });
                });

        return errors;
    }

    protected ApiExpectation expectation() {
        return expectation;
    }

    protected abstract Map<String, ? extends AbstractParam> getParameters();

    protected abstract List<String> getValues(String parameterName);
}
