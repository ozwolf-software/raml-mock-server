package net.ozwolf.mockserver.raml;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import net.ozwolf.mockserver.raml.specification.ClassPathSpecification;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static net.ozwolf.mockserver.raml.util.Fixtures.fixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class RamlSpecificationRuleIntegrationTest {
    @Rule
    public MockServerRule server = new MockServerRule(5000, this);

    @ClassRule
    public final static RamlSpecificationsRule SPECIFICATIONS = new RamlSpecificationsRule()
            .withSpecifications(
                    new ClassPathSpecification("my-service", "apispecs-test/apispecs.raml")
            );

    @Test
    public void shouldCorrectlyVerifyMockServerExpectationsAgainstRamlSpecification() throws IOException {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);

        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN))
        ).respond(
                response()
                        .withStatusCode(200)
                        .withHeader(new Header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN))
                        .withBody("Hello John!")
        );

        Client client = Client.create(new DefaultClientConfig());

        String response = client.resource("http://localhost:5000/hello/John")
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN)
                .get(String.class);
        assertThat(response, is("Hello John!"));

        RamlSpecification.Result result = SPECIFICATIONS.get("my-service").obeyedBy(mockClient);

        assertFalse(result.isValid());
        assertThat(result.getFormattedErrorMessage(), is(fixture("fixtures/expected-obey-errors-get-hello-john.txt")));
    }

    @Test
    public void shouldPassAllRamlRequirements() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);

        mockClient.when(
                request()
                        .withPath("/hello/John")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN))
                        .withQueryStringParameter(new Parameter("my-token", "12345"))
        ).respond(
                response()
                        .withStatusCode(200)
                        .withHeaders(
                                new Header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN),
                                new Header("ttl", "1m")
                        )
                        .withBody("Hello John!")
        );

        Client client = Client.create(new DefaultClientConfig());

        String response = client.resource("http://localhost:5000/hello/John")
                .queryParam("my-token", "12345")
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN)
                .get(String.class);
        assertThat(response, is("Hello John!"));

        RamlSpecification.Result result = SPECIFICATIONS.get("my-service").obeyedBy(mockClient);

        assertTrue(result.getFormattedErrorMessage(), result.isValid());
    }

    @Test
    public void shouldRaiseErrorsWhenJsonBodiesAreIncorrect() throws IOException {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/hello/Sarah/greetings")
                        .withMethod("PUT")
                        .withHeaders(
                                new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON),
                                new Header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON),
                                new Header(HttpHeaders.AUTHORIZATION, "Basic TOKEN1234")
                        )
                        .withBody(fixture("fixtures/incorrect-greeting-request.json"))
        ).respond(
                response()
                        .withStatusCode(200)
                        .withHeaders(
                                new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        )
                        .withBody(fixture("fixtures/incorrect-greetings-response.json"))
        );

        Client client = Client.create(new DefaultClientConfig());

        String response = client.resource("http://localhost:5000/hello/Sarah/greetings")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Basic TOKEN1234")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .entity(fixture("fixtures/incorrect-greeting-request.json"))
                .put(String.class);
        assertThat(response, is(fixture("fixtures/incorrect-greetings-response.json")));

        RamlSpecification.Result result = SPECIFICATIONS.get("my-service").obeyedBy(mockClient);
        assertFalse(result.isValid());
        assertThat(result.getFormattedErrorMessage(), is(fixture("fixtures/expected-obey-errors-put-greeting.txt")));
    }
}
