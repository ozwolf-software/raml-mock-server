package net.ozwolf.mockserver.raml.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.raml.model.parameter.AbstractParam;

import java.util.Map;

public class ParameterMatchers {
    public static TypeSafeMatcher<Map.Entry<String, ? extends AbstractParam>> parameterOfName(final String name) {
        return new TypeSafeMatcher<Map.Entry<String, ? extends AbstractParam>>() {
            @Override
            protected boolean matchesSafely(Map.Entry<String, ? extends AbstractParam> item) {
                return item.getKey().equals(name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("name = <%s>", name));
            }

            @Override
            protected void describeMismatchSafely(Map.Entry<String, ? extends AbstractParam> item, Description mismatchDescription) {
                mismatchDescription.appendText(String.format("name = <%s>", item.getKey()));
            }
        };
    }
}
