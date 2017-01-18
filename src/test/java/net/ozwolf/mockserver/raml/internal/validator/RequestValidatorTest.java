package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static net.ozwolf.mockserver.raml.util.ValidatorFactory.validator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);

    @Test
    public void shouldReturnErrorIfNoMatchingResourceFound() {
        when(expectation.hasValidResource()).thenReturn(false);

        ValidationErrors errors = new RequestValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] No resource matching for request found.");
    }

    @Test
    public void shouldReturnErrorIfNoMatchignActionFound() {
        when(expectation.hasValidResource()).thenReturn(true);
        when(expectation.hasValidAction()).thenReturn(false);

        ValidationErrors errors = new RequestValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains("[ request ] Resource for does not support method.");
    }

    @Test
    public void shouldRunValidators() {
        Validator validator1 = validator("This is an error!");
        Validator validator2 = validator(null);
        Validator validator3 = validator("And this is another error!");

        when(expectation.hasValidResource()).thenReturn(true);
        when(expectation.hasValidAction()).thenReturn(true);

        ValidationErrors errors = new MyTestValidator(expectation, validator1, validator2, validator3).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(2)
                .contains("This is an error!")
                .contains("And this is another error!");
    }

    private static class MyTestValidator extends RequestValidator {
        private final List<Validator> validators;

        MyTestValidator(ApiExpectation expectation, Validator... validators) {
            super(expectation);
            this.validators = newArrayList(validators);
        }

        @Override
        protected List<Validator> getValidators() {
            return validators;
        }
    }
}