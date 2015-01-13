package net.ozwolf.mockserver.raml.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.raml.model.SecurityScheme;

import java.util.Map;

public class SecurityMatchers {
    public static TypeSafeMatcher<Map.Entry<String, SecurityScheme>> securityOfName(final String name) {
        return new TypeSafeMatcher<Map.Entry<String, SecurityScheme>>() {
            @Override
            protected boolean matchesSafely(Map.Entry<String, SecurityScheme> item) {
                return item.getKey().equals(name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("name = <%s>", name));
            }

            @Override
            protected void describeMismatchSafely(Map.Entry<String, SecurityScheme> item, Description mismatchDescription) {
                mismatchDescription.appendText(String.format("name = <%s>", item.getKey()));
            }
        };
    }
}
