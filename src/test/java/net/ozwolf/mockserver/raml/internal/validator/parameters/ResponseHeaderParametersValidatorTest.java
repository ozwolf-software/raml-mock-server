package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.model.Header;
import org.raml.model.Response;

import java.util.Optional;

import static net.ozwolf.mockserver.raml.util.HeaderFactory.header;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseHeaderParametersValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);
    private final Response response = mock(Response.class);

    @Before
    public void setUp() {
        when(expectation.hasValidAction()).thenReturn(true);
        when(expectation.hasValidResponse()).thenReturn(true);
        when(expectation.getResponse()).thenReturn(Optional.of(response));
    }

    @Test(expected = NoValidActionException.class)
    public void shouldThrowNoValidActionException() {
        when(expectation.hasValidAction()).thenReturn(false);

        new ResponseHeaderParametersValidator(expectation).validate();
    }

    @Test
    public void shouldReturnZeroErrorsWhenRequestHasNoExpectedHeaders() {
        when(response.getHeaders()).thenReturn(null);

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldReturnZeroErrorsWhenHeaderIsOptionalAndNoValueProvided() {
        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.empty());
        when(response.getHeaders()).thenReturn(header(false, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldIncludeErrorWhenParameterIsRequiredButNotProvided() {
        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.empty());
        when(response.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ response ] [ header ] [ ttl ] Parameter is compulsory but no value(s) provided.");
    }

    @Test
    public void shouldReturnErrorIfHeaderIsNotRepeatableButMultipleValuesProvided() {
        Header header = new Header("ttl", "1", "2");
        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.of(header));
        when(response.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ response ] [ header ] [ ttl ] Only one value allowed but multiple values provided.");
    }

    @Test
    public void shouldReturnErrorIfHeaderDoesNotMeetRulesOfParameter() {
        Header header = new Header("ttl", "i_am_text");

        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.of(header));
        when(response.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ response ] [ header ] [ ttl ] Value of [ i_am_text ] does not meet API requirements.");
    }
}