package net.ozwolf.mockserver.raml.internal.domain.body;

import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Test;
import org.raml.model.MimeType;

import java.io.IOException;

import static net.ozwolf.mockserver.raml.util.Fixtures.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonBodySpecificationTest {
    @Test
    public void shouldValidateSchemaAndEntityAsCorrect() throws IOException {
        String schema = fixture("apispecs-test/schemas/greeting-response.json");
        String response = fixture("apispecs-test/examples/greeting-response.json");

        MimeType mimeType = mock(MimeType.class);
        when(mimeType.getSchema()).thenReturn(schema);

        ValidationErrors errors = new JsonBodySpecification("Response Body", mimeType).validate(response);

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldValidSchemaAndEntityAsInError() throws IOException {
        String schema = fixture("apispecs-test/schemas/greeting-request.json");
        String request = "{\"notes\":\"I am a note.\"}";

        MimeType mimeType = mock(MimeType.class);
        when(mimeType.getSchema()).thenReturn(schema);

        ValidationErrors errors = new JsonBodySpecification("request", mimeType).validate(request);

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ body ] object has missing required properties ([\"greeting\"])");
    }
}