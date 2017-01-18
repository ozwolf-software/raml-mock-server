package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.raml.model.ParamType;
import org.raml.model.Resource;
import org.raml.model.parameter.UriParameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestUriParametersValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);
    private final Resource resource = mock(Resource.class);

    @Before
    public void setUp() {
        when(expectation.hasValidResource()).thenReturn(true);
        when(expectation.hasValidAction()).thenReturn(true);
        when(expectation.getResource()).thenReturn(Optional.of(resource));
    }

    @Test(expected = NoValidActionException.class)
    public void shouldThrowNoValidActionException() {
        when(expectation.hasValidAction()).thenReturn(false);

        new RequestUriParametersValidator(expectation).validate();
    }

    @Test
    public void shouldReturnErrorIfUriParameterDoesNotMeetRulesOfParameter() {
        when(expectation.getUriValueOf("ttl")).thenReturn("i_am_text");
        when(resource.getResolvedUriParameters()).thenReturn(uriParameter(true, false));

        ValidationErrors errors = new RequestUriParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ uri ] [ ttl ] Value of [ i_am_text ] does not meet API requirements.");
    }

    private Map<String, UriParameter> uriParameter(boolean required, boolean repeatable) {
        UriParameter parameter = new UriParameter();
        parameter.setRequired(required);
        parameter.setRepeat(repeatable);
        parameter.setType(ParamType.INTEGER);
        Map<String, UriParameter> parameters = new HashMap<>();
        parameters.put("ttl", parameter);
        return parameters;
    }
}