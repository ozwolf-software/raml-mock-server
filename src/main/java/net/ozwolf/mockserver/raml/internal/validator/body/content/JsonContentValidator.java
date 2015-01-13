package net.ozwolf.mockserver.raml.internal.validator.body.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;

public class JsonContentValidator {
    private final String bodyType;

    public JsonContentValidator(String bodyType) {
        this.bodyType = bodyType;
    }

    public ValidationErrors validate(String schema, String requestBody) {
        ValidationErrors errors = new ValidationErrors();

        try {
            JsonNode schemaNode = JsonLoader.fromString(schema);
            JsonNode dataNode = JsonLoader.fromString(requestBody);

            ProcessingReport report = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode).validate(dataNode);

            if (!report.isSuccess())
                report.iterator().forEachRemaining(m -> errors.addMessage("%s: %s", bodyType, m.getMessage()));
        } catch (Exception e) {
            errors.addMessage("%s: Unexpected error of [ %s ] while validating JSON content", bodyType, e.getMessage());
        }
        return errors;
    }
}
