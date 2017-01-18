package net.ozwolf.mockserver.raml.internal.validator.security;

import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ValidationErrors;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.model.Parameter;
import org.raml.model.ParamType;
import org.raml.model.SecurityScheme;
import org.raml.model.SecuritySchemeDescriptor;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestSecurityValidatorTest {
    private final ApiExpectation expectation = mock(ApiExpectation.class);

    @Before
    public void setUp() {
        Map<String, SecurityScheme> security = getSecurity();
        when(expectation.getSecuritySpecification()).thenReturn(security);
    }

    @Test
    public void shouldReturnZeroErrorsWhenThereAreNoSecuritySpecifications() {
        when(expectation.getSecuritySpecification()).thenReturn(new HashMap<>());

        ValidationErrors errors = new RequestSecurityValidator(expectation).validate();

        assertThat(errors.isInError()).isFalse();
    }

    @Test
    public void shouldReturnErrorThatSecurityIsMissing() {
        when(expectation.getRequestHeader(HttpHeaders.AUTHORIZATION)).thenReturn(Optional.empty());
        when(expectation.getQueryParameter("token")).thenReturn(Optional.empty());

        ValidationErrors errors = new RequestSecurityValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(1)
                .contains(("[ request ] [ security ] Missing required security credentials.  Must use one of [ query-token, basic ]."));
    }

    @Test
    public void shouldReturnErrorsForBrokenSecuritySpecifications() {
        org.mockserver.model.Header authorizationHeader = new org.mockserver.model.Header(HttpHeaders.AUTHORIZATION, "Basic 1234", "wrong");
        Parameter tokenParameter = new Parameter("token", "123456", "i_am_text");

        when(expectation.getRequestHeader(HttpHeaders.AUTHORIZATION)).thenReturn(Optional.of(authorizationHeader));
        when(expectation.getQueryParameter("token")).thenReturn(Optional.of(tokenParameter));

        ValidationErrors errors = new RequestSecurityValidator(expectation).validate();

        assertThat(errors.isInError()).isTrue();
        assertThat(errors.getMessages())
                .hasSize(4)
                .contains(
                        "[ security ] [ header ] [ basic ] Only one value allowed for security parameters but multiple found.",
                        "[ security ] [ header ] [ basic ] Value of [ wrong ] does not meet API requirements.",
                        "[ security ] [ query ] [ query-token ] Only one value allowed for security parameters but multiple found.",
                        "[ security ] [ query ] [ query-token ] Value of [ i_am_text ] does not meet API requirements."
                );
    }

    private Map<String, SecurityScheme> getSecurity() {
        Map<String, SecurityScheme> security = new HashMap<>();

        SecurityScheme basicHeader = new SecurityScheme();
        SecuritySchemeDescriptor basicHeaderDesc = new SecuritySchemeDescriptor();
        basicHeaderDesc.setHeaders(basicAuthorizationHeaders());
        basicHeader.setDescribedBy(basicHeaderDesc);

        SecurityScheme queryToken = new SecurityScheme();
        SecuritySchemeDescriptor queryTokenDesc = new SecuritySchemeDescriptor();
        queryTokenDesc.setQueryParameters(queryTokenParameters());
        queryToken.setDescribedBy(queryTokenDesc);

        security.put("basic", basicHeader);
        security.put("query-token", queryToken);

        return security;
    }

    private Map<String, Header> basicAuthorizationHeaders() {
        Map<String, Header> headers = new HashMap<>();

        Header header = new Header();
        header.setRequired(true);
        header.setRepeat(false);
        header.setType(ParamType.STRING);
        header.setPattern("Basic (.+)");

        headers.put(HttpHeaders.AUTHORIZATION, header);

        return headers;
    }

    private Map<String, QueryParameter> queryTokenParameters() {
        Map<String, QueryParameter> parameters = new HashMap<>();

        QueryParameter parameter = new QueryParameter();
        parameter.setRequired(true);
        parameter.setRepeat(false);
        parameter.setType(ParamType.INTEGER);

        parameters.put("token", parameter);

        return parameters;
    }
}