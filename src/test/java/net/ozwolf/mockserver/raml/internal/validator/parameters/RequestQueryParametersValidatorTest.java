package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.model.Parameter;
import org.raml.model.Action;
import org.raml.model.ParamType;
import org.raml.model.parameter.QueryParameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestQueryParametersValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);
    private final Action action = mock(Action.class);

    @Before
    public void setUp() {
        when(expectation.hasValidAction()).thenReturn(true);
        when(expectation.getAction()).thenReturn(Optional.of(action));
    }

    @Test(expected = NoValidActionException.class)
    public void shouldThrowNoValidActionException() {
        when(expectation.hasValidAction()).thenReturn(false);

        new RequestQueryParametersValidator(expectation).validate();
    }

    @Test
    public void shouldReturnZeroErrorsWhenRequestHasNoExpectedQueryParameters() {
        when(action.getQueryParameters()).thenReturn(null);

        ValidationErrors errors = new RequestQueryParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldReturnZeroErrorsWhenQueryParameterIsOptionalAndNoValueProvided() {
        when(expectation.getQueryParameter("ttl")).thenReturn(Optional.empty());
        when(action.getQueryParameters()).thenReturn(queryParameter(false, false));

        ValidationErrors errors = new RequestQueryParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldIncludeErrorWhenParameterIsRequiredButNotProvided() {
        when(expectation.getQueryParameter("ttl")).thenReturn(Optional.empty());
        when(action.getQueryParameters()).thenReturn(queryParameter(true, false));

        ValidationErrors errors = new RequestQueryParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ query ] [ ttl ] Parameter is compulsory but no value(s) provided.");
    }

    @Test
    public void shouldReturnErrorIfQueryParameterIsNotRepeatableButMultipleValuesProvided() {
        Parameter parameter = new Parameter("ttl", "1", "2");
        when(expectation.getQueryParameter("ttl")).thenReturn(Optional.of(parameter));
        when(action.getQueryParameters()).thenReturn(queryParameter(true, false));

        ValidationErrors errors = new RequestQueryParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ query ] [ ttl ] Only one value allowed but multiple values provided.");
    }

    @Test
    public void shouldReturnErrorIfQueryParameterDoesNotMeetRulesOfParameter() {
        Parameter parameter = new Parameter("ttl", "i_am_text");

        when(expectation.getQueryParameter("ttl")).thenReturn(Optional.of(parameter));
        when(action.getQueryParameters()).thenReturn(queryParameter(true, false));

        ValidationErrors errors = new RequestQueryParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ query ] [ ttl ] Value of [ i_am_text ] does not meet API requirements.");
    }

    private Map<String, QueryParameter> queryParameter(boolean required, boolean repeatable) {
        QueryParameter parameter = new QueryParameter();
        parameter.setRequired(required);
        parameter.setRepeat(repeatable);
        parameter.setType(ParamType.INTEGER);
        Map<String, QueryParameter> parameters = new HashMap<>();
        parameters.put("ttl", parameter);
        return parameters;
    }
}