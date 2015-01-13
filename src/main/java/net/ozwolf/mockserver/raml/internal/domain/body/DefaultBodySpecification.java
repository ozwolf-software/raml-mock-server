package net.ozwolf.mockserver.raml.internal.domain.body;

import net.ozwolf.mockserver.raml.internal.domain.BodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;

import javax.ws.rs.core.MediaType;

public class DefaultBodySpecification implements BodySpecification {
    private final MediaType mediaType;

    public DefaultBodySpecification(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public ValidationErrors validate(String body) {
        return new ValidationErrors();
    }

    @Override
    public MediaType getContentType() {
        return mediaType;
    }
}
