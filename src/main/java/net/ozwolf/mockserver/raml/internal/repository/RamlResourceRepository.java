package net.ozwolf.mockserver.raml.internal.repository;

import com.sun.jersey.api.uri.UriTemplate;
import org.raml.model.Raml;
import org.raml.model.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class RamlResourceRepository {
    private final Raml raml;

    public RamlResourceRepository(Raml raml) {
        this.raml = raml;
    }
}
