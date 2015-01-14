package net.ozwolf.mockserver.raml.internal.validator.security;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.apache.commons.lang.StringUtils;
import org.mockserver.model.Header;
import org.mockserver.model.KeyToMultiValue;
import org.mockserver.model.Parameter;
import org.raml.model.SecurityScheme;
import org.raml.model.SecuritySchemeDescriptor;
import org.raml.model.parameter.AbstractParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestSecurityValidator implements Validator {
    private final ApiExpectation expectation;

    public RequestSecurityValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        Map<String, SecurityScheme> security = expectation.getSecuritySpecification();

        if (security.isEmpty()) return errors;

        Map<String, SecurityScheme> usedSecurity = new HashMap<>();
        security.entrySet().stream()
                .filter(s -> {
                    SecuritySchemeDescriptor descriptor = s.getValue().getDescribedBy();
                    boolean headerProvided = descriptor.getHeaders().keySet().stream()
                            .filter(k -> expectation.getRequestHeader(k).isPresent())
                            .findFirst()
                            .isPresent();
                    boolean queryParameterProvided = descriptor.getQueryParameters().keySet().stream()
                            .filter(k -> expectation.getQueryParameter(k).isPresent())
                            .findFirst()
                            .isPresent();

                    return headerProvided || queryParameterProvided;
                })
                .forEach(e -> usedSecurity.put(e.getKey(), e.getValue()));

        if (usedSecurity.isEmpty()) {
            errors.addMessage("[ request ] [ security ] Missing required security credentials.  Must use one of [ %s ].", getAllowedSchemes(security));
            return errors;
        }

        usedSecurity.entrySet().stream()
                .forEach(s -> {
                    SecuritySchemeDescriptor descriptor = s.getValue().getDescribedBy();

                    descriptor.getHeaders().entrySet().stream()
                            .forEach(h -> {
                                Optional<Header> requestHeader = expectation.getRequestHeader(h.getKey());

                                if (requestHeader.isPresent())
                                    errors.combineWith(verifyParameters("header", s.getKey(), requestHeader.get(), h.getValue()));
                            });

                    descriptor.getQueryParameters().entrySet().stream()
                            .forEach(p -> {
                                Optional<Parameter> queryParameter = expectation.getQueryParameter(p.getKey());

                                if (queryParameter.isPresent())
                                    errors.combineWith(verifyParameters("query", s.getKey(), queryParameter.get(), p.getValue()));
                            });
                });

        return errors;
    }

    private ValidationErrors verifyParameters(String parameterType,
                                              String name,
                                              KeyToMultiValue parameter,
                                              AbstractParam specification) {
        ValidationErrors errors = new ValidationErrors();

        if (parameter.getValues().size() > 1)
            errors.addMessage("[ security ] [ %s ] [ %s ] Only one value allowed for security parameters but multiple found.", parameterType, name);

        parameter.getValues().stream()
                .forEach(v -> {
                    if (!specification.validate(v))
                        errors.addMessage("[ security ] [ %s ] [ %s ] Value of [ %s ] does not meet API requirements.", parameterType, name, v);
                });

        return errors;
    }

    private String getAllowedSchemes(Map<String, SecurityScheme> security) {
        return StringUtils.join(security.keySet(), ", ");
    }
}
