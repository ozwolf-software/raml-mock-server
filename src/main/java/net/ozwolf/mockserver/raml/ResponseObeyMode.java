package net.ozwolf.mockserver.raml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * # Obey Check Mode
 *
 * This flag is used in conjunction with the `RamlSpecification.obeyedBy()` method to specify the level of response code allowance response validation will cover.
 *
 * + `STRICT` - The expected status code **must** be defined in the API specification.
 * + `SAFE_ERRORS` - Allows responses matching codes `404`, `405`, `415` and all `5**` exception responses to be returned, regardless of API specifications.
 * + *Note*: If the API specification states a `404` response, for example, the MockServer behaviour must still obey that.
 * + `ALL_ERRORS` - Allows all `4**` and `5**` exception responses to be returned, regardless of API specifications.
 * + *Note*: If the API specification states a `404` response, for example, the MockServer behaviour must still obey that.
 */
public enum ResponseObeyMode {
    STRICT,
    SAFE_ERRORS("(404|405|415|5.+)"),
    ALL_ERRORS("(4|5).+");

    private final Pattern alwaysAllowedCodes;

    ResponseObeyMode(String pattern) {
        this.alwaysAllowedCodes = Pattern.compile(pattern);
    }

    ResponseObeyMode() {
        this.alwaysAllowedCodes = null;
    }

    public boolean isStatusCodeAllowed(Integer statusCode) {
        if (alwaysAllowedCodes == null)
            return false;
        Matcher matcher = alwaysAllowedCodes.matcher(String.valueOf(statusCode));
        return matcher.matches();
    }
}
