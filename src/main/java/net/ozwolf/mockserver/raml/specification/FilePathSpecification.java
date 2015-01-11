package net.ozwolf.mockserver.raml.specification;

import net.ozwolf.mockserver.raml.RamlSpecification;
import org.apache.commons.io.FileUtils;
import org.raml.model.Raml;
import org.raml.parser.loader.FileResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;

import java.io.File;
import java.io.IOException;

public class FilePathSpecification extends RamlSpecification {
    private final String filePath;

    public FilePathSpecification(String name, String filePath) {
        super(name);
        this.filePath = filePath;
    }

    @Override
    protected Raml getRaml() {
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
