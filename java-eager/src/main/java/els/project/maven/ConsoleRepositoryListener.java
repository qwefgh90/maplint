package els.project.maven;


import java.io.PrintStream;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

/**
 * A simplistic repository listener that logs events to the console.
 */
public class ConsoleRepositoryListener
        extends AbstractRepositoryListener
{

    private final Logger out= LoggerFactory.getLogger(ConsoleRepositoryListener.class);

    public void artifactDeployed( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Deployed " + event.getArtifact() + " to " + event.getRepository() );
    }

    public void artifactDeploying( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Deploying " + event.getArtifact() + " to " + event.getRepository() );
    }

    public void artifactDescriptorInvalid( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Invalid artifact descriptor for " + event.getArtifact() + ": "
                + event.getException().getMessage() );
    }

    public void artifactDescriptorMissing( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Missing artifact descriptor for " + event.getArtifact() );
    }

    public void artifactInstalled( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Installed " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactInstalling( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Installing " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactResolved( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Resolved artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactDownloading( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Downloading artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactDownloaded( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Downloaded artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactResolving( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Resolving artifact " + event.getArtifact() );
    }

    public void metadataDeployed( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Deployed " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataDeploying( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Deploying " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataInstalled( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Installed " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInstalling( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Installing " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInvalid( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Invalid metadata " + event.getMetadata() );
    }

    public void metadataResolved( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Resolved metadata " + event.getMetadata() + " from " + event.getRepository() );
    }

    public void metadataResolving( RepositoryEvent event )
    {
        requireNonNull( event, "event cannot be null" );
        out.trace( "Resolving metadata " + event.getMetadata() + " from " + event.getRepository() );
    }

}