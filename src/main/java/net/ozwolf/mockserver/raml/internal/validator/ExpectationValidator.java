package net.ozwolf.mockserver.raml.internal.validator;

import com.sun.jersey.api.uri.UriTemplate;
import net.ozwolf.mockserver.raml.ExpectationError;
import org.mockserver.mock.Expectation;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class ExpectationValidator {
    private final Expectation expectation;
    private final Raml raml;

    public ExpectationValidator(Expectation expectation, Raml raml) {
        this.expectation = expectation;
        this.raml = raml;
    }

    public Optional<ExpectationError> validate(){
        ExpectationError error = new ExpectationError(expectation);

        Optional<Resource> resource = findResourceFor(expectation.getHttpRequest().getPath());

        if (!resource.isPresent())
            return of(error.withError(String.format("No resource found in specification for path [ %s ]", expectation.getHttpRequest().getPath())));

        Optional<Action> action = of(resource.get().getAction(expectation.getHttpRequest().getMethod()));

        if (!action.isPresent())
            return of(error.withError(String.format("Resource [ %s ] does not accept a method call of [ %s ]", resource.get().getUri(), expectation.getHttpRequest().getMethod())));

        return error.isInError() ? of(error) : empty();
    }

    private Optional<Resource> findResourceFor(String path) {
        return findResourceIn(path, raml.getResources());
    }

    private Optional<Resource> findResourceIn(String path, Map<String, Resource> resources) {
        return resources.entrySet().stream()
                .map(e -> {
                    if (new UriTemplate(e.getValue().getUri()).match(path, new HashMap<>()))
                        return of(e.getValue());

                    Map<String, Resource> children = e.getValue().getResources();
                    if (children != null)
                        return findResourceIn(path, children);

                    return Optional.<Resource>empty();
                })
                .reduce(empty(), (a, b) -> (b.isPresent()) ? b : a);
    }
}
