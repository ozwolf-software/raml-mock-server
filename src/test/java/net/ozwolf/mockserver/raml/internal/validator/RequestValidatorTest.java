package net.ozwolf.mockserver.raml.internal.validator;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);

    @Test
    public void shouldReturnErrorIfNoMatchingResourceFound() {
        when(expectation.hasValidResource()).thenReturn(false);

        ValidationErrors errors = new RequestValidator(expectation).validate();

        assertThat(errors.isInError(), is(true));
        assertThat(errors.getMessages().size(), is(1));
        assertThat(errors.getMessages(), hasItem("[ request ] No resource matching for request found."));
    }

    @Test
    public void shouldReturnErrorIfNoMatchignActionFound() {
        when(expectation.hasValidResource()).thenReturn(true);
        when(expectation.hasValidAction()).thenReturn(false);

        ValidationErrors errors = new RequestValidator(expectation).validate();

        assertThat(errors.isInError(), is(true));
        assertThat(errors.getMessages().size(), is(1));
        assertThat(errors.getMessages(), hasItem("[ request ] Resource for does not support method."));
    }

    @Test
    public void shouldRunValidators() {
        Validator validator1 = validator("This is an error!");
        Validator validator2 = validator(null);
        Validator validator3 = validator("And this is another error!");

        when(expectation.hasValidResource()).thenReturn(true);
        when(expectation.hasValidAction()).thenReturn(true);

        ValidationErrors errors = new MyTestValidator(expectation, validator1, validator2, validator3).validate();

        assertThat(errors.isInError(), is(true));
        assertThat(errors.getMessages().size(), is(2));
        assertThat(errors.getMessages(), hasItem("This is an error!"));
        assertThat(errors.getMessages(), hasItem("And this is another error!"));
    }

    private Validator validator(String errorMessage) {
        Validator validator = mock(Validator.class);
        ValidationErrors errors = new ValidationErrors();
        if (StringUtils.isNotBlank(errorMessage))
            errors.addMessage(errorMessage);

        when(validator.validate()).thenReturn(errors);
        return validator;
    }

    private static class MyTestValidator extends RequestValidator {
        private final List<Validator> validators;

        public MyTestValidator(ApiExpectation expectation, Validator... validators) {
            super(expectation);
            this.validators = newArrayList(validators);
        }

        @Override
        protected List<Validator> getValidators() {
            return validators;
        }
    }
}