package net.ozwolf.mockserver.raml.internal.domain;

import org.raml.model.parameter.AbstractParam;

public class ApiParameter {
    private final String name;
    private final AbstractParam parameter;

    public ApiParameter(String name, AbstractParam parameter) {
        this.name = name;
        this.parameter = parameter;
    }

    public String getName() {
        return name;
    }

    public AbstractParam getParameter() {
        return parameter;
    }
}
