package mybatis.project;

import els.project.JavaProject;
import mybatis.parser.resource.ResourceSystem;
import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

public class ExternalResourceSystem implements ResourceSystem {
    private final JavaProject javaProject;

    public ExternalResourceSystem(JavaProject javaProject) {
        this.javaProject = javaProject;
    }

    @Override
    public InputStream getResourceAsStream(String resource) throws IOException {
        var trimedResource = resource.replaceAll("^\\W+", "");
        var candidates = this.javaProject
                .getOtherFiles()
                .stream()
                .filter(f -> f.path.endsWith(Paths.get(trimedResource)))
                .collect(Collectors.toList());
        return Files.newInputStream(candidates.stream().findFirst().get().path);
    }

    @Override
    public Properties getUrlAsProperties(String urlString) throws IOException {
        return Resources.getUrlAsProperties(urlString);
    }

    @Override
    public Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        try (InputStream in = getResourceAsStream(resource)) {
            props.load(in);
        }
        return props;
    }
}
