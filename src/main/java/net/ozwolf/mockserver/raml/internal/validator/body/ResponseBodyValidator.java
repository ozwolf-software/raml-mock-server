package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiBody;
import net.ozwolf.mockserver.raml.internal.domain.ApiResponse;
import net.ozwolf.mockserver.raml.internal.validator.body.content.JsonContentValidator;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResponseBodyValidator {
    private final ApiResponse response;
    private final ApiAction action;

    public ResponseBodyValidator(ApiResponse response, ApiAction action) {
        this.response = response;
        this.action = action;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        Integer statusCode = response.getStatusCode();
        MediaType contentType = response.getContentType();
        Optional<ApiBody> body = action.getResponseBody(statusCode, contentType);
        Optional<String> responseBody = response.getBody();

        if (!body.isPresent()) {
            errors.add(String.format("Response [ %d %s ]: No response specification exists.", statusCode, contentType));
            return errors;
        }

        Optional<String> schema = body.get().getSchema();

        if (!body.get().getSchema().isPresent())
            return errors;

        if (!responseBody.isPresent()) {
            errors.add(String.format("Response [ %d %s ]: Has an expected response but none returned.", statusCode, contentType));
            return errors;
        }

        if (contentType.isCompatible(MediaType.APPLICATION_JSON_TYPE))
            errors.addAll(new JsonContentValidator("Request Body").validate(schema.get(), responseBody.get()));

        return errors;
    }
}
