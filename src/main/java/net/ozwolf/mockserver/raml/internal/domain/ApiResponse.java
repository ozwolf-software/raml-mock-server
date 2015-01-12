package net.ozwolf.mockserver.raml.internal.domain;

import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

public class ApiResponse {
    private final HttpResponse response;

    private final static Header DEFAULT_CONTENT_TYPE = new Header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    public ApiResponse(HttpResponse response) {
        this.response = response;
    }

    public MediaType getContentType() {
        return MediaType.valueOf(
                response.getHeaders().stream()
                        .filter(h -> h.getName().equals(HttpHeaders.CONTENT_TYPE))
                        .findFirst()
                        .orElse(DEFAULT_CONTENT_TYPE)
                        .getValues()
                        .get(0)
        );
    }

    public Integer getStatusCode() {
        return response.getStatusCode();
    }

    public Optional<String> getBody() {
        if (response.getBody() == null)
            return Optional.empty();

        return Optional.of(response.getBodyAsString());
    }
}
