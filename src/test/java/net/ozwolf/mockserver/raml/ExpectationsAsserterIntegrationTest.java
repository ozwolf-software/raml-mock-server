package net.ozwolf.mockserver.raml;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import net.ozwolf.mockserver.raml.provider.ClassPathProvider;
import net.ozwolf.mockserver.raml.provider.FilePathProvider;
import net.ozwolf.mockserver.raml.provider.RemoteProvider;
import net.ozwolf.mockserver.raml.remote.ZipArchiveRemoteResourceHandler;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.raml.model.ActionType;

import javax.ws.rs.core.MediaType;
import java.net.URI;

import static net.ozwolf.mockserver.raml.remote.ZipArchiveRemoteResourceHandler.handler;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ExpectationsAsserterIntegrationTest {
    @Rule
    public MockServerRule server = new MockServerRule(15000, this);

    @ClassRule
    public final static RamlSpecificationsRule SPECIFICATIONS = new RamlSpecificationsRule()
            .withSpecification("my-service", new ClassPathProvider("apispecs-test/apispecs.raml"))
            .withSpecification("my-file-service", new FilePathProvider("src/test/resources/apispecs-test/apispecs.raml"))
            .withSpecification("my-remote-service", new RemoteProvider("http://remote.site.com/apispecs.zip", handler("target/specifications/remote-service", "apispecs.raml")));

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

        String response1 = client.resource(URI.create("http://localhost:15000/hello/John")).accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);


    }
}
