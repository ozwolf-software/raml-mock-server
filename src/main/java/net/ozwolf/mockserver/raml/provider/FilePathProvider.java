package net.ozwolf.mockserver.raml.provider;

import net.ozwolf.mockserver.raml.RamlProvider;
import org.apache.commons.io.FileUtils;
import org.raml.model.Raml;
import org.raml.parser.loader.FileResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.io.File;
import java.io.IOException;

public class FilePathProvider implements RamlProvider {
    private final String filePath;

    public FilePathProvider(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Raml getRaml() {
        try {
            File file = new File(filePath);
            if (!file.exists())
                throw new IllegalArgumentException(String.format("[ %s ] does not exist.", file.getPath()));

            if (file.exists() && !file.canRead())
                throw new IllegalStateException(String.format("[ %s ] cannot be read.", file.getPath()));

            if (file.exists() && !file.isFile())
                throw new IllegalArgumentException(String.format("[ %s ] is not a file.", file.getPath()));

            ResourceLoader loader = new FileResourceLoader(file.getParent());
            return new RamlDocumentBuilder(loader).build(FileUtils.readFileToString(file), file.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
