package net.ozwolf.mockserver.raml.internal.validator.body.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonContentValidator {
    private final String bodyType;

    public JsonContentValidator(String bodyType) {
        this.bodyType = bodyType;
    }

    public List<String> validate(String schema, String requestBody) {
        List<String> errors = new ArrayList<>();

        try {
            JsonNode schemaNode = JsonLoader.fromString(schema);
            JsonNode dataNode = JsonLoader.fromString(requestBody);

            ProcessingReport report = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode).validate(dataNode);

            if (!report.isSuccess())
                report.iterator().forEachRemaining(m -> errors.add(String.format("%s: %s", bodyType, m.getMessage())));
        } catch (Exception e) {
            errors.add(String.format("%s: Unexpected error of [ %s ] while validating JSON content", bodyType, e.getMessage()));
        }
        return errors;
    }
}
