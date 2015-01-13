package net.ozwolf.mockserver.raml.exception;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NoValidResourceExceptionTest {
    @Test
    public void shouldFormatExceptionMessageCorrectly() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.getMethod()).thenReturn("GET");

        RamlMockServerException exception = new NoValidResourceException(expectation);
        assertThat(exception.getMessage(), is("Expectation [ GET /hello/John ] has no valid matching resource specification."));
    }
}