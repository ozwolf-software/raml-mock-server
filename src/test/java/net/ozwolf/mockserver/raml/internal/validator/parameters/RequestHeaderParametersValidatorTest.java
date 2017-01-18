package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.model.Header;
import org.raml.model.Action;

import java.util.Optional;

import static net.ozwolf.mockserver.raml.util.HeaderFactory.header;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestHeaderParametersValidatorTest {
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

        new RequestHeaderParametersValidator(expectation).validate();
    }

    @Test
    public void shouldReturnZeroErrorsWhenRequestHasNoExpectedHeaders() {
        when(action.getHeaders()).thenReturn(null);

        ValidationErrors errors = new RequestHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldReturnZeroErrorsWhenHeaderIsOptionalAndNoValueProvided() {
        when(expectation.getRequestHeader("ttl")).thenReturn(Optional.empty());
        when(action.getHeaders()).thenReturn(header(false, false));

        ValidationErrors errors = new RequestHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldIncludeErrorWhenParameterIsRequiredButNotProvided() {
        when(expectation.getRequestHeader("ttl")).thenReturn(Optional.empty());
        when(action.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new RequestHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ header ] [ ttl ] Parameter is compulsory but no value(s) provided.");
    }

    @Test
    public void shouldReturnErrorIfHeaderIsNotRepeatableButMultipleValuesProvided() {
        Header header = new Header("ttl", "1", "2");
        when(expectation.getRequestHeader("ttl")).thenReturn(Optional.of(header));
        when(action.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new RequestHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ header ] [ ttl ] Only one value allowed but multiple values provided.");
    }

    @Test
    public void shouldReturnErrorIfHeaderDoesNotMeetRulesOfParameter() {
        Header header = new Header("ttl", "i_am_text");

        when(expectation.getRequestHeader("ttl")).thenReturn(Optional.of(header));
        when(action.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new RequestHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] [ header ] [ ttl ] Value of [ i_am_text ] does not meet API requirements.");
    }
}