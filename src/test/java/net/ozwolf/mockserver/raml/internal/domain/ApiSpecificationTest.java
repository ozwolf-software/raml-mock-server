package net.ozwolf.mockserver.raml.internal.domain;

import org.junit.Test;
import org.raml.model.*;
import org.raml.parser.visitor.RamlDocumentBuilder;

import javax.ws.rs.core.MediaType;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiSpecificationTest {
    private final static ApiSpecification SPECIFICATION = new ApiSpecification(new RamlDocumentBuilder().build("apispecs-test/apispecs.raml"));

    @Test
    public void shouldReturnResourceForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");

        Optional<Resource> resource = SPECIFICATION.getResourceFor(expectation);

        assertThat(resource.isPresent(), is(true));
        assertThat(resource.get().getUri(), is("/hello/{name}"));
    }

    @Test
    public void shouldReturnActionForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.getMethod()).thenReturn("GET");

        Optional<Action> action = SPECIFICATION.getActionFor(expectation);

        assertThat(action.isPresent(), is(true));
        assertThat(action.get().getType(), is(ActionType.GET));
    }

    @Test
    public void shouldReturnResponseForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.getMethod()).thenReturn("GET");
        when(expectation.getResponseStatusCode()).thenReturn(200);
        when(expectation.getResponseContentType()).thenReturn(MediaType.TEXT_PLAIN_TYPE);

        Optional<Response> response = SPECIFICATION.getResponseFor(expectation);

        assertThat(response.isPresent(), is(true));
        assertThat(response.get().getDescription(), is("retrieved the greeting successfully"));
    }

    @Test
    public void shouldReturnRequestBodySpecification() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John/greetings");
        when(expectation.getMethod()).thenReturn("PUT");
        when(expectation.getRequestContentType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);

        Optional<MimeType> body = SPECIFICATION.getRequestBodyFor(expectation);

        assertThat(body.isPresent(), is(true));
        assertThat(body.get().getType(), is("application/json"));
    }

    @Test
    public void shouldReturnSpecifiedSecurityScheme() {
        assertThat(SPECIFICATION.getSecurityScheme(new SecurityReference("my-token")).getDescription(), is("basic custom token"));
    }
}