package net.ozwolf.mockserver.raml.exception;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NoValidResponseExceptionTest {
    @Test
    public void shouldFormatExceptionMessageCorrectly() {
        ApiExpectation expectation = mock(ApiExpectation.class);
        when(expectation.getUri()).thenReturn("/hello/John");
        when(expectation.getMethod()).thenReturn("GET");
        when(expectation.getResponseStatusCode()).thenReturn(201);

        RamlMockServerException exception = new NoValidResponseException(expectation);
        assertThat(exception.getMessage(), is("Expectation [ GET /hello/John ] [ 200 ] has no valid matching response specification."));
    }

}