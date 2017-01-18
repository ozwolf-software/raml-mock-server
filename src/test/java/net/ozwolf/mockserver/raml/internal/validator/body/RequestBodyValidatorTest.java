package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.BodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.domain.body.JsonBodySpecification;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.junit.Before;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.MimeType;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.ozwolf.mockserver.raml.util.Fixtures.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestBodyValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);
    private final Action action = mock(Action.class);

    @Before
    public void setUp() {
        Map<String, MimeType> contentTypes = getContentTypes();
        when(action.getBody()).thenReturn(contentTypes);
        when(expectation.getAction()).thenReturn(Optional.of(action));
        when(expectation.getUri()).thenReturn("/hello/John/greetings");
        when(expectation.getMethod()).thenReturn("PUT");
    }

    @Test(expected = NoValidActionException.class)
    public void shouldThrowIllegalArgumentExceptionWhenNoActionFound() {
        when(expectation.getAction()).thenReturn(Optional.empty());

        new RequestBodyValidator(expectation).validate();
    }

    @Test
    public void shouldReturnZeroErrorsWhenActionHasNoBodySpecifiedForRequest() {
        when(action.hasBody()).thenReturn(false);

        Validator validator = new RequestBodyValidator(expectation);

        assertThat(validator.validate().isInError()).isFalse();
    }

    @Test
    public void shouldReturnMissingBodyErrorWhenActionRequiresRequestBodyAndRequestDoesNotHaveIt() {
        when(action.hasBody()).thenReturn(true);
        when(expectation.getRequestBody()).thenReturn(Optional.empty());

        Validator validator = new RequestBodyValidator(expectation);
        ValidationErrors errors = validator.validate();
        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] Has an expected request body but none provided.");
    }

    @Test
    public void shouldReturnWildcardContentTypeErrorWhenRequestReturnsWildcard() throws IOException {
        when(action.hasBody()).thenReturn(true);
        when(expectation.getRequestBody()).thenReturn(Optional.of(fixture("apispecs-test/examples/greeting-response.json")));
        when(expectation.getRequestContentType()).thenReturn(MediaType.WILDCARD_TYPE);

        Validator validator = new RequestBodyValidator(expectation);
        ValidationErrors errors = validator.validate();
        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] Wildcard or no content type provided in request.");
    }

    @Test
    public void shouldReturnErrorWhenRequestedContentTypeResponseWasNotRecognisedByApiSpecification() throws IOException {
        when(action.hasBody()).thenReturn(true);
        when(expectation.getRequestBody()).thenReturn(Optional.of(fixture("apispecs-test/examples/greeting-response.json")));
        when(expectation.getRequestContentType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(expectation.getRequestBodySpecification()).thenReturn(Optional.empty());

        Validator validator = new RequestBodyValidator(expectation);
        ValidationErrors errors = validator.validate();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] No request specification exists for [ application/json ].  Acceptable content types are [ application/xml, text/plain ]");
    }

    @Test
    public void shouldIncludeErrorsFromBodyValidation() throws IOException {
        String requestBody = fixture("apispecs-test/examples/greeting-response.json");

        when(action.hasBody()).thenReturn(true);
        when(expectation.getRequestBody()).thenReturn(Optional.of(requestBody));
        when(expectation.getRequestContentType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);

        BodySpecification body = mock(JsonBodySpecification.class);
        ValidationErrors bodyErrors = new ValidationErrors();
        bodyErrors.addMessage("Request Body: The content did not match the provided schema.");
        when(body.validate(requestBody)).thenReturn(bodyErrors);

        when(expectation.getRequestBodySpecification()).thenReturn(Optional.of(body));

        Validator validator = new RequestBodyValidator(expectation);
        ValidationErrors errors = validator.validate();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("Request Body: The content did not match the provided schema.");
    }

    private Map<String, MimeType> getContentTypes() {
        Map<String, MimeType> contentTypes = new HashMap<>();

        MimeType mimeType = mock(MimeType.class);

        contentTypes.put(MediaType.APPLICATION_XML, mimeType);
        contentTypes.put(MediaType.TEXT_PLAIN, mimeType);

        return contentTypes;
    }
}