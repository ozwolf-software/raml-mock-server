package net.ozwolf.mockserver.raml.internal.domain;

import javax.ws.rs.core.MediaType;

public interface BodySpecification {
    MediaType getContentType();

    ValidationErrors validate(String body);
}
