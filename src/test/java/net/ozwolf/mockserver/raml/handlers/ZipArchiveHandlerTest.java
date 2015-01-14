package net.ozwolf.mockserver.raml.handlers;

import net.lingala.zip4j.exception.ZipException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static net.ozwolf.mockserver.raml.util.Fixtures.zipFixture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ZipArchiveHandlerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldTakeZipFileAndUnpack() throws ZipException {
        File zip = zipFixture("src/test/resources/apispecs-test");
        File targetDirectory = new File("target/specifications/test-service");
        File specificationFile = new File("target/specifications/test-service/apispecs.raml");

        File result = ZipArchiveHandler.handler(targetDirectory.getPath(), specificationFile.getName()).handle(zip);

        assertThat(result, is(specificationFile));
    }

    @Test
    public void shouldReturnIllegalAccessErrorWhenTargetDirectoryCannotBeCreated() {
        File targetDirectory = new File("target/specifications/test-service");
        File spy = spy(targetDirectory);

        when(spy.exists()).thenReturn(false);
        when(spy.mkdirs()).thenReturn(false);

        exception.expect(IllegalAccessError.class);
        exception.expectMessage("Could not create [ target/specifications/test-service ] directory.");
        new ZipArchiveHandler(spy, null).handle(null);
    }

    @Test
    public void shouldReturnIllegalArgumentErrorWhenTargetDirectoryIsNotADirectory() {
        File targetDirectory = new File("src/test/resources/apispecs-test/apispecs.raml");

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("[ src/test/resources/apispecs-test/apispecs.raml ] is not a directory.");
        new ZipArchiveHandler(targetDirectory, null).handle(null);
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
        new ZipArchiveHandler(spy, null).handle(null);
    }
}