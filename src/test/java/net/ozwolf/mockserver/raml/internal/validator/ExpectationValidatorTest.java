package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.ExpectationError;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpectationValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);
    private final Validator validator = mock(Validator.class);

    @Before
    public void setUp(){
        when(expectation.getUri()).thenReturn("/hello/world");
        when(expectation.getMethod()).thenReturn("GET");
    }

    @Test
    public void shouldReturnEmptyErrorWhenNoErrorsFoundFromValidators() {
        when(validator.validate()).thenReturn(new ValidationErrors());

        ExpectationValidator expectationValidator = new ExpectationValidator(expectation, validator);

        assertThat(expectationValidator.validate().isPresent(), is(false));
    }

    @Test
    public void shouldReturnExpectationErrorWhenValidatorReturnsErrors(){
        ValidationErrors errors = new ValidationErrors();
        errors.addMessage("This is an error!");

        when(validator.validate()).thenReturn(errors);

        Optional<ExpectationError> error = new ExpectationValidator(expectation, validator).validate();

        assertThat(error.isPresent(), is(true));
        assertThat(error.get().getMessages().size(), is(1));
        assertThat(error.get().getMessages(), hasItem("This is an error!"));
    }
}