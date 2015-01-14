package net.ozwolf.mockserver.raml.internal.validator.parameters;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.model.Header;
import org.raml.model.ParamType;
import org.raml.model.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
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

        assertThat(errors.isInError(), is(false));
    }

    @Test
    public void shouldReturnZeroErrorsWhenHeaderIsOptionalAndNoValueProvided() {
        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.<Header>empty());
        when(response.getHeaders()).thenReturn(header(false, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError(), is(false));
    }

    @Test
    public void shouldIncludeErrorWhenParameterIsRequiredButNotProvided() {
        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.<Header>empty());
        when(response.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError(), is(true));
        assertThat(errors.getMessages().size(), is(1));
        assertThat(errors.getMessages(), hasItem("[ response ] [ header ] [ ttl ] Parameter is compulsory but no value(s) provided."));
    }

    @Test
    public void shouldReturnErrorIfHeaderIsNotRepeatableButMultipleValuesProvided() {
        Header header = new Header("ttl", "1", "2");
        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.of(header));
        when(response.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError(), is(true));
        assertThat(errors.getMessages().size(), is(1));
        assertThat(errors.getMessages(), hasItem("[ response ] [ header ] [ ttl ] Only one value allowed but multiple values provided."));
    }

    @Test
    public void shouldReturnErrorIfHeaderDoesNotMeetRulesOfParameter() {
        Header header = new Header("ttl", "i_am_text");

        when(expectation.getResponseHeader("ttl")).thenReturn(Optional.of(header));
        when(response.getHeaders()).thenReturn(header(true, false));

        ValidationErrors errors = new ResponseHeaderParametersValidator(expectation).validate();

        assertThat(errors.isInError(), is(true));
        assertThat(errors.getMessages().size(), is(1));
        assertThat(errors.getMessages(), hasItem("[ response ] [ header ] [ ttl ] Value of [ i_am_text ] does not meet API requirements."));
    }

    private Map<String, org.raml.model.parameter.Header> header(boolean required, boolean repeatable) {
        org.raml.model.parameter.Header header = new org.raml.model.parameter.Header();
        header.setRequired(required);
        header.setRepeat(repeatable);
        header.setType(ParamType.INTEGER);
        Map<String, org.raml.model.parameter.Header> headers = new HashMap<>();
        headers.put("ttl", header);
        return headers;
    }
}