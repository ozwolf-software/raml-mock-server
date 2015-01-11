package net.ozwolf.mockserver.raml.internal.validator;

import org.mockserver.model.HttpRequest;
import org.raml.model.Action;
import org.raml.model.parameter.UriParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RequestValidator {
    private final Action action;
    private final HttpRequest request;

    public RequestValidator(Action action, HttpRequest request) {
        this.action = action;
        this.request = request;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        errors.addAll(validateUriParameters());
        errors.addAll(validateQueryParameters());
        errors.addAll(validateHeaderParameters());

        return errors;
    }

    private List<String> validateUriParameters() {
        List<String> errors = new ArrayList<>();

        Map<String, UriParameter> uriParameters = action.getResource().getUriParameters();

        return errors;
    }

    private List<String> validateQueryParameters() {
        List<String> errors = new ArrayList<>();



        return errors;
    }

    private List<String> validateHeaderParameters() {
        List<String> errors = new ArrayList<>();
        return errors;
    }
}
