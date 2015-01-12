package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiAction;
import net.ozwolf.mockserver.raml.internal.domain.ApiBody;
import net.ozwolf.mockserver.raml.internal.domain.ApiResponse;
import org.raml.model.MimeType;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResponseValidator {
    private final ApiAction action;
    private final ApiResponse response;

    public ResponseValidator(ApiAction action, ApiResponse response) {
        this.action = action;
        this.response = response;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        Optional<ApiBody> responseSpecification = action.getResponseBodyFor(response.getStatusCode(), response.getContentType());

        if (!responseSpecification.isPresent()) {
            errors.add(String.format("Response: No response type of [ %s ] for status code [ %d ].", response.getContentType(), response.getStatusCode()));
            return errors;
        }



        return errors;
    }
}
