package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;
import net.ozwolf.mockserver.raml.internal.validator.body.BodyValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.HeaderParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.QueryParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.parameters.UriParametersValidator;
import net.ozwolf.mockserver.raml.internal.validator.security.SecurityValidator;
import org.mockserver.model.HttpRequest;
import org.raml.model.Action;
import org.raml.model.SecurityScheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestValidator {


    private final ApiAction action;
    private final ApiRequest request;

    public RequestValidator(ApiAction action,
                            ApiRequest request) {
        this.action = action;
        this.request = request;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        errors.addAll(new SecurityValidator(request, action).validate());
        errors.addAll(new HeaderParametersValidator(request, action).validate());
        errors.addAll(new UriParametersValidator(request, action).validate());
        errors.addAll(new QueryParametersValidator(request, action).validate());
        errors.addAll(new BodyValidator("Request", request, action).validate());

        return errors;
    }

}