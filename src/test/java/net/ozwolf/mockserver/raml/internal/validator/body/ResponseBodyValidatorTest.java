package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.domain.body.DefaultBodySpecification;
import org.junit.Before;
import org.junit.Test;
import org.raml.model.MimeType;
import org.raml.model.Response;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseBodyValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);
    private final Response response = mock(Response.class);

    @Before
    public void setUp() {
        Map<String, MimeType> contentTypes = getContentTypes();
        when(response.getBody()).thenReturn(contentTypes);
        when(expectation.getResponse()).thenReturn(Optional.of(response));
        when(expectation.getUri()).thenReturn("/hello/John/greetings");
        when(expectation.getMethod()).thenReturn("PUT");
        when(expectation.getResponseStatusCode()).thenReturn(200);
        when(expectation.getResponseContentType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(expectation.hasValidAction()).thenReturn(true);
        when(expectation.hasValidResponse()).thenReturn(true);
    }

    @Test(expected = NoValidActionException.class)
    public void shouldThrowNoValidActionException() {
        when(expectation.hasValidAction()).thenReturn(false);

        new ResponseBodyValidator(expectation).validate();
    }

    @Test
    public void shouldRaiseErrorIfNoResponseFoundForExpectation() {
        when(expectation.getResponse()).thenReturn(Optional.empty());

        ValidationErrors errors = new ResponseBodyValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ response ] [ 200 ] [ application/json ] No response body specification exists.");
    }

    @Test
    public void shouldThrowErrorIfNoBodySpecificationFoundForResponseCodeAndContentType() {
        when(expectation.getResponseBodySpecification()).thenReturn(Optional.empty());
        when(expectation.getResponseBody()).thenReturn(Optional.of("{\"greeting\":\"Hello John!\"}"));

        ValidationErrors errors = new ResponseBodyValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ response ] [ 200 ] [ application/json ] No response body specification exists for this content type.  Acceptable content types are [ application/json, text/plain ].");
    }

    @Test
    public void shouldReturnErrorIfNoResponseBodyIsProvided() {
        when(expectation.getResponseBodySpecification()).thenReturn(Optional.of(new DefaultBodySpecification(MediaType.APPLICATION_JSON_TYPE)));
        when(expectation.getResponseBody()).thenReturn(Optional.empty());

        ValidationErrors errors = new ResponseBodyValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ response ] [ 200 ] [ application/json ] Has an expected response body but none returned.");
    }

    private Map<String, MimeType> getContentTypes() {
        Map<String, MimeType> contentTypes = new HashMap<>();

        MimeType mimeType = mock(MimeType.class);

        contentTypes.put(MediaType.APPLICATION_JSON, mimeType);
        contentTypes.put(MediaType.TEXT_PLAIN, mimeType);

        return contentTypes;
    }
}