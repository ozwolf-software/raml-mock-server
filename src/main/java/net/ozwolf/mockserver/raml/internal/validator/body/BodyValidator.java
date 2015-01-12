package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiBody;
import net.ozwolf.mockserver.raml.internal.domain.ApiRequest;
import net.ozwolf.mockserver.raml.internal.validator.body.content.JsonContentValidator;
import org.apache.commons.lang.StringUtils;
import org.mockserver.model.Header;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BodyValidator {
    private final String bodyType;
    private final ApiRequest request;
    private final ApiAction action;

    public BodyValidator(String bodyType, ApiRequest request, ApiAction action) {
        this.bodyType = bodyType;
        this.request = request;
        this.action = action;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (!action.hasBody())
            return errors;

        String requestBody = request.getBodyAsString();

        if (action.hasBody() && StringUtils.isBlank(requestBody)) {
            errors.add(String.format("%s: Content expected but none provided", bodyType));
            return errors;
        }

        Header contentTypeHeader = request.getHeader(HttpHeaders.CONTENT_TYPE)
                .orElse(new Header(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD));

        MediaType mediaType = MediaType.valueOf(contentTypeHeader.getValues().get(0));

        if (mediaType.isWildcardType()) {
            errors.add(String.format("%s: Validator currently does not support wildcard [ %s ] header values.", bodyType, HttpHeaders.CONTENT_TYPE));
            return errors;
        }

        Optional<ApiBody> body = action.getBody(mediaType);

        if (!body.isPresent()) {
            errors.add(String.format("%s: Unrecognised content type of [ %s ].  Acceptable content types are: [ %s ]", bodyType, HttpHeaders.CONTENT_TYPE, action.getRequestContentTypes()));
            return errors;
        }

        ApiBody apiSpecificationBody = body.get();

        if (!apiSpecificationBody.getSchema().isPresent())
            return errors;

        String schema = apiSpecificationBody.getSchema().get();

        if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE))
            errors.addAll(new JsonContentValidator(bodyType).validate(schema, requestBody));

        return errors;
    }
}
