package net.ozwolf.mockserver.raml.internal.domain;

import org.apache.commons.lang.StringUtils;
import org.mockserver.model.Body;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.Parameter;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ApiRequest {
    private final HttpRequest request;

    public ApiRequest(HttpRequest request) {
        this.request = request;
    }

    public String getPath() {
        return this.request.getPath();
    }

    public boolean matchesUriSize(Integer size) {
        return getUriValues().size() == size;
    }

    public Optional<String> getUriParameter(Integer index) {
        List<String> values = getUriValues();
        if (index >= values.size())
            return Optional.empty();

        return Optional.of(values.get(index));
    }

    public Optional<Header> getHeader(String headerName) {
        return request.getHeaders().stream()
                .filter(h -> h.getName().equals(headerName))
                .findFirst();
    }

    public Optional<Parameter> getQueryParameter(String parameterName) {
        return request.getQueryStringParameters().stream()
                .filter(p -> p.getName().equals(parameterName))
                .findFirst();
    }

    public Optional<String> getBody() {
        if (request.getBody() == null) return Optional.empty();
        return Optional.of(request.getBodyAsString());
    }

    public MediaType getContentType() {
        return MediaType.valueOf(
                getHeader(HttpHeaders.CONTENT_TYPE)
                        .orElse(new Header(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD))
                        .getValues().stream()
                        .findFirst()
                        .get()
        );
    }

    private List<String> getUriValues() {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, StringUtils.split(request.getPath(), "/"));
        return result;
    }
}
