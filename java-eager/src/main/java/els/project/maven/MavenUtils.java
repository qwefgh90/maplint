package els.project.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.*;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenUtils {
    protected static Logger logger = LoggerFactory.getLogger(MavenUtils.class);

    public static Set<Artifact> dependencies(Path workspace, Path projectPath) throws ModelBuildingException {
        File projectPomFile = projectPath.resolve("pom.xml").toAbsolutePath().toFile();

        var repositoryRoot = lookupLocalRepoDir().toPath();//workspace.resolve("local");
        logger.info("loading this sample project's Maven descriptor from {}\n", projectPomFile);
        logger.debug("local Maven repository set to {}\n",
                repositoryRoot);

        RepositorySystem repositorySystem = getRepositorySystem();
        RepositorySystemSession repositorySystemSession = getRepositorySystemSession(repositorySystem, repositoryRoot);

        final DefaultModelBuildingRequest modelBuildingRequest = new DefaultModelBuildingRequest()
                .setPomFile(projectPomFile);

        ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
        ModelBuildingResult modelBuildingResult = modelBuilder.build(modelBuildingRequest);

        Model model = modelBuildingResult.getEffectiveModel();
        logger.debug("Maven model resolved: {}, parsing its dependencies..\n", model);
        Set<Artifact> resolvedArtifacts = new HashSet<>();
        model.getDependencies().forEach((d) -> {
            logger.info("Processing a dependency: {}\n", d);
            try {
                Artifact artifact = new DefaultArtifact(d.getGroupId(), d.getArtifactId(), d.getType(), d.getVersion());
                var resolvedArtifact = resolveArtifact(repositorySystem, repositorySystemSession, artifact);
                resolvedArtifacts.add(resolvedArtifact);
                logger.info("{} -> {}", resolvedArtifact, resolvedArtifact.getFile());
                traverseDependencyTree(repositorySystem, repositorySystemSession, artifact, new DependencyVisitor() {
                    @Override
                    public boolean visitEnter(DependencyNode node) {
                        try {
                            var artifact = node.getArtifact();
                            Artifact resolvedArtifact = resolveArtifact(repositorySystem, repositorySystemSession, artifact);
                            resolvedArtifacts.add(resolvedArtifact);
                            logger.info("{} -> {}", resolvedArtifact, resolvedArtifact.getFile());
                        } catch (ArtifactResolutionException e) {
                            logger.error("error resolving artifact: {}\n", e.getMessage());
                        }
                        return true;
                    }
                    @Override
                    public boolean visitLeave(DependencyNode node) {
                        return true;
                    }
                });
            } catch (ArtifactResolutionException | DependencyCollectionException e) {
                logger.error("error resolving artifact: {}\n", e.getMessage());
            }
        });
        return resolvedArtifacts;
    }

    public static Artifact resolveArtifact(RepositorySystem repositorySystem, RepositorySystemSession repositorySystemSession, Artifact artifact) throws ArtifactResolutionException {
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(getRepositories(repositorySystem, repositorySystemSession));
        ArtifactResult artifactResult = repositorySystem.resolveArtifact(repositorySystemSession, artifactRequest);
        artifact = artifactResult.getArtifact();
        return artifact;
    }

    private static RepositorySystem getRepositorySystem() {
        DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator();
        serviceLocator
                .addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        serviceLocator.addService(TransporterFactory.class, FileTransporterFactory.class);
        serviceLocator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        serviceLocator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                logger.warn("error creating service: {}\n", exception.getMessage());
                exception.printStackTrace();
            }
        });

        return serviceLocator.getService(RepositorySystem.class);
    }

    /**
     * It traverses the dependency tree of the artifact
     * @param repositorySystem
     * @param repositorySystemSession
     * @param artifact
     * @param visitor
     * @throws DependencyCollectionException
     */
    private static void traverseDependencyTree(RepositorySystem repositorySystem, RepositorySystemSession repositorySystemSession, Artifact artifact, DependencyVisitor visitor) throws DependencyCollectionException {
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, ""));
        collectRequest.setRepositories(getRepositories(repositorySystem, repositorySystemSession));
        CollectResult collectResult = repositorySystem.collectDependencies(repositorySystemSession, collectRequest);
        collectResult.getRoot().accept(visitor);
    }

    private static DefaultRepositorySystemSession getRepositorySystemSession(RepositorySystem system, Path repositoryRoot) {
        final DefaultRepositorySystemSession repositorySystemSession =
                MavenRepositorySystemUtils.newSession();

        final LocalRepository localRepo = new LocalRepository(repositoryRoot.toFile());
        repositorySystemSession.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_DAILY);
        repositorySystemSession.setLocalRepositoryManager(
                system.newLocalRepositoryManager(repositorySystemSession, localRepo));
        repositorySystemSession.setRepositoryListener(new ConsoleRepositoryListener());

        return repositorySystemSession;
    }

    private static File lookupLocalRepoDir() {
        return new File(USER_MAVEN_CONFIGURATION_HOME, "repository");
    }

    private static final String USER_HOME = System.getProperty("user.home");

    private static final File USER_MAVEN_CONFIGURATION_HOME = new File(
            USER_HOME, ".m2");

    private static List<RemoteRepository> getRepositories(RepositorySystem system,
                                                          RepositorySystemSession session) {
        try {
            RemoteRepository local = new RemoteRepository.Builder("local", "default", USER_MAVEN_CONFIGURATION_HOME.toURI().toURL().toString()).build();
            return Arrays.asList(local, getCentralMavenRepository());
        } catch (MalformedURLException e) {
            return Arrays.asList(getCentralMavenRepository());
        }
    }

    private static RemoteRepository getCentralMavenRepository() {
        return new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build();
    }

}