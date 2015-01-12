package net.ozwolf.mockserver.raml.internal.validator.security;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiParameter;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;
import net.ozwolf.mockserver.raml.internal.domain.ApiSecurity;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class SecurityValidator {
    private final ApiRequest request;
    private final ApiAction action;

    public SecurityValidator(ApiRequest request, ApiAction action) {
        this.request = request;
        this.action = action;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        List<ApiSecurity> schemes = action.getSecuritySchemes();

        if (schemes.isEmpty())
            return errors;

        List<ApiSecurity> usedSecurity = schemes.stream()
                .filter(s -> {
                    boolean headerProvided = s.getHeaders().stream()
                            .filter(h -> request.getHeader(h.getName()).isPresent())
                            .findFirst()
                            .isPresent();
                    boolean queryParameterProvided = s.getQueryParameters().stream()
                            .filter(q -> request.getQueryParameter(q.getName()).isPresent())
                            .findFirst()
                            .isPresent();

                    return headerProvided || queryParameterProvided;
                })
                .collect(toList());

        if (usedSecurity.isEmpty()) {
            errors.add(String.format("Security: Missing required security credentials.  Must use one of [ %s ]", action.getSecuritySchemeNames()));
            return errors;
        }

        usedSecurity.stream()
                .forEach(s -> {
                    s.getHeaders().stream()
                            .forEach(h -> {
                                Optional<Header> requestHeader = request.getHeader(h.getName());

                                if (requestHeader.isPresent())
                                    errors.addAll(verifyHeader(requestHeader.get(), h));
                            });

                    s.getQueryParameters().stream()
                            .forEach(p -> {
                                Optional<Parameter> queryParameter = request.getQueryParameter(p.getName());

                                if (queryParameter.isPresent())
                                    errors.addAll(verifyQueryParameter(queryParameter.get(), p));
                            });
                });

        return errors;
    }

    private List<String> verifyHeader(Header header, ApiParameter parameter) {
        List<String> errors = new ArrayList<>();

        if (header.getValues().size() > 1)
            errors.add(String.format("Security Header [ %s ]: Only one value allowed for security parameters but multiple found.", parameter.getName()));

        header.getValues().stream()
                .forEach(v -> {
                    if (!parameter.getParameter().validate(v))
                        errors.add(String.format("Security Header [ %s ]: Value of [ %s ] does not meet API requirements.", parameter.getName(), v));
                });

        return errors;
    }

    private List<String> verifyQueryParameter(Parameter parameter, ApiParameter apiParameter) {
        List<String> errors = new ArrayList<>();

        if (parameter.getValues().size() > 1)
            errors.add(String.format("Security Parameter [ %s ]: Only one value allowed for security paraemters but multiple found.", apiParameter.getName()));

        parameter.getValues().stream()
                .forEach(v -> {
                    if (!apiParameter.getParameter().validate(v))
                        errors.add(String.format("Security Parameter [ %s ]: Value of [ %s ] does not meet API requirements.", parameter.getName(), v));
                });

        return errors;
    }

}
