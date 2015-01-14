package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.BodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.apache.commons.lang.StringUtils;
import org.raml.model.Action;
import org.raml.model.MimeType;

import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Optional;

public class RequestBodyValidator implements Validator {
    private final ApiExpectation expectation;

    public RequestBodyValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        Action action = expectation.getAction()
                .orElseThrow(() -> new NoValidActionException(expectation));

        if (!action.hasBody())
            return errors;

        Optional<String> requestBody = expectation.getRequestBody();

        if (action.hasBody() && !requestBody.isPresent()) {
            errors.addMessage("[ request ] Has an expected request body but none provided.");
            return errors;
        }

        MediaType contentType = expectation.getRequestContentType();

        if (contentType.isWildcardType()) {
            errors.addMessage("[ request ] Wildcard or no content type provided in request.");
            return errors;
        }

        Optional<BodySpecification> body = expectation.getRequestBodySpecification();

        if (!body.isPresent()) {
            errors.addMessage("[ request ] No request specification exists for [ %s ].  Acceptable content types are [ %s ]", contentType, getAllowedContentTypes(action.getBody()));
            return errors;
        }

        errors.combineWith(body.get().validate(requestBody.get()));
        return errors;
    }

    private String getAllowedContentTypes(Map<String, MimeType> contentTypes) {
        if (contentTypes == null)
            return "n/a";

        return StringUtils.join(contentTypes.keySet(), ", ");
    }
}
