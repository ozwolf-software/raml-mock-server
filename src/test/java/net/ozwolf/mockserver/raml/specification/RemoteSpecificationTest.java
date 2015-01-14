package net.ozwolf.mockserver.raml.specification;

import net.lingala.zip4j.exception.ZipException;
import net.ozwolf.mockserver.raml.handlers.ZipArchiveHandler;
import org.junit.Test;
import org.raml.model.Raml;

import java.io.File;

import static net.ozwolf.mockserver.raml.util.Fixtures.zipFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RemoteSpecificationTest {
    @Test
    public void shouldLoadRamlFromRemoteLocationAndUseProvidedFileHandler() throws ZipException {
        File zip = zipFixture("src/test/resources/apispecs-test");
        File targetDirectory = new File("target/specifications/test-service");
        File specificationFile = new File("target/specifications/test-service/apispecs.raml");

        RemoteSpecification specification = new RemoteSpecification("my-service", zip.toURI().toString(), new ZipArchiveHandler(targetDirectory, specificationFile));

        Raml raml = specification.getRaml();

        assertThat(raml.getResources().get("/hello/{name}").getDisplayName(), is("Hello Greeting"));
    }
}