package net.ozwolf.mockserver.raml.handlers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class RamlFileHandlerIntegrationTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldCopyFileIntoTargetLocation() {
        File file = new File("src/test/resources/apispecs-test/apispecs.raml");
        File targetDirectory = new File("target/specifications/test-service");
        File expectedFile = new File("target/specifications/test-service/apispecs.raml");

        File result = RamlFileHandler.handler(targetDirectory.getPath()).handle(file);

        assertThat(result).isEqualTo(expectedFile);
        assertThat(result.exists()).isTrue();
    }

    @Test
    public void shouldReturnIllegalAccessErrorWhenTargetDirectoryCannotBeCreated() {
        File targetDirectory = new File("target/specifications/test-service");
        File spy = spy(targetDirectory);

        when(spy.exists()).thenReturn(false);
        when(spy.mkdirs()).thenReturn(false);

        exception.expect(IllegalAccessError.class);
        exception.expectMessage("Could not create [ target/specifications/test-service ] directory.");
        new RamlFileHandler(spy).handle(null);
    }

    @Test
    public void shouldReturnIllegalArgumentErrorWhenTargetDirectoryIsNotADirectory() {
        File targetDirectory = new File("src/test/resources/apispecs-test/apispecs.raml");

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("[ src/test/resources/apispecs-test/apispecs.raml ] is not a directory.");
        new RamlFileHandler(targetDirectory).handle(null);
    }

    @Test
    public void shouldReturnIllegalStateErrorWhenTargetDirectoryCannotBeWrittenTo() {
        File targetDirectory = new File("target/specifications/test-service");
        File spy = spy(targetDirectory);

        when(spy.exists()).thenReturn(true);
        when(spy.isDirectory()).thenReturn(true);
        when(spy.canWrite()).thenReturn(false);

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Cannot write to [ target/specifications/test-service ].");
        new RamlFileHandler(spy).handle(null);
    }
}