package net.ozwolf.mockserver.raml.internal.domain.body;

import net.ozwolf.mockserver.raml.internal.domain.BodySpecification;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.assertj.core.api.Assertions.assertThat;


public class DefaultBodySpecificationTest {
    private final MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

    @Test
    public void shouldAlwaysReturnEmptyErrors() {
        BodySpecification specification = new DefaultBodySpecification(mediaType);
        assertThat(specification.validate("").isInError()).isFalse();
        assertThat(specification.getContentType()).isEqualTo(mediaType);
    }
}