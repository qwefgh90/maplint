package mybatis.project;

import els.JavaLanguageService;
import els.exception.JavaProjectInitializationError;
import els.project.JavaProject;
import mybatis.parser.XMLConfigParser;
import mybatis.parser.model.Config;
import mybatis.parser.resource.ResourceSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class MyBatisProjectService {
    public JavaProject javaProject;

    public MyBatisProjectService() {
    }

    private Path configFile;
    private ResourceSystem resourceSystem;

    public void initialize(Path root) throws ConfigNotFoundException, MyBatisProjectInitializationException {
        this.initialize(root, null);
    }
    public void initialize(Path root, String configFileName) throws ConfigNotFoundException, MyBatisProjectInitializationException {
        var service = JavaLanguageService.getLanguageService();
        try {
            javaProject = service.createJavaProject(root);
        } catch (JavaProjectInitializationError e) {
            throw new MyBatisProjectInitializationException(e);
        }
        var allXMLFiles = javaProject.getOtherFiles().stream()
                .filter((f) -> f.path.getFileName().toString().contains(".xml"))
                .filter((f) -> configFileName == null ? true : f.path.getFileName().toString().contains(configFileName))
                .collect(Collectors.toList());
        var candidates = allXMLFiles.stream().filter(f -> ProjectUtil.isMyBatisConfigFile(f.path)).collect(Collectors.toList());
        var configPath = candidates.stream().sorted((a,b) -> -(a.path.getFileName().compareTo(b.path.getFileName()))).findFirst();
        if(configPath.isPresent()){
            resourceSystem = new ExternalResourceSystem(javaProject);
            this.configFile = configPath.get().path;
        }else // TODO this code at the wrong position. Clean resources?
            throw new ConfigNotFoundException(root.toString());
    }

    public Path getConfigFile() {
        return configFile;
    }

    public Config getParsedConfig(){
        var path = this.getConfigFile();
        XMLConfigParser parser = null;
        try {
            parser = new XMLConfigParser(Files.newInputStream(path), this);
        } catch (IOException e) {
            //IOException can't be thrown.
            throw new RuntimeException(e);
        }
        return parser.parse();
    }

    public ResourceSystem getResourceSystem() {
        return resourceSystem;
    }
}
