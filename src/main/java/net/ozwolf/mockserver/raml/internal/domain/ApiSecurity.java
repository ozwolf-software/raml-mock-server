package net.ozwolf.mockserver.raml.internal.domain;

import org.raml.model.SecurityScheme;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ApiSecurity {
    private final String name;
    private final SecurityScheme scheme;

    public ApiSecurity(String name, SecurityScheme scheme) {
        this.name = name;
        this.scheme = scheme;
    }

    public String getName() {
        return name;
    }

    public List<ApiParameter> getHeaders() {
        if (scheme.getDescribedBy().getHeaders() == null)
            return new ArrayList<>();

        return scheme.getDescribedBy().getHeaders().entrySet().stream()
                .map(e -> new ApiParameter(e.getKey(), e.getValue()))
                .collect(toList());
    }

    public List<ApiParameter> getQueryParameters() {
        if (scheme.getDescribedBy().getQueryParameters() == null)
            return new ArrayList<>();

        return scheme.getDescribedBy().getQueryParameters().entrySet().stream()
                .map(e -> new ApiParameter(e.getKey(), e.getValue()))
                .collect(toList());
    }
}
