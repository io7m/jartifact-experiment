package com.io7m.jartifact.core;

import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
public abstract class Artifact
{
  Artifact()
  {

  }

  @Override
  public final String toString()
  {
    return new StringBuilder(32)
      .append(this.group())
      .append(":")
      .append(this.artifact())
      .append(":")
      .append(this.type())
      .append(":")
      .append(this.version().map(Version::toString).orElse(this.versionRaw()))
      .toString();
  }

  public abstract String group();

  public abstract String artifact();

  public abstract String type();

  public abstract Optional<Version> version();

  public abstract String versionRaw();

  public static Builder builder()
  {
    final Builder builder = new AutoValue_Artifact.Builder();
    builder.setType("jar");
    return builder;
  }

  public static Builder builder(
    final String group,
    final String artifact,
    final Version version)
  {
    final Builder builder = new AutoValue_Artifact.Builder();
    builder.setGroup(group);
    builder.setArtifact(artifact);
    builder.setType("jar");
    builder.setVersion(version);
    builder.setVersionRaw(version.toString());
    return builder;
  }

  @AutoValue.Builder
  public static abstract class Builder
  {
    Builder()
    {

    }

    public abstract Builder setGroup(String g);

    public abstract Builder setArtifact(String a);

    public abstract Builder setVersion(final Version v);

    public abstract Builder setVersionRaw(final String t);

    public abstract Builder setType(String t);

    public abstract Artifact build();
  }
}
