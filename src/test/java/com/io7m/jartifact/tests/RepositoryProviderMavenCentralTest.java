package com.io7m.jartifact.tests;

import com.io7m.jartifact.core.Artifact;
import com.io7m.jartifact.core.Version;
import com.io7m.jartifact.http.HTTPData;
import com.io7m.jartifact.http.HTTPDefault;
import com.io7m.jartifact.http.HTTPException;
import com.io7m.jartifact.http.HTTPType;
import com.io7m.jartifact.maven_central.RepositoryProviderMavenCentral;
import com.io7m.jartifact.spi.RepositoryException;
import com.io7m.jartifact.spi.RepositoryProviderType;
import com.io7m.jartifact.spi.RepositoryRemoteArtifact;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

public final class RepositoryProviderMavenCentralTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(RepositoryProviderMavenCentralTest.class);

  @Test
  public void testAvailable()
    throws Exception
  {
    final HTTPType http =
      url -> HTTPData.create(-1L, "text/xml", resource("jnull-core.xml"));

    final RepositoryProviderType provider =
      RepositoryProviderMavenCentral.create(http);

    final List<Artifact> r =
      provider.artifactVersionsAvailable("com.io7m.jnull", "io7m-jnull-core");

    Assertions.assertAll(
      () -> Assertions.assertEquals(3, r.size()),
      () -> Assertions.assertEquals("io7m-jnull-core", r.get(0).artifact()),
      () -> Assertions.assertEquals("com.io7m.jnull", r.get(0).group()),
      () -> Assertions.assertEquals("io7m-jnull-core", r.get(1).artifact()),
      () -> Assertions.assertEquals("com.io7m.jnull", r.get(1).group()),
      () -> Assertions.assertEquals("io7m-jnull-core", r.get(2).artifact()),
      () -> Assertions.assertEquals("com.io7m.jnull", r.get(2).group()));
  }

  @Test
  public void testUnavailable()
    throws Exception
  {
    final HTTPType http =
      url -> HTTPData.create(-1L, "text/xml", resource("nonexistent.xml"));

    final RepositoryProviderType provider =
      RepositoryProviderMavenCentral.create(http);

    final List<Artifact> r =
      provider.artifactVersionsAvailable("com.io7m.nonexistent", "nonexistent");

    Assertions.assertEquals(0, r.size());
  }

  @Test
  public void testArtifact()
    throws Exception
  {
    final HTTPType http =
      new HTTPDefault();

    final RepositoryProviderType provider =
      RepositoryProviderMavenCentral.create(http);

    final RepositoryRemoteArtifact r =
      provider.artifactFile(
        Artifact.builder()
          .setVersion(Version.builder()
                        .setMajor(1)
                        .setMinor(0)
                        .setPatch(0)
                        .build())
          .setVersionRaw("1.0.0")
          .setGroup("com.io7m.jnull")
          .setArtifact("io7m-jnull-core")
          .setType("jar")
          .build());

    Assertions.assertEquals(7980L, r.size());
  }

  @Test
  public void testArtifactSignature()
    throws Exception
  {
    final HTTPType http =
      new HTTPDefault();

    final RepositoryProviderType provider =
      RepositoryProviderMavenCentral.create(http);

    final RepositoryRemoteArtifact r =
      provider.artifactFile(
        Artifact.builder()
          .setVersion(Version.builder()
                        .setMajor(1)
                        .setMinor(0)
                        .setPatch(0)
                        .build())
          .setVersionRaw("1.0.0")
          .setGroup("com.io7m.jnull")
          .setArtifact("io7m-jnull-core")
          .setType("jar.asc")
          .build());

    Assertions.assertEquals(801L, r.size());
  }

  @Test
  public void test404()
  {
    final HTTPType http = url -> {
      throw new HTTPException("404 MISSING", 404, "MISSING");
    };

    final RepositoryProviderType provider =
      RepositoryProviderMavenCentral.create(http);

    final RepositoryException ex = Assertions.assertThrows(
      RepositoryException.class,
      () -> {
        provider.artifactVersionsAvailable(
          "com.io7m.jnull",
          "io7m-jnull-core");
      });

    final HTTPException cause = (HTTPException) ex.getCause();
    Assertions.assertEquals(404, cause.responseCode());
  }

  private static InputStream resource(final String name)
  {
    return RepositoryProviderMavenCentralTest.class.getResourceAsStream(name);
  }
}
