package net.ozwolf.mockserver.raml.specification;

import org.junit.Test;
import org.raml.model.Raml;

import static org.assertj.core.api.Assertions.assertThat;


public class ClassPathSpecificationTest {
    @Test
    public void shouldLoadRamlFromClassPath() {
        ClassPathSpecification specification = new ClassPathSpecification("my-service", "apispecs-test/apispecs.raml");

        Raml raml = specification.getRaml();

        assertThat(raml.getResources().get("/hello/{name}").getDisplayName()).isEqualTo("Hello Greeting");
    }
}