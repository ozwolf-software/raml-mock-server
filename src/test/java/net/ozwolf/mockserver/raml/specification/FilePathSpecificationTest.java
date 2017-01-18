package net.ozwolf.mockserver.raml.specification;

import org.junit.Test;
import org.raml.model.Raml;

import static org.assertj.core.api.Assertions.assertThat;

public class FilePathSpecificationTest {
    @Test
    public void shouldLoadRamlFromFilePath() {
        FilePathSpecification specification = new FilePathSpecification("my-service", "src/test/resources/apispecs-test/apispecs.raml");

        Raml raml = specification.getRaml();

        assertThat(raml.getResources().get("/hello/{name}").getDisplayName()).isEqualTo("Hello Greeting");
    }
}