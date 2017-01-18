package net.ozwolf.mockserver.raml.util;

import org.raml.model.ParamType;
import org.raml.model.parameter.Header;

import java.util.HashMap;
import java.util.Map;

public class HeaderFactory {
    public static Map<String, Header> header(boolean required, boolean repeatable) {
        org.raml.model.parameter.Header header = new org.raml.model.parameter.Header();
        header.setRequired(required);
        header.setRepeat(repeatable);
        header.setType(ParamType.INTEGER);
        Map<String, org.raml.model.parameter.Header> headers = new HashMap<>();
        headers.put("ttl", header);
        return headers;
    }
}
