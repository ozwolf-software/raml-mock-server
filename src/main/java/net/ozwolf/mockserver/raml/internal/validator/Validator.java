package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;

public interface Validator {
    ValidationErrors validate();
}
