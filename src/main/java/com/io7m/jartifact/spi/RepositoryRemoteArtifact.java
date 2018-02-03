package com.io7m.jartifact.spi;

import com.google.auto.value.AutoValue;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

@AutoValue
public abstract class RepositoryRemoteArtifact
{
  RepositoryRemoteArtifact()
  {

  }

  public abstract long size();

  public abstract String contentType();

  public abstract InputStream stream();

  public static RepositoryRemoteArtifact create(
    final long size,
    final String type,
    final InputStream stream)
  {
    return new AutoValue_RepositoryRemoteArtifact(size, type, stream);
  }
}
