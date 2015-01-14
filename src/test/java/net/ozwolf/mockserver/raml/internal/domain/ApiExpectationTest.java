package net.ozwolf.mockserver.raml.internal.domain;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.exception.NoValidResourceException;
import net.ozwolf.mockserver.raml.internal.domain.body.DefaultBodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.body.JsonBodySpecification;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;
import org.raml.model.Action;
import org.raml.model.Resource;
import org.raml.model.SecurityScheme;
import org.raml.parser.visitor.RamlDocumentBuilder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.matchers.Times.once;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ApiExpectationTest {
    private final static ApiSpecification SPECIFICATION = new ApiSpecification(new RamlDocumentBuilder().build("apispecs-test/apispecs.raml"));
    private final static Expectation MOCK_GET_EXPECTATION = new Expectation(
            request().withPath("/hello/John")
                    .withMethod("GET")
                    .withHeaders(
                            new Header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON),
                            new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    )
                    .withQueryStringParameter(new Parameter("ttl", "30s")),
            once()
    ).thenRespond(
            response()
                    .withStatusCode(200)
                    .withHeaders(
                            new Header("ttl", "30s"),
                            new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    )
    );
    private final static Expectation MOCK_PUT_EXPECTATION = new Expectation(
            request().withPath("/hello/John/greetings")
                    .withMethod("PUT")
                    .withHeaders(
                            new Header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON),
                            new Header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    )
                    .withBody("Why hello there {name}"),
            once()
    ).thenRespond(
            response()
                    .withStatusCode(200)
                    .withHeader(new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
    );

    private final static ApiExpectation GET_EXPECTATION = new ApiExpectation(SPECIFICATION, MOCK_GET_EXPECTATION);
    private final static ApiExpectation PUT_EXPECTATION = new ApiExpectation(SPECIFICATION, MOCK_PUT_EXPECTATION);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldReturnResponseMatchingExpectationStatusCode() {

        assertThat(GET_EXPECTATION.getResponse().isPresent(), is(true));
    }

    @Test
    public void shouldReturnMatchingRequestHeader() {
        assertThat(GET_EXPECTATION.getRequestHeader("wrong").isPresent(), is(false));
        assertThat(GET_EXPECTATION.getRequestHeader(HttpHeaders.ACCEPT).isPresent(), is(true));
        assertThat(GET_EXPECTATION.getRequestHeader(HttpHeaders.ACCEPT).get().getValues().size(), is(1));
        assertThat(GET_EXPECTATION.getRequestHeader(HttpHeaders.ACCEPT).get().getValues(), hasItem(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnMatchingResponseHeader() {
        assertThat(GET_EXPECTATION.getResponseHeader("wrong").isPresent(), is(false));
        assertThat(GET_EXPECTATION.getResponseHeader("ttl").isPresent(), is(true));
        assertThat(GET_EXPECTATION.getResponseHeader("ttl").get().getValues().size(), is(1));
        assertThat(GET_EXPECTATION.getResponseHeader("ttl").get().getValues(), hasItem("30s"));
    }

    @Test
    public void shouldReturnMatchingQueryParameter() {
        assertThat(GET_EXPECTATION.getQueryParameter("wrong").isPresent(), is(false));
        assertThat(GET_EXPECTATION.getQueryParameter("ttl").isPresent(), is(true));
        assertThat(GET_EXPECTATION.getQueryParameter("ttl").get().getValues().size(), is(1));
        assertThat(GET_EXPECTATION.getQueryParameter("ttl").get().getValues(), hasItem("30s"));
    }

    @Test
    public void shouldReturnUriParameterValue() {
        assertThat(GET_EXPECTATION.getUriValueOf("name"), is("John"));
    }

    @Test
    public void shouldReturnRequestContentType() {
        assertThat(GET_EXPECTATION.getRequestContentType(), is(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void shouldReturnResponseContentType() {
        assertThat(GET_EXPECTATION.getResponseContentType(), is(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void shouldReturnValidResponseBodySpecification() {
        assertThat(GET_EXPECTATION.getResponseBodySpecification().isPresent(), is(true));
        assertThat(GET_EXPECTATION.getResponseBodySpecification().get(), instanceOf(JsonBodySpecification.class));
    }

    @Test
    public void shouldReturnValidRequestBodySpecification() {
        assertThat(PUT_EXPECTATION.getRequestBodySpecification().isPresent(), is(true));
        assertThat(PUT_EXPECTATION.getRequestBodySpecification().get(), instanceOf(DefaultBodySpecification.class));
    }

    @Test
    public void shouldReturnSecuritySpecifications() {
        Map<String, SecurityScheme> schemes = GET_EXPECTATION.getSecuritySpecification();

        assertThat(schemes.size(), is(2));
        assertThat(schemes.containsKey("basic"), is(true));
        assertThat(schemes.containsKey("my-token"), is(true));
    }

    @Test(expected = NoValidResourceException.class)
    public void shouldThrowNoValidResourceExceptionWhenAttemptingToGetUriValueButNoMatchingResource() {
        ApiSpecification specification = mock(ApiSpecification.class);
        when(specification.getResourceFor(any(ApiExpectation.class))).thenReturn(Optional.<Resource>empty());

        new ApiExpectation(specification, MOCK_GET_EXPECTATION).getUriValueOf("name");
    }

    @Test(expected = NoValidResourceException.class)
    public void shouldThrowNoValidResourceExceptionWhenAttemptingToGetSecuritySpecification() {
        ApiSpecification specification = mock(ApiSpecification.class);
        when(specification.getResourceFor(any(ApiExpectation.class))).thenReturn(Optional.<Resource>empty());

        new ApiExpectation(specification, MOCK_GET_EXPECTATION).getSecuritySpecification();
    }

    @Test(expected = NoValidActionException.class)
    public void shouldThrowNoValidActionExceptionWhenAttemptingToGetSecuritySpecification() {
        Resource resource = mock(Resource.class);
        when(resource.getSecuredBy()).thenReturn(newArrayList());

        ApiSpecification specification = mock(ApiSpecification.class);
        when(specification.getResourceFor(any(ApiExpectation.class))).thenReturn(Optional.of(resource));
        when(specification.getActionFor(any(ApiExpectation.class))).thenReturn(Optional.<Action>empty());

        new ApiExpectation(specification, MOCK_GET_EXPECTATION).getSecuritySpecification();
    }
}