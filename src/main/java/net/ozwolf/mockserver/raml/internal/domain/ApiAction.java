package net.ozwolf.mockserver.raml.internal.domain;

import org.apache.commons.lang.StringUtils;
import org.raml.model.*;

import javax.ws.rs.core.MediaType;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ApiAction {
    private final Raml raml;
    private final Action action;

    public ApiAction(Raml raml, Action action) {
        this.raml = raml;
        this.action = action;
    }

    public List<ApiSecurity> getSecuritySchemes() {
        return getSecurityReferences().stream()
                .map(r -> {
                    Optional<SecurityScheme> scheme = getSecurityScheme(r.getName());
                    if (!scheme.isPresent()) return Optional.<ApiSecurity>empty();

                    return Optional.of(new ApiSecurity(r.getName(), scheme.get()));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    public String getUri() {
        return action.getResource().getUri();
    }

    public List<String> getUriParts() {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, StringUtils.split(getUri(), "/"));
        return result;
    }

    public List<ApiParameter> getHeaders() {
        if (action.getHeaders() == null)
            return new ArrayList<>();

        return action.getHeaders().entrySet().stream()
                .map(e -> new ApiParameter(e.getKey(), e.getValue()))
                .collect(toList());
    }

    public List<ApiParameter> getUriParameters() {
        if (action.getResource().getUriParameters() == null)
            return new ArrayList<>();

        return action.getResource().getUriParameters().entrySet().stream()
                .map(e -> new ApiParameter(e.getKey(), e.getValue()))
                .collect(toList());
    }

    public List<ApiParameter> getQueryParameters() {
        if (action.getQueryParameters() == null)
            return new ArrayList<>();

        return action.getQueryParameters().entrySet().stream()
                .map(e -> new ApiParameter(e.getKey(), e.getValue()))
                .collect(toList());
    }

    public boolean hasBody() {
        return action.hasBody();
    }

    public Optional<ApiBody> getRequestBody(MediaType mediaType) {
        if (action.getBody() == null) return Optional.empty();

        return getBodyIn(action.getBody(), mediaType);
    }

    public Optional<ApiBody> getResponseBody(Integer statusCode, MediaType contentType) {
        if (!action.getResponses().containsKey(String.valueOf(statusCode)))
            return Optional.empty();

        Response response = action.getResponses().get(String.valueOf(statusCode));

        return getBodyIn(response.getBody(), contentType);
    }

    public String getRequestContentTypes() {
        return StringUtils.join(action.getBody().keySet(), ", ");
    }

    public String getSecuritySchemeNames() {
        List<String> securitySchemeNames = getSecuritySchemes().stream()
                .map(ApiSecurity::getName)
                .collect(toList());

        return StringUtils.join(securitySchemeNames, ", ");
    }

    private List<SecurityReference> getSecurityReferences() {
        List<SecurityReference> references = action.getSecuredBy();
        references.addAll(action.getResource().getSecuredBy());
        return references;
    }

    private Optional<SecurityScheme> getSecurityScheme(String schemeName) {
        return raml.getSecuritySchemes().stream()
                .filter(s -> s.containsKey(schemeName))
                .map(s -> s.get(schemeName))
                .findFirst();
    }

    private Optional<ApiBody> getBodyIn(Map<String, MimeType> bodies, MediaType mediaType) {
        return bodies.entrySet().stream()
                .map(e -> new ApiBody(MediaType.valueOf(e.getKey()), e.getValue()))
                .filter(b -> b.getContentType().isCompatible(mediaType))
                .findFirst();
    }
}
