package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiBody;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;
import net.ozwolf.mockserver.raml.internal.validator.body.content.JsonContentValidator;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestBodyValidator {
    private final ApiRequest request;
    private final ApiAction action;

    public RequestBodyValidator(ApiRequest request, ApiAction action) {
        this.request = request;
        this.action = action;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (!action.hasBody()) return errors;

        Optional<String> requestBody = request.getBody();

        if (action.hasBody() && !requestBody.isPresent()) {
            errors.add(String.format("Request Body: Content expected but none provided"));
            return errors;
        }

        MediaType contentType = request.getContentType();
        Optional<ApiBody> body = action.getRequestBody(contentType);

        if (contentType.isWildcardType()) {
            errors.add(String.format("Request Body: Validator currently does not support wildcard [ %s ] header values.", HttpHeaders.CONTENT_TYPE));
            return errors;
        }

        if (!body.isPresent()) {
            errors.add(String.format("Request Body: Unrecognised content type of [ %s ].  Acceptable content types are: [ %s ]", HttpHeaders.CONTENT_TYPE, action.getRequestContentTypes()));
            return errors;
        }

        Optional<String> schema = body.get().getSchema();

        if (!schema.isPresent())
            return errors;

        if (contentType.isCompatible(MediaType.APPLICATION_JSON_TYPE))
            errors.addAll(new JsonContentValidator("Request Body").validate(schema.get(), requestBody.get()));

        return errors;
    }
}
