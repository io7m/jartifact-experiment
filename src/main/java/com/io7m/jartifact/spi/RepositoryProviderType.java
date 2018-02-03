package com.io7m.jartifact.spi;

import com.io7m.jartifact.core.Artifact;

import java.util.List;

public interface RepositoryProviderType
{
  List<Artifact> artifactVersionsAvailable(
    String group,
    String artifact)
    throws RepositoryException;

  RepositoryRemoteArtifact artifactFile(
    Artifact artifact)
    throws RepositoryException;
}
