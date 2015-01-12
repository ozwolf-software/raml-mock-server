package net.ozwolf.mockserver.raml.internal.domain;

import org.raml.model.MimeType;

import javax.ws.rs.core.MediaType;
import java.util.Optional;

public class ApiBody {
    private final MediaType contentType;
    private final MimeType mimeType;

    public ApiBody(MediaType contentType, MimeType mimeType) {
        this.contentType = contentType;
        this.mimeType = mimeType;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public Optional<String> getSchema() {
        if (mimeType.getSchema() == null)
            return Optional.empty();

        return Optional.of(mimeType.getSchema());
    }
}
