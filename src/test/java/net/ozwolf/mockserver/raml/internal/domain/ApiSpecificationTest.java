package net.ozwolf.mockserver.raml.internal.domain;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.raml.model.*;
import org.raml.parser.visitor.RamlDocumentBuilder;

import javax.ws.rs.core.MediaType;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiSpecificationTest {
    private final static ApiSpecification SPECIFICATION = new ApiSpecification(new RamlDocumentBuilder().build("apispecs-test/apispecs.raml"));

    @Test
    public void shouldReturnResourceForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.isExpectationFor(argThat(isResourceFor("/hello/{name}")))).thenReturn(true);

        Optional<Resource> resource = SPECIFICATION.getResourceFor(expectation);

        assertThat(resource.isPresent(), is(true));
        assertThat(resource.get().getUri(), is("/hello/{name}"));
    }

    @Test
    public void shouldReturnActionForProvidedExpectation() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.getMethod()).thenReturn("GET");
        when(expectation.isExpectationFor(argThat(isResourceFor("/hello/{name}")))).thenReturn(true);

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
        when(expectation.isExpectationFor(argThat(isResourceFor("/hello/{name}")))).thenReturn(true);

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
        when(expectation.isExpectationFor(argThat(isResourceFor("/hello/{name}/greetings")))).thenReturn(true);

        Optional<MimeType> body = SPECIFICATION.getRequestBodyFor(expectation);

        assertThat(body.isPresent(), is(true));
        assertThat(body.get().getType(), is("application/json"));
    }

    @Test
    public void shouldReturnSpecifiedSecurityScheme() {
        assertThat(SPECIFICATION.getSecurityScheme(new SecurityReference("my-token")).getDescription(), is("basic custom token"));
    }

    private static TypeSafeMatcher<Resource> isResourceFor(String uri){
        return new TypeSafeMatcher<Resource>() {
            @Override
            protected boolean matchesSafely(Resource resource) {
                return resource.getUri().equals(uri);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("[ uri = <%s> ]", uri));
            }

            @Override
            protected void describeMismatchSafely(Resource item, Description mismatchDescription) {
                mismatchDescription.appendText(String.format("[ uri = <%s> ]", item.getUri()));
            }
        };
    }
}