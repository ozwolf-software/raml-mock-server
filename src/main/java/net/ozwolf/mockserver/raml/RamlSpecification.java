package net.ozwolf.mockserver.raml;

import com.google.common.annotations.VisibleForTesting;
import net.ozwolf.mockserver.raml.internal.domain.ApiExpectation;
import net.ozwolf.mockserver.raml.internal.domain.ApiSpecification;
import net.ozwolf.mockserver.raml.internal.validator.ExpectationValidator;
import net.ozwolf.mockserver.raml.internal.validator.RequestValidator;
import net.ozwolf.mockserver.raml.internal.validator.ResponseValidator;
import net.ozwolf.mockserver.raml.internal.validator.Validator;
import org.mockserver.client.server.MockServerClient;
import org.raml.model.Raml;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

/**
 * # RAML Specification
 *
 * An extensible class for holding a RAML specification and validating `MockServer` expectations against.
 *
 * @see net.ozwolf.mockserver.raml.specification.ClassPathSpecification
 * @see net.ozwolf.mockserver.raml.specification.FilePathSpecification
 * @see net.ozwolf.mockserver.raml.specification.RemoteSpecification
 */
public abstract class RamlSpecification {
    private final String name;

    private Optional<Raml> raml;

    private final static List<String> DEFAULT_METHODS_TO_CHECK = newArrayList("GET", "DELETE", "POST", "PUT");

    /**
     * Create the specification using the given name.
     *
     * @param name The specification name.
     */
    public RamlSpecification(String name) {
        this.name = name;
        this.raml = Optional.empty();
    }

    public String getName() {
        return name;
    }

    /**
     * Initialize the specification.  Needs to be called before any validation and verification can be undertaken.
     */
    public void initialize() {
        this.raml = Optional.of(getRaml());
    }

    /**
     * Determine if the given `MockServer` location has obeyed the RAML API specifications.  Allows `ObeyMode.SAFE_ERRORS` responses to be used.
     *
     * @param client           The `MockServer` client
     * @param alsoCheckMethods By default, only GET, POST, PUT and DELETE methods are checked.  Add HEAD, OPTIONS, etc. if needed here.
     * @return The result of the expectation validations.
     * @throws java.lang.IllegalStateException If the specification has not been initialized.
     * @see ResponseObeyMode
     */
    public Result obeyedBy(MockServerClient client, String... alsoCheckMethods) {
        return this.obeyedBy(client, ResponseObeyMode.SAFE_ERRORS, alsoCheckMethods);
    }

    /**
     * Determine if the given `MockServer` location has obeyed the RAML API specifications.
     *
     * @param client           The `MockServer` client
     * @param responseObeyMode The level of obeying responses will adhere to.
     * @param alsoCheckMethods By default, only GET, POST, PUT and DELETE methods are checked.  Add HEAD, OPTIONS, etc. if needed here.
     * @return The result of the expectation validations.
     * @throws java.lang.IllegalStateException If the specification has not been initialized.
     * @see ResponseObeyMode
     */
    public Result obeyedBy(MockServerClient client, ResponseObeyMode responseObeyMode, String... alsoCheckMethods) {
        Raml raml = this.raml.orElseThrow(() -> new IllegalStateException(String.format("[ %s ] specification has not been initialized", name)));
        Result result = new Result();
        List<String> methodsToCheck = newArrayList(DEFAULT_METHODS_TO_CHECK);
        methodsToCheck.addAll(newArrayList(alsoCheckMethods));

        Arrays.asList(client.retrieveAsExpectations(null))
                .stream()
                .map(e -> {
                    ApiSpecification specification = new ApiSpecification(raml);
                    ApiExpectation expectation = new ApiExpectation(specification, e);
                    if (!methodsToCheck.contains(expectation.getMethod()))
                        return Optional.<ExpectationError>empty();

                    return new ExpectationValidator(expectation, getValidators(expectation, responseObeyMode)).validate();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(result::withErrors);

        return result;
    }

    protected abstract Raml getRaml();

    @VisibleForTesting
    protected Validator[] getValidators(ApiExpectation expectation, ResponseObeyMode responseObeyMode) {
        return new Validator[]{
                new RequestValidator(expectation),
                new ResponseValidator(expectation, responseObeyMode)
        };
    }

    /**
     * # RAML Specification Obey Result
     *
     * Contains the result of validating a `MockServer` expectations have obeyed the RAML API specification.
     */
    public static class Result {
        private final List<ExpectationError> errors;

        private Result() {
            this.errors = newArrayList();
        }

        /**
         * Was the expectations valid.
         *
         * @return Was the expectations valid.
         */
        public boolean isValid() {
            return this.errors.isEmpty();
        }

        /**
         * Return the list of expectations in error.
         *
         * @return The expectation errors.
         */
        public List<ExpectationError> getErrors() {
            return errors;
        }

        /**
         * Print a formatted error message.
         *
         * ```java
         * assertTrue(result.getFormattedErrorMessage(), result.isValid());
         * ```
         *
         * @return The formatted error message
         */
        public String getFormattedErrorMessage() {
            StringBuilder builder = new StringBuilder("Expectation did not meet RAML specification requirements:");
            errors.stream()
                    .forEach(e -> {
                        builder.append(String.format("\n\t[ expectation ] [ %s ] [ %s ]", e.getMethod(), e.getUri()));
                        e.getMessages().stream().forEach(m -> builder.append(String.format("\n\t\t%s", m)));
                    });
            return builder.toString();
        }

        private Result withErrors(ExpectationError error) {
            this.errors.add(error);
            return this;
        }
    }
}
