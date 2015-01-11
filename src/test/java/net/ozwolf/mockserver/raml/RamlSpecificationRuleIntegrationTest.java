package net.ozwolf.mockserver.raml;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import net.ozwolf.mockserver.raml.specification.ClassPathSpecification;
import net.ozwolf.mockserver.raml.specification.FilePathSpecification;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.raml.model.ActionType;

import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class RamlSpecificationRuleIntegrationTest {
    @Rule
    public MockServerRule server = new MockServerRule(15000, this);

    @ClassRule
    public final static RamlSpecificationsRule SPECIFICATIONS = new RamlSpecificationsRule()
            .withSpecifications(
                    new ClassPathSpecification("my-service", "apispecs/apispecs.raml"),
                    new FilePathSpecification("my-other-service", "src/test/resources/apispecs/apispecs.raml")
            );

    @Test
    public void shouldCorrectlyVerifyMockServerExpectationsAgainstRamlSpecification() {
        new MockServerClient("localhost", 15000)
                .when(
                        request()
                                .withPath("/hello/John")
                                .withMethod(ActionType.GET.name())
                                .withHeader(
                                        new Header(HttpHeaders.ACCEPT, "application/json")
                                )
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeader(
                                        new Header(HttpHeaders.CONTENT_TYPE, "application/json")
                                )
                                .withBody("{\"name\":\"John\", \"greeting\":\"Hello John!\"}")
                );

        Client client = Client.create(new DefaultClientConfig());

        String resource1 = client.resource("http://localhost:15000/hello/John").get(String.class);

        SPECIFICATIONS.get("my-service").obeyedBy(new MockServerClient("localhost", 15000));
    }
}
