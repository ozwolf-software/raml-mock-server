package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.BodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.apache.commons.lang.StringUtils;
import org.raml.model.MimeType;
import org.raml.model.Response;

import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Optional;

public class ResponseBodyValidator implements Validator {
    private final ApiExpectation expectation;

    public ResponseBodyValidator(ApiExpectation expectation) {
        this.expectation = expectation;
    }

    @Override
    public ValidationErrors validate() {
        ValidationErrors errors = new ValidationErrors();

        if (!expectation.hasValidAction())
            throw new NoValidActionException(expectation);

        Integer statusCode = expectation.getResponseStatusCode();
        MediaType contentType = expectation.getResponseContentType();

        Optional<Response> response = expectation.getResponse();

        if (!response.isPresent()) {
            errors.addMessage("[ response ] [ %d ] [ %s ] No response body specification exists.", statusCode, contentType);
            return errors;
        }

        Optional<BodySpecification> specification = expectation.getResponseBodySpecification();
        Optional<String> responseBody = expectation.getResponseBody();

        if (!specification.isPresent() && responseBody.isPresent()) {
            errors.addMessage("[ response ] [ %d ] [ %s ] No response body specification exists for this content type.  Acceptable content types are [ %s ].", statusCode, contentType, getAllowedContentTypes(response.get().getBody()));
            return errors;
        }

        if (!responseBody.isPresent()) {
            errors.addMessage("[ response ] [ %d ] [ %s ] Has an expected response body but none returned.", statusCode, contentType);
            return errors;
        }

        errors.combineWith(specification.get().validate(responseBody.get()));

        return errors;
    }

    private String getAllowedContentTypes(Map<String, MimeType> contentTypes){
        if (contentTypes == null)
            return "n/a";

        return StringUtils.join(contentTypes.keySet(), ", ");
    }
}
