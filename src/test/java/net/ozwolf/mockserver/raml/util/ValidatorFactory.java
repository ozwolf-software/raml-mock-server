package net.ozwolf.mockserver.raml.util;

import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.apache.commons.lang.StringUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidatorFactory {
    public static Validator validator(String errorMessage) {
        Validator validator = mock(Validator.class);
        ValidationErrors errors = new ValidationErrors();
        if (StringUtils.isNotBlank(errorMessage))
            errors.addMessage(errorMessage);

        when(validator.validate()).thenReturn(errors);
        return validator;
    }
}
