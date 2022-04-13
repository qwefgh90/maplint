package els.project;

import els.exception.JavaProjectInitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author qwefgh90
 */
public class MavenProjectFactory extends JavaProjectFactory implements MavenProjectUpdater {
    Logger logger = LoggerFactory.getLogger(MavenProjectFactory.class);
    public final Path pomXml;

    public MavenProjectFactory(Path root) {
        super(root);
        pomXml = root.resolve("pom.xml");
    }

    @Override
    public JavaProject create(Set<String> externalDependencies, Set<Path> classPath, Set<String> exports) throws JavaProjectInitializationError {
        var dependencies = mvnDependencies(pomXml, "dependency:list");
        var docs = mvnDependencies(pomXml, "dependency:sources");
        return new MavenProject(root, dependencies, docs, classPath, exports, externalDependencies, pomXml, this);
    }

    /**
     * TODO https://maven.apache.org/ref/3.8.4/maven-embedder/
     * @param pomXml
     * @param goal
     * @return
     */
    protected Set<Path> mvnDependencies(Path pomXml, String goal) {
        assert Files.exists(pomXml);
        try {
            // TODO consider using mvn valide dependency:copy-dependencies -DoutputDirectory=??? instead
            // Run maven as a subprocess
            String[] command = {
                    getMvnCommand(),
                    "--batch-mode", // Turns off ANSI control sequences
                    "validate",
                    goal,
                    "-DincludeScope=test",
                    "-DoutputAbsoluteArtifactFilename=true",
            };
            var output = Files.createTempFile("java-language-server-maven-output", ".txt");
            var errorOutput = Files.createTempFile("java-language-server-maven-error-output", ".txt");
            logger.debug("Execute {}", String.join(" ", command));
            logger.debug("Reading dependencies from {} ({})", pomXml.toAbsolutePath().toString(), String.join(" ", command));
            var workingDirectory = pomXml.toAbsolutePath().getParent().toFile();
            var process =
                    new ProcessBuilder()
                            .command(command)
                            .directory(workingDirectory)
                            .redirectError(ProcessBuilder.Redirect.INHERIT)
                            .redirectOutput(output.toFile())
                            .start();
            // Wait for process to exit
            var result = process.waitFor();

            if (result != 0) {
                logger.warn("the exit value is {}.\n the error is from {}.\n {}", result, output.toAbsolutePath(), Files.readString(output));
                return Set.of();
            }
            // Read output
            var dependencies = new HashSet<Path>();
            logger.debug("The dependency list is following.");
            for (var line : Files.readAllLines(output)) {
                var jar = readDependency(line);
                if (jar != NOT_FOUND) {
                    dependencies.add(jar);
                }
            }
            return dependencies;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final Pattern DEPENDENCY_PATTERN =
            Pattern.compile("^\\[INFO\\]\\s+(.*:.*:.*:.*:.*):((/|\\w:\\\\).*?)( -- module .*)?$");

    protected final Path NOT_FOUND = Paths.get("");

    protected Path readDependency(String line) {
        var match = DEPENDENCY_PATTERN.matcher(line);
        if (!match.matches()) {
            return NOT_FOUND;
        }
        var artifact = match.group(1);
        var path = match.group(2);
        logger.debug(String.format("=> %s (%s)", artifact, path));
        return Paths.get(path);
    }

    protected String getMvnCommand() {
        var mvnCommand = "mvn";
        if (File.separatorChar == '\\') {
            mvnCommand = findExecutableOnPath("mvn.cmd");
            if (mvnCommand == null) {
                mvnCommand = findExecutableOnPath("mvn.bat");
            }
        }
        return mvnCommand;
    }

    protected String findExecutableOnPath(String name) {
        for (var dirname : System.getenv("PATH").split(File.pathSeparator)) {
            var file = new File(dirname, name);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    @Override
    public void updateDependencies(MavenProject project) {
        var dependencies = mvnDependencies(pomXml, "dependency:list");
        var docs = mvnDependencies(pomXml, "dependency:sources");
        project.dependencies = dependencies;
        project.docs = docs;
    }
}
