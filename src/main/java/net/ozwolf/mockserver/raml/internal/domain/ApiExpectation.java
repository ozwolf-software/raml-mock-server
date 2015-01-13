package net.ozwolf.mockserver.raml.internal.domain;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.exception.NoValidResourceException;
import net.ozwolf.mockserver.raml.exception.NoValidResponseException;
import net.ozwolf.mockserver.raml.internal.domain.body.DefaultBodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.body.JsonBodySpecification;
import org.apache.commons.lang.StringUtils;
import org.mockserver.mock.Expectation;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.Parameter;
import org.raml.model.*;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.*;

public class ApiExpectation {
    private final ApiSpecification specification;
    private final HttpRequest request;
    private final HttpResponse response;

    public ApiExpectation(ApiSpecification specification, Expectation expectation) {
        this.specification = specification;
        this.request = expectation.getHttpRequest();
        this.response = expectation.getHttpResponse(false);
    }

    public boolean hasValidResource() {
        return getResource().isPresent();
    }

    public boolean hasValidAction() {
        return getAction().isPresent();
    }

    public boolean hasValidResponse() {
        return getResponse().isPresent();
    }

    public Optional<Resource> getResource() {
        return specification.getResourceFor(this);
    }

    public Optional<Action> getAction() {
        return specification.getActionFor(this);
    }

    public Optional<Response> getResponse() {
        return specification.getResponseFor(this);
    }

    public String getUri() {
        return this.request.getPath();
    }

    public String getMethod() {
        return request.getMethod();
    }

    public Optional<Header> getRequestHeader(String name) {
        return request.getHeaders().stream()
                .filter(h -> h.getName().equals(name))
                .findFirst();
    }

    public Optional<Header> getResponseHeader(String name) {
        return response.getHeaders().stream()
                .filter(h -> h.getName().equals(name))
                .findFirst();
    }

    public Optional<Parameter> getQueryParameter(String name) {
        return request.getQueryStringParameters().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    public String getUriValueOf(String parameterName) {
        String uri = getResource().orElseThrow(() -> new NoValidResourceException(this)).getUri();
        List<String> uriParts = new ArrayList<>();
        List<String> pathParts = new ArrayList<>();

        Collections.addAll(uriParts, StringUtils.split(uri, "/"));
        Collections.addAll(pathParts, StringUtils.split(request.getPath(), "/"));

        int parameterIndex = uriParts.indexOf(String.format("{%s}", parameterName));
        return pathParts.get(parameterIndex);
    }

    public Optional<String> getRequestBody() {
        return Optional.of(request.getBodyAsString());
    }

    public Optional<String> getResponseBody() {
        return Optional.of(response.getBodyAsString());
    }

    public MediaType getRequestContentType() {
        return MediaType.valueOf(
                getRequestHeader(HttpHeaders.CONTENT_TYPE)
                        .orElse(new Header(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD))
                        .getValues()
                        .get(0)
        );
    }

    public MediaType getResponseContentType() {
        return MediaType.valueOf(
                getResponseHeader(HttpHeaders.CONTENT_TYPE)
                        .orElse(new Header(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD))
                        .getValues()
                        .get(0)
        );
    }

    public String getAllowedRequestContentTypes() {
        Action action = getAction().orElseThrow(() -> new NoValidActionException(this));
        if (action.getBody() == null) return "";
        return StringUtils.join(
                action.getBody().keySet(),
                ", "
        );
    }

    public String getAllowedResponseContentTypes() {
        Response response = getResponse().orElseThrow(() -> new NoValidResponseException(this));
        if (response.getBody() == null)
            return "";
        return StringUtils.join(
                response.getBody().keySet(),
                ", "
        );
    }

    public Optional<BodySpecification> getRequestBodySpecification() {
        Optional<MimeType> body = specification.getRequestBodyFor(this);
        if (!body.isPresent())
            return Optional.empty();

        if (MediaType.APPLICATION_JSON_TYPE.isCompatible(this.getRequestContentType()))
            return Optional.of(new JsonBodySpecification("Request Body", body.get()));

        return Optional.of(new DefaultBodySpecification(this.getResponseContentType()));
    }

    public Optional<BodySpecification> getResponseBodySpecification() {
        Optional<MimeType> body = specification.getResponseBodyFor(this);
        if (!body.isPresent())
            return Optional.empty();

        if (MediaType.APPLICATION_JSON_TYPE.isCompatible(this.getResponseContentType()))
            return Optional.of(new JsonBodySpecification("Response Body", body.get()));

        return Optional.of(new DefaultBodySpecification(this.getResponseContentType()));
    }

    public Map<String, SecurityScheme> getSecuritySpecification() {
        List<SecurityReference> resourceReferences = getResource()
                .orElseThrow(() -> new NoValidResourceException(this))
                .getSecuredBy();
        List<SecurityReference> actionReferences = getAction()
                .orElseThrow(() -> new NoValidActionException(this))
                .getSecuredBy();

        Map<String, SecurityScheme> security = new HashMap<>();
        resourceReferences.stream()
                .forEach(s -> security.put(s.getName(), specification.getSecurityScheme(s)));
        actionReferences.stream()
                .forEach(s -> {
                    if (!security.containsKey(s.getName()))
                        security.put(s.getName(), specification.getSecurityScheme(s));
                });

        return security;
    }

    public String getSecuritySpecificationDescription() {
        return StringUtils.join(getSecuritySpecification().keySet(), ", ");
    }

    public Integer getResponseStatusCode() {
        return response.getStatusCode();
    }
}
