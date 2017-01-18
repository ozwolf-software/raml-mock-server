package net.ozwolf.mockserver.raml.internal.domain;

import org.junit.Test;
import org.raml.model.*;
import org.raml.parser.visitor.RamlDocumentBuilder;

import javax.ws.rs.core.MediaType;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ApiSpecificationTest {
    private final static ApiSpecification SPECIFICATION = new ApiSpecification(new RamlDocumentBuilder().build("apispecs-test/apispecs.raml"));

    @Test
    public void shouldReturnResourceForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.isExpectationFor(argThat(r -> r.getUri().equalsIgnoreCase("/hello/{name}")))).thenReturn(true);

        Optional<Resource> resource = SPECIFICATION.getResourceFor(expectation);

        assertThat(resource.isPresent()).isTrue();
        assertThat(resource.get().getUri()).isEqualTo("/hello/{name}");
    }

    @Test
    public void shouldReturnActionForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.getMethod()).thenReturn("GET");
        when(expectation.isExpectationFor(argThat(r -> r.getUri().equalsIgnoreCase("/hello/{name}")))).thenReturn(true);

        Optional<Action> action = SPECIFICATION.getActionFor(expectation);

        assertThat(action.isPresent()).isTrue();
        assertThat(action.get().getType()).isEqualTo(ActionType.GET);
    }

    @Test
    public void shouldReturnResponseForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.getMethod()).thenReturn("GET");
        when(expectation.getResponseStatusCode()).thenReturn(200);
        when(expectation.getResponseContentType()).thenReturn(MediaType.TEXT_PLAIN_TYPE);
        when(expectation.isExpectationFor(argThat(r -> r.getUri().equalsIgnoreCase("/hello/{name}")))).thenReturn(true);

        Optional<Response> response = SPECIFICATION.getResponseFor(expectation);

        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().getDescription()).isEqualTo("retrieved the greeting successfully");
    }

    @Test
    public void shouldReturnRequestBodySpecification() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John/greetings");
        when(expectation.getMethod()).thenReturn("PUT");
        when(expectation.getRequestContentType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(expectation.isExpectationFor(argThat(r -> r.getUri().equals("/hello/{name}/greetings")))).thenReturn(true);

        Optional<MimeType> body = SPECIFICATION.getRequestBodyFor(expectation);

        assertThat(body.isPresent()).isTrue();
        assertThat(body.get().getType()).isEqualTo("application/json");
    }

    @Test
    public void shouldReturnSpecifiedSecurityScheme() {
        assertThat(SPECIFICATION.getSecurityScheme(new SecurityReference("my-token")).getDescription()).isEqualTo("basic custom token");
    }

}