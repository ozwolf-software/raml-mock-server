package net.ozwolf.mockserver.raml.internal.domain.body;

import net.ozwolf.mockserver.raml.internal.domain.BodySpecification;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.body.content.JsonContentValidator;
import org.raml.model.MimeType;

import javax.ws.rs.core.MediaType;

public class JsonBodySpecification implements BodySpecification {
    private final String bodyType;
    private final MimeType mimeType;

    public JsonBodySpecification(String bodyType, MimeType mimeType) {
        this.bodyType = bodyType;
        this.mimeType = mimeType;
    }

    @Override
    public MediaType getContentType() {
        return MediaType.APPLICATION_JSON_TYPE;
    }

    @Override
    public ValidationErrors validate(String body) {
        if (mimeType.getSchema() == null)
            return new ValidationErrors();

        return new JsonContentValidator(bodyType).validate(mimeType.getSchema(), body);
    }
}
