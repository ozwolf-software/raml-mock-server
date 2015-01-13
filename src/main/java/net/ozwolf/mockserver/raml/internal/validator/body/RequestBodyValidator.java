package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.BodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.raml.model.Action;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
            errors.addMessage("Request Body: Content expected but none provided.");
            return errors;
        }

        MediaType contentType = expectation.getRequestContentType();

        if (contentType.isWildcardType()) {
            errors.addMessage("Request Body: Validator currently does not support wildcard [ %s ] header values.", HttpHeaders.CONTENT_TYPE);
            return errors;
        }

        Optional<BodySpecification> body = expectation.getRequestBodySpecification();

        if (!body.isPresent()) {
            errors.addMessage("Request Body: Unrecognised content type of [ %s ].  Acceptable content types are: [ %s ]", contentType, expectation.getAllowedRequestContentTypes());
            return errors;
        }

        errors.combineWith(body.get().validate(requestBody.get()));
        return errors;
    }
}
