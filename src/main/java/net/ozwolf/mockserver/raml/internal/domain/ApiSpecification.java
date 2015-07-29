package net.ozwolf.mockserver.raml.internal.domain;

import com.sun.jersey.api.uri.UriTemplate;
import org.raml.model.*;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.*;

public class ApiSpecification {
    private final Raml raml;

    public ApiSpecification(Raml raml) {
        this.raml = raml;
    }

    public Optional<Resource> getResourceFor(ApiExpectation expectation) {
        return findResourceIn(expectation.getUri(), raml.getResources());
    }

    public Optional<Action> getActionFor(ApiExpectation expectation) {
        Optional<Resource> resource = getResourceFor(expectation);
        if (!resource.isPresent())
            return empty();
        return ofNullable(resource.get().getAction(expectation.getMethod()));
    }

    public Optional<Response> getResponseFor(ApiExpectation expectation) {
        Optional<Action> action = getActionFor(expectation);
        if (!action.isPresent())
            return empty();

        return action.get().getResponses().entrySet().stream()
                .filter(e -> Integer.valueOf(e.getKey()).equals(expectation.getResponseStatusCode()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public Optional<MimeType> getRequestBodyFor(ApiExpectation expectation) {
        Optional<Action> action = getActionFor(expectation);
        if (!action.isPresent())
            return empty();

        return action.get().getBody().entrySet().stream()
                .filter(e -> expectation.getRequestContentType().isCompatible(MediaType.valueOf(e.getKey())))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public Optional<MimeType> getResponseBodyFor(ApiExpectation expectation) {
        Optional<Response> response = getResponseFor(expectation);
        if (!response.isPresent())
            return empty();
        return response.get().getBody().entrySet().stream()
                .filter(e -> expectation.getResponseContentType().isCompatible(MediaType.valueOf(e.getKey())))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    public SecurityScheme getSecurityScheme(SecurityReference reference) {
        return raml.getSecuritySchemes().stream()
                .filter(m -> m.containsKey(reference.getName()))
                .map(m -> m.get(reference.getName()))
                .findFirst()
                .get();
    }

    private Optional<Resource> findResourceIn(String uri, Map<String, Resource> resources) {
        return resources.entrySet().stream()
                .map(e -> {
                    if (new UriTemplate(e.getValue().getUri()).match(uri, new HashMap<>()))
                        return of(e.getValue());

                    Map<String, Resource> children = e.getValue().getResources();
                    if (children != null)
                        return findResourceIn(uri, children);

                    return Optional.<Resource>empty();
                })
                .reduce(empty(), (a, b) -> (b.isPresent()) ? b : a);
    }
}
