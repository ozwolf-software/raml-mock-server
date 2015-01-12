package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiResponse;
import net.ozwolf.mockserver.raml.internal.validator.body.ResponseBodyValidator;

import java.util.ArrayList;
import java.util.List;

public class ResponseValidator {
    private final ApiAction action;
    private final ApiResponse response;

    public ResponseValidator(ApiAction action, ApiResponse response) {
        this.action = action;
        this.response = response;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        errors.addAll(new ResponseBodyValidator(response, action).validate());

        return errors;
    }
}
