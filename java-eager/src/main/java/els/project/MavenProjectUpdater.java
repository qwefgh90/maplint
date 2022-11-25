package els.project;

import org.apache.maven.model.building.ModelBuildingException;

public interface MavenProjectUpdater {
    void updateDependencies(MavenProject project) throws ModelBuildingException;
}
