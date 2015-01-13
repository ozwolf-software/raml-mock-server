package net.ozwolf.mockserver.raml.internal.validator.body;

import net.ozwolf.mockserver.raml.exception.NoValidActionException;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.Response;

import javax.ws.rs.core.MediaType;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseBodyValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);
    private final Action action = mock(Action.class);

    @Before
    public void setUp() {
        when(expectation.getAction()).thenReturn(Optional.of(action));
        when(expectation.getUri()).thenReturn("/hello/John/greetings");
        when(expectation.getMethod()).thenReturn("PUT");
        when(expectation.getResponseStatusCode()).thenReturn(200);
        when(expectation.getResponseContentType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        when(expectation.hasValidAction()).thenReturn(true);

    }

    @Test(expected = NoValidActionException.class)
    public void shouldThrowNoValidActionException(){
        when(expectation.hasValidAction()).thenReturn(false);

        new ResponseBodyValidator(expectation).validate();
    }

    @Test
    public void shouldRaiseErrorIfNoResponseFoundForExpectation(){
        when(expectation.getResponse()).thenReturn(Optional.<Response>empty());

        ValidationErrors errors = new ResponseBodyValidator(expectation).validate();

        assertThat(errors.isInError(), is(true));
        assertThat(errors.getMessages().size(), is(1));
        assertThat(errors.getMessages(), hasItem("Response [ PUT /hello/John/greetings ] [ 200 ] [ application/json ]: No response specification exists."));
    }
}